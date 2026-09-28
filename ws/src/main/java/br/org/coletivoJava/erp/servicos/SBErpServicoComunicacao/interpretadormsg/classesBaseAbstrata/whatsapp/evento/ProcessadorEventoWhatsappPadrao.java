package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.whatsapp.evento;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.FabTipoStatusMensagemWhtzap.DESCONHECIDO;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.FabTipoStatusMensagemWhtzap.FALHA_ENTREGA;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.FabTipoStatusMensagemWhtzap.MENSAGEM_ENTREGUE;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.FabTipoStatusMensagemWhtzap.MENSAGEM_ENVIADA;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.FabTipoStatusMensagemWhtzap.MENSAGEM_EXCLUIDA;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.FabTipoStatusMensagemWhtzap.MENSAGEM_LIDA;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoUsuarioChat;
import br.org.coletivoJava.integracoes.matrixChat.FabApiRestIntMatrixChatSalas;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.EncaminhamentoMatrixParaWtzp;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.CarameloCode;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author salvio
 */
public class ProcessadorEventoWhatsappPadrao extends ProcessadorWtzpEventoBaseAbstrato {

    private static final String TAG_LOG = "[WTZP-STATUS]";

    /**
     * A Meta aceita o envio fora da janela de 24h e só avisa a falha depois,
     * pelo webhook de status - muitas vezes antes de o encaminhamento com o
     * recibo ser gravado (a gravação acontece após a definição da trilha, que
     * pode levar segundos). Sem registro, a falha espera em segundo plano até
     * ele aparecer, para o webhook responder 200 sem demora.
     */
    private static final int SEGUNDOS_MAXIMO_AGUARDANDO_REGISTRO = 90;
    private static final int SEGUNDOS_ENTRE_TENTATIVAS = 3;

