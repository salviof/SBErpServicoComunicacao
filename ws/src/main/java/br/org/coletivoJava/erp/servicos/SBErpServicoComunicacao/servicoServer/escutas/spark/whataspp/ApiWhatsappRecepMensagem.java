package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.whataspp;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.whatsapp.evento.ProcessadorEventoWhatsappPadrao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.whatsapp.mensagem.ProcessadorWtzpMsg;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.entregaPendente.AvisoDeEsperaContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.entregaPendente.FilaDeEntregaPendente;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfProcessadorEventoWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.salas.ServicoSalaSobDemanda;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoClient.UtilServicoAdministrativo;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import javax.persistence.EntityManager;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfProcessadorMensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.RotaPadraoWtzp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.integracoes.matrixChat.FabApiRestIntMatrixChatSalas;
import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import br.org.coletivoJava.integracoes.whatsapp.config.FabConfigApiWhatsapp;
import com.super_bits.modulosSB.SBCore.ConfigGeral.CarameloCode;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.json.ErroProcessandoJson;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Path;
import spark.Request;

/**
 *
 * @author salvio
 */
@Path("/api/v1/whatsapp/recepcao/evento")
public class ApiWhatsappRecepMensagem extends RotaPadraoWtzp {

    private static final String HEADER_ASSINATURA = "X-Hub-Signature-256";
    private static final String PREFIXO = "sha256=";
    private static final String ALGORITMO = "HmacSHA256";

    private static final String TAG_LOG = "[WTZP-RECEP]";

    /**
     * A instrumentação nunca pode interromper a recepção do pacote, por isso o
     * serviço de log é chamado dentro de um try.
     */
    private void log(FabMensagens pTipo, String pMensagem) {
        try {
            CarameloCode.getServicoLogEventos().registrarLogDeEvento(pTipo, TAG_LOG + " " + pMensagem);
        } catch (Throwable t) {
            System.out.println(TAG_LOG + " " + pTipo + " " + pMensagem);
        }
    }