    private static final ExecutorService EXECUTOR_FALHAS_SEM_REGISTRO = Executors.newFixedThreadPool(2, new ThreadFactory() {

        private final AtomicInteger sequencia = new AtomicInteger();

        @Override
        public Thread newThread(Runnable pTarefa) {
            Thread thread = new Thread(pTarefa, "falha-entrega-wtzp-" + sequencia.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    });

    /**
     * Recibos de mensagens do atendimento (Matrix) ainda sem garantia de
     * registro no banco. Só por eles vale esperar: mensagem automática de trilha
     * nunca ganha encaminhamento, e esperá-la ocuparia o executor à toa.
     */
    private static final Map<String, Long> RECIBOS_ENVIADOS_PELO_ATENDIMENTO = new ConcurrentHashMap<>();
    private static final long MILIS_VALIDADE_RECIBO_ENVIADO = 10 * 60 * 1000L;

    /**
     * Chamado logo após o WhatsApp devolver o recibo de uma mensagem do
     * atendimento, antes de o encaminhamento ser gravado.
     */
    public static void registrarReciboEnviadoPeloAtendimento(String pRecibo) {
        if (pRecibo == null) {
            return;
        }
        long agora = System.currentTimeMillis();
        RECIBOS_ENVIADOS_PELO_ATENDIMENTO.values().removeIf(registro -> agora - registro > MILIS_VALIDADE_RECIBO_ENVIADO);
        RECIBOS_ENVIADOS_PELO_ATENDIMENTO.put(pRecibo, agora);
    }

    public ProcessadorEventoWhatsappPadrao(EventoMensagemWtzap pEvento) {
        super(pEvento);

    }
    private EncaminhamentoMatrixParaWtzp mensagemRelacionada;

    /**
     * A instrumentação nunca pode interromper o processamento do status, por
     * isso o serviço de log é chamado dentro de um try.
     */
    private static void log(FabMensagens pTipo, String pMensagem) {
        try {
            CarameloCode.getServicoLogEventos().registrarLogDeEvento(pTipo, TAG_LOG + " " + pMensagem);
        } catch (Throwable t) {
            System.out.println(TAG_LOG + " " + pTipo + " " + pMensagem);
        }
    }

    @Override
    public EncaminhamentoMatrixParaWtzp getMensagemRelacionada() {
        return mensagemRelacionada;
    }

    private static EncaminhamentoMatrixParaWtzp buscarEncaminhamento(String pCodigoMensagemWhatsapp) {
        return AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT
                .getMensagemEnviadaPorMatrixByRegistroWhatsapp(pCodigoMensagemWhatsapp);
    }

    /**
     * Avisa na sala de atendimento que a mensagem não chegou ao contato.
     *
     * @return true se o Matrix aceitou o aviso
     */
    private static boolean notificarFalhaNaSala(EncaminhamentoMatrixParaWtzp pEncaminhamento, EventoMensagemWtzap pEvento) {
        String codigoSala = null;
        try {
            codigoSala = pEncaminhamento.getMensagem().getSalaCodigoMatrix();
            String nomeContato = pEncaminhamento.getContato() == null ? "o contato" : pEncaminhamento.getContato().getNome();
            String aviso = "⚠️ A mensagem NÃO foi entregue para " + nomeContato + ". " + pEvento.getDescricaoErro();
            ItfRespostaWebServiceSimples resp = FabApiRestIntMatrixChatSalas.SALA_ENVIAR_MENSAGEM_TEXTO_SIMPLES
                    .getAcao(codigoSala, pEncaminhamento.getId().toString() + "fail", aviso).getResposta();
            boolean sucesso = resp != null && resp.isSucesso();
            log(sucesso ? FabMensagens.AVISO : FabMensagens.ERRO, "Aviso de falha de entrega "
                    + (sucesso ? "enviado" : "RECUSADO pelo Matrix")
                    + ". sala=" + codigoSala
                    + " recibo=" + pEvento.getCodigoMensagem()
                    + " codigoErro=" + pEvento.getCodigoErro()
                    + (sucesso ? "" : " resposta=" + (resp == null ? "null" : resp.getCodigoResposta() + " " + resp.getRespostaTexto())));
            return sucesso;
        } catch (Throwable t) {
            log(FabMensagens.ERRO, "Falha avisando a sala " + codigoSala + " sobre a falha de entrega do recibo "
                    + pEvento.getCodigoMensagem() + ": " + t.getClass().getSimpleName() + ": " + t.getMessage());
            return false;
        }
    }

    private static void aguardarRegistroENotificarFalha(EventoMensagemWtzap pEvento) {
        final String recibo = pEvento.getCodigoMensagem();
        log(FabMensagens.AVISO, "Falha de entrega recebida antes do registro do encaminhamento; aguardando até "
                + SEGUNDOS_MAXIMO_AGUARDANDO_REGISTRO + "s. recibo=" + recibo
                + " waid=" + pEvento.getWaIdContatoDestinatario()
                + " codigoErro=" + pEvento.getCodigoErro());
        try {
            EXECUTOR_FALHAS_SEM_REGISTRO.submit(() -> {
                try {
                    long limite = System.currentTimeMillis() + SEGUNDOS_MAXIMO_AGUARDANDO_REGISTRO * 1000L;
                    while (System.currentTimeMillis() < limite) {
                        Thread.sleep(SEGUNDOS_ENTRE_TENTATIVAS * 1000L);
                        EncaminhamentoMatrixParaWtzp encaminhamento = buscarEncaminhamento(recibo);
                        if (encaminhamento != null) {
                            RECIBOS_ENVIADOS_PELO_ATENDIMENTO.remove(recibo);
                            notificarFalhaNaSala(encaminhamento, pEvento);
                            return;
                        }
                    }
                    log(FabMensagens.ERRO, "Falha de entrega NÃO avisada ao atendimento: nenhum encaminhamento"
                            + " registrado com o recibo " + recibo + " após " + SEGUNDOS_MAXIMO_AGUARDANDO_REGISTRO + "s."
                            + " waid=" + pEvento.getWaIdContatoDestinatario()
                            + " erro=" + pEvento.getDescricaoErro());
                } catch (Throwable t) {
                    log(FabMensagens.ERRO, "Falha aguardando registro do recibo " + recibo + ": "
                            + t.getClass().getSimpleName() + ": " + t.getMessage());
                }
            });
        } catch (Throwable t) {
            log(FabMensagens.ERRO, "Falha agendando aviso de falha de entrega do recibo " + recibo + ": "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    @Override
    public void processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento, ErroConexaoServicoChat {
        try {
            mensagemRelacionada = buscarEncaminhamento(eventoWhatsapp.getCodigoMensagem());

            if (eventoWhatsapp.getTipoStatus() == FALHA_ENTREGA) {
                if (mensagemRelacionada == null) {
                    if (RECIBOS_ENVIADOS_PELO_ATENDIMENTO.containsKey(eventoWhatsapp.getCodigoMensagem())) {
                        aguardarRegistroENotificarFalha(eventoWhatsapp);
                    } else {
                        log(FabMensagens.AVISO, "Falha de entrega de mensagem automática (sem sala de atendimento)."
                                + " recibo=" + eventoWhatsapp.getCodigoMensagem()
                                + " waid=" + eventoWhatsapp.getWaIdContatoDestinatario()
                                + " erro=" + eventoWhatsapp.getDescricaoErro());
                    }
                } else {
                    notificarFalhaNaSala(mensagemRelacionada, eventoWhatsapp);
                }
                return;
            }

            if (mensagemRelacionada == null) {
                // Status de mensagem que não saiu do Matrix (menus, avisos das trilhas).
                return;
            }

            String codigoEventoMatrix = mensagemRelacionada.getMensagem().getCodigoReciboMensagemMatrix();
            switch (eventoWhatsapp.getTipoStatus()) {

                case MENSAGEM_ENTREGUE:
                    //marcar no matrix que a mensagem foi entregue

                    mensagemRelacionada.setFoiEntregueNoCelularDoContato(true);

                    if (UtilSBPersistencia.mergeRegistro(mensagemRelacionada) == null) {
                        throw new ErroFalhaEncaminhando("Falha persistindo historico de entrega de mensagem");
                    }

                    break;
                case MENSAGEM_ENVIADA:
                    //marcar no banco de dados, que a mensagem foi enviada
                    mensagemRelacionada.setFoiEnviadoPeloWhatsapp(true);
                    if (UtilSBPersistencia.mergeRegistro(mensagemRelacionada) == null) {
                        throw new ErroFalhaEncaminhando("Falha persistindo historico de encaminhamento de mensagem");
                    }
                    break;
                case MENSAGEM_LIDA:
                    //marcar no banco de dados que a mensagem foi lida]
                    ComoUsuarioChat usuarioChatContato = getUsuarioChatContato();
                    if (AplicacaoWsChat.SERVICO_MATRIX.salaNotificarLeitura(mensagemRelacionada.getMensagem().getSalaCodigoMatrix(), usuarioChatContato, codigoEventoMatrix)) {

                        if (UtilSBPersistencia.mergeRegistro(mensagemRelacionada) == null) {
                            throw new ErroFalhaEncaminhando("Falha persistindo historico de entrega de mensagem");
                        }
                    } else {
                        throw new ErroFalhaEncaminhando("Falha notificando leitura de mensagem");
                    }
                    break;
                case MENSAGEM_EXCLUIDA:
                    //notificar o atendente na sala de atendimento que a mensagem foi excluida
                    ComoChatSalaBean salaexclusao = AplicacaoWsChat.SERVICO_MATRIX.getSalaByCodigo(mensagemRelacionada.getMensagem().getSalaCodigoMatrix());
                    AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(salaexclusao, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(),
                            codigoEventoMatrix, "Uma mensagem foi excluida:" + eventoWhatsapp.getDescricaoErro());
                    break;
                case DESCONHECIDO:
                    ComoChatSalaBean salaDesconhecido = AplicacaoWsChat.SERVICO_MATRIX.getSalaByCodigo(mensagemRelacionada.getMensagem().getSalaCodigoMatrix());
                    AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(salaDesconhecido, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(),
                            codigoEventoMatrix, "Evento desconhecido recebido:" + eventoWhatsapp.getDescricaoErro());

                    break;
                default:
                    throw new AssertionError();
            }
        } catch (ErroConexaoServicoChat ex) {
            throw new ErroFalhaEncaminhando("Serviço Matrix indisponível");
        }

    }

    private ComoUsuarioChat getUsuarioChatContato() throws ErroFalhaEncaminhando, ErroConexaoServicoChat {
        final String CONTATO_WP_ID = eventoWhatsapp.getWaIdContatoDestinatario();
        Contato contato;
        try {
            contato = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato(CONTATO_WP_ID);
        } catch (ErroRegraDeNEgocioChat ex) {
            throw new ErroFalhaEncaminhando("Falha de regra de negocio obtendo contato " + CONTATO_WP_ID + ": " + ex.getMessage());
        }
        if (contato == null) {
            throw new ErroFalhaEncaminhando("Contato " + CONTATO_WP_ID + " não encontrado");
        }
        return AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(contato.getMatrixID());
    }

}