    private static String paraHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private static String calcularHmacHex(byte[] dados, String segredo)
            throws ErroParamentosInvalidos {
        try {
            Mac mac = Mac.getInstance(ALGORITMO);
            mac.init(new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), ALGORITMO));
            return paraHex(mac.doFinal(dados));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ErroParamentosInvalidos("Falha ao calcular assinatura: " + e.getMessage());
        }
    }

    /**
     * Lê o App Secret da Meta a partir da configuração. Ausência é erro de
     * configuração do servidor, não erro do cliente: lança unchecked para virar
     * 500 e não 400.
     */
    private static String obterAppSecret() {
        String appSecret = SBCore.getConfigModulo(FabConfigApiWhatsapp.class)
                .getPropriedade(FabConfigApiWhatsapp.CHAVE_APP_ASSINATURA_MSG);
        if (appSecret == null || appSecret.trim().isEmpty()) {
            throw new IllegalStateException(
                    "App Secret do WhatsApp não configurado (CHAVE_APP_ASSINATURA_MSG)");
        }
        return appSecret;
    }

    @Override
    public void validarParamentros(Request requisicao) throws ErroParamentosInvalidos {

        if (!SBCore.isEmModoProducao() || true) {
            System.out.println(requisicao.body());
        }
        try {
            String assinaturaRecebida = requisicao.headers(HEADER_ASSINATURA);
            if (assinaturaRecebida == null || !assinaturaRecebida.startsWith(PREFIXO)) {
                throw new ErroParamentosInvalidos("Assinatura ausente ou malformada");
            }

            byte[] corpo = requisicao.bodyAsBytes();
            if (corpo == null || corpo.length == 0) {
                throw new ErroParamentosInvalidos("Corpo vazio");
            }

            String assinaturaEsperada = PREFIXO + calcularHmacHex(corpo, obterAppSecret());

            if (!MessageDigest.isEqual(
                    assinaturaEsperada.getBytes(StandardCharsets.UTF_8),
                    assinaturaRecebida.getBytes(StandardCharsets.UTF_8))) {
                throw new ErroParamentosInvalidos("Assinatura inválida");
            }
        } catch (Throwable t) {
            CarameloCode.getServicoLogEventos().registrarLogDeEvento(FabMensagens.ERRO, "Falha validando assinatura whatsapp" + t.getMessage());
            throw new ErroParamentosInvalidos("Erro validando assinatura do whatsapp" + t.getMessage());
        }

    }

    @Override
    public String executarRegraDeNegocio(String pCorpo) throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
        try {
            return processar(new PacoteMemensagemRecebidoWhatsapp(pCorpo));
        } catch (ErroProcessandoJson ex) {
            throw new ErroRegraDeNegocio("Json enviado inválido");
        }
    }

    /**
     * Registro de trânsito da mensagem: reaproveita o que já existe para o
     * mesmo id do WhatsApp, para a reentrega não criar linha nova.
     */
    private MensagemTrOrigemWhatsapp prepararRegistro(MensagemTrOrigemWhatsapp pRegistroAnterior,
            MensagemWhatsapp pMensagem, PacoteMemensagemRecebidoWhatsapp pPacote) {

        MensagemTrOrigemWhatsapp registro = pRegistroAnterior;
        if (registro == null) {
            registro = new MensagemTrOrigemWhatsapp();
        }
        registro.setCorpoJsonRecebido(UtilCRCJson.getTextoByJsonObjeect(pPacote.getDadosJson()));
        registro.setRegistrado(true);
        registro.setCodigoRegistroMensagemWhatsapp(pMensagem.getId());
        return registro;
    }

    /**
     * Distingue "a sala ainda está sendo criada" de "falhou de verdade".
     *
     * A pergunta é feita ao {@link ServicoSalaSobDemanda}, e não ao tipo da
     * exceção, porque as trilhas convertem qualquer falha em
     * ErroComDevolucaoMensagemUsuario antes de a exceção chegar aqui.
     *
     * Sendo espera: a mensagem entra na fila, o contato é avisado uma única vez
     * e a requisição termina em 200 - para a Meta não reentregar o pacote e
     * repetir a resposta.
     *
     * @return true se a mensagem foi enfileirada
     */
    private boolean tratarComoEsperaDeSala(MensagemTrOrigemWhatsapp pRegistro, MensagemWhatsapp pMensagem,
            String pMotivo) {

        String waid = pMensagem.getContatoOrigem().getWa_id();
        if (!ServicoSalaSobDemanda.isSalaEmCriacaoParaContato(waid)) {
            return false;
        }
        boolean contatoJaAguardava = FilaDeEntregaPendente.isContatoAguardando(waid);

        // O merge fica para o finally da rota, que persiste o registro em
        // qualquer caminho.
        FilaDeEntregaPendente.enfileirar(pRegistro, pMensagem);

        if (!contatoJaAguardava) {
            AvisoDeEsperaContato.avisarEspera(pMensagem);
        }
        log(FabMensagens.AVISO, "Sala do contato " + waid + " ainda em criação; mensagem "
                + pMensagem.getId() + " foi para a fila e a requisição responde sucesso."
                + " avisoDeEsperaEnviado=" + !contatoJaAguardava
                + " motivo=" + pMotivo);
        return true;
    }

    public String processar(PacoteMemensagemRecebidoWhatsapp pPacote) throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro, ErroProcessandoJson {
        PacoteMemensagemRecebidoWhatsapp pacoteMensagemWtzp = pPacote;
        /// ATENÇÃO COM leituras incoerentes, conflitos ou deadlocks dos registros de banco de dados
        /// POIS VÁRIAS THREADS PODEM ESTAR RODANDO AO MESMO TEMPO,
        /// A GESTÃO DE CONCORRENCIA DAS TRANSAÇÕES do JPA NÃO LIDARÁ BEM COM A MANIPULÇÃO
        /// (esta instância é compartilhada entre threads do Spark: todo estado deve ser local)

        for (MensagemWhatsapp msgWtsap : pacoteMensagemWtzp.getMensagens()) {

            // A Meta reentrega o mesmo pacote enquanto não receber 200, e a resposta ao
            // contato (menu, link, encaminhamento) é enviada antes das etapas que podem
            // falhar - como aguardar a criação da sala do contato. Sem esta guarda cada
            // reentrega repete a resposta, e o contato recebe o mesmo menu várias vezes.
            MensagemTrOrigemWhatsapp registroAnterior = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getMensagemEnviadaPorWhatsappByRegistrWhatsapp(msgWtsap.getId());
            if (registroAnterior != null && registroAnterior.isEncaminhado()) {
                log(FabMensagens.AVISO, "Mensagem " + msgWtsap.getId() + " de "
                        + msgWtsap.getContatoOrigem().getWa_id()
                        + " já havia sido respondida; reentrega descartada para não repetir a resposta ao contato.");
                continue;
            }

            final String waidContato = msgWtsap.getContatoOrigem().getWa_id();

            // O contato já tem mensagem na fila esperando a sala: esta entra na fila
            // atrás dela, sem processar e sem repetir o aviso. Processar agora faria a
            // mensagem nova chegar na sala antes da anterior.
            if (FilaDeEntregaPendente.isContatoAguardando(waidContato)) {
                MensagemTrOrigemWhatsapp registroNaFila = prepararRegistro(registroAnterior, msgWtsap, pPacote);
                FilaDeEntregaPendente.enfileirar(registroNaFila, msgWtsap);
                UtilSBPersistencia.mergeRegistro(registroNaFila);
                log(FabMensagens.AVISO, "Mensagem " + msgWtsap.getId() + " de " + waidContato
                        + " entrou na fila atrás das anteriores; o contato já foi avisado da espera.");
                continue;
            }

            log(FabMensagens.AVISO, "Processando mensagem " + msgWtsap.getId()
                    + " tipo=" + msgWtsap.getTipoMensagem()
                    + " de=" + waidContato
                    + " entrada=" + msgWtsap.getEntrada().getCodigo()
                    + (registroAnterior == null ? "" : " (reentrega de mensagem que ainda não foi respondida)"));

            EntityManager em = UtilSBPersistencia.getEMPadraoNovo();
            try {
                UtilSBPersistencia.iniciarTransacao(em);

                MensagemTrOrigemWhatsapp logTransidoDeMensagem = prepararRegistro(registroAnterior, msgWtsap, pPacote);

                boolean logPersistido = false;
                try {
                    ItfProcessadorMensagemWhatsapp processador = new ProcessadorWtzpMsg(msgWtsap, logTransidoDeMensagem);
                    try {
                        processador.processar();
                    } catch (ErroConexaoServicoChat ex) {
                        throw new ErroComDevolucaoMensagemUsuario("Erro de conexão com serviço chat" + ex.getMessage(), "Erro conectando com serviço de entrega, entre em contato com o administrador");
                    } catch (RuntimeException falhaNaoPrevista) {
                        // Rede de segurança: qualquer exceção não declarada de
                        // qualquer trilha vira devolução ao contato. Antes ela
                        // subia até o handler do Spark, virava 500 e o contato
                        // ficava sem resposta nenhuma.
                        log(FabMensagens.ERRO, "Falha não prevista processando a mensagem " + msgWtsap.getId()
                                + " de " + msgWtsap.getContatoOrigem().getWa_id() + ". erro="
                                + falhaNaoPrevista.getClass().getName() + ": " + falhaNaoPrevista.getMessage());
                        UtilServicoAdministrativo.notificarAdmiministrador("ATENÇÃO! FALHA NÃO PREVISTA PROCESSANDO PACOTE "
                                + falhaNaoPrevista.getClass().getName() + ":" + falhaNaoPrevista.getMessage()
                                + "PAYLOAD:" + pPacote.getDadosJson());
                        throw new ErroComDevolucaoMensagemUsuario("Falha não prevista: "
                                + falhaNaoPrevista.getClass().getName() + ": " + falhaNaoPrevista.getMessage(),
                                "Sua mensagem não foi entregue por uma falha no nosso sistema."
                                + " Nossa equipe já foi avisada. Tente novamente em alguns minutos"
                                + " ou ligue para este mesmo número.");
                    }
                } catch (ErroComDevolucaoMensagemUsuario ex) {

                    // As trilhas convertem qualquer falha em ErroComDevolucaoMensagemUsuario,
                    // então é aqui que a espera pela sala normalmente cai.
                    if (tratarComoEsperaDeSala(logTransidoDeMensagem, msgWtsap, ex.getMessage())) {
                        continue;
                    }

                    String retorno = null;
                    try {
                        retorno = AplicacaoWsChat.SERVICO_WHATSAPP.enviarMensagemTexto(msgWtsap.getEntrada(), msgWtsap.getContatoOrigem().getWa_id(), ex.getMensagemRetorno());
                    } catch (ErroConexaoServicoChat ex1) {
                        throw new ErroConexaoSistemaTerceiro("Falha retornando mensagem de erro para o usuário, o pacote foi recusado");
                    }
                    if (retorno == null) {
                        throw new ErroConexaoSistemaTerceiro("Falha retornando mensagem de erro para o usuário, o pacote foi recusado");
                    }
                    // O contato foi avisado do erro: o ciclo desta mensagem está
                    // encerrado. Sem esta marca, uma reentrega repetiria o aviso e o
                    // dreno ainda tentaria entregá-la de novo.
                    logTransidoDeMensagem.setEncaminhado(true);
                    // Sem "continue": cai no finally e depois na checagem de persistência.
                } catch (ErroFalhaEncaminhando | ErroFalhaGerandoSalaAtendimento | ErroFalhaGerandoUsuarioAtendimento | AssertionError t) {

                    if (tratarComoEsperaDeSala(logTransidoDeMensagem, msgWtsap, t.getMessage())) {
                        continue;
                    }

                    // TODO IMPLEMENTAR NOTIFICAÇÃO DE ERRO
                    log(FabMensagens.ERRO, "Falha processando a mensagem " + msgWtsap.getId()
                            + " de " + msgWtsap.getContatoOrigem().getWa_id()
                            + ". A requisição será respondida com falha e a Meta pode reentregar o pacote."
                            + " respostaJaEnviadaAoContato=" + logTransidoDeMensagem.isEncaminhado()
                            + " erro=" + t.getClass().getSimpleName() + ": " + t.getMessage());
                    UtilServicoAdministrativo.notificarAdmiministrador("ATENÇÃO! FALHA PROCESSANDO PACOTE " + t.getClass().getSimpleName() + ":" + t.getMessage() + "PAYLOAD:" + pPacote.getDadosJson());
                    throw new ErroConexaoSistemaTerceiro("falha processando mensagem vinda do whatsapp " + t.getMessage());
                } finally {
                    // Nunca lançar daqui: um throw em finally descarta a exceção
                    // original que está subindo. Só registra o resultado.
                    logTransidoDeMensagem = UtilSBPersistencia.mergeRegistro(logTransidoDeMensagem);
                    logPersistido = (logTransidoDeMensagem != null);
                    if (!logPersistido) {
                        UtilServicoAdministrativo.notificarAdmiministrador(
                                "Falha persistindo registro da mensagem whatsapp " + msgWtsap.getId());
                    }
                }

                // Só chega aqui no caminho sem exceção (ou após devolução ao usuário).
                // Nos caminhos com exceção, a original propaga intacta.
                if (!logPersistido) {
                    throw new ErroConexaoSistemaTerceiro("Falha persistindo mensagem no repositório");
                }
            } finally {
                UtilSBPersistencia.finzalizaTransacaoEFechaEM(em);
            }
        }

        for (EventoMensagemWtzap evento : pacoteMensagemWtzp.getStatusMensagem()) {
            ItfProcessadorEventoWhatsapp processadorEvento = new ProcessadorEventoWhatsappPadrao(evento);
            // A falha de entrega é avisada na sala pelo próprio processador. Aqui só
            // se registra o descarte: um status com problema não pode derrubar os
            // demais do pacote nem fazer a Meta reentregar tudo.
            try {
                processadorEvento.processar();
            } catch (Throwable t) {
                log(FabMensagens.ERRO, "Status " + evento.getTipoStatus() + " do recibo " + evento.getCodigoMensagem()
                        + " (waid=" + evento.getWaIdContatoDestinatario() + ") foi descartado: "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
                UtilServicoAdministrativo.notificarAdmiministrador("Falha processando evento, o evento foi ignorado" + t.getMessage());
            }
        }
        return "OK";
    }
}
