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
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.ItfResposta;

/**
 *
 * @author salvio
 */
public class ProcessadorEventoWhatsappPadrao extends ProcessadorWtzpEventoBaseAbstrato {

    public ProcessadorEventoWhatsappPadrao(EventoMensagemWtzap pEvento) {
        super(pEvento);

    }
    private EncaminhamentoMatrixParaWtzp mensagemRelacionada;

    @Override
    public EncaminhamentoMatrixParaWtzp getMensagemRelacionada() {
        return mensagemRelacionada;
    }

    @Override
    public void processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento, ErroConexaoServicoChat {
        try {
            final String CONTATO_WP_ID = eventoWhatsapp.getWaIdContatoDestinatario();

            mensagemRelacionada = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT
                    .getMensagemEnviadaPorMatrixByRegistroWhatsapp(eventoWhatsapp.getCodigoMensagem());
            if (mensagemRelacionada == null) {
                return;
            }
            Contato contato;
            try {
                contato = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato(CONTATO_WP_ID);
            } catch (ErroRegraDeNEgocioChat ex) {
                throw new ErroComDevolucaoMensagemUsuario("Falha de regra de negocio ao receber mensagem",
                        "A mensagem não foi entregue: " + ex.getMessage());
            }
            ComoUsuarioChat usuarioChatContato;
            try {
                if (contato != null) {
                    usuarioChatContato = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(contato.getMatrixID());
                } else {
                    usuarioChatContato = AplicacaoWsChat.SERVICO_MATRIX.gerarUsuarioContato(contato.getNome(), contato.getTelefone());
                }

            } catch (ErroRegraDeNEgocioChat ex) {
                throw new ErroFalhaEncaminhando("Falha obtendo contato no sistema matrix");
            }

            String codigoEventoMatrix = mensagemRelacionada.getMensagem().getCodigoReciboMensagemMatrix();
            switch (eventoWhatsapp.getTipoStatus()) {

                case FALHA_ENTREGA:
                    // notificar o atendente que houve falha na entrega da mensagem.

                    throw new ErroComDevolucaoMensagemUsuario("Falha enviando mensagem", "O Sistema falhou ao entregar a mensagem para " + mensagemRelacionada.getContato().getNome() + "com o erro: "
                            + eventoWhatsapp.getDescricaoErro());

                //  AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getTrilha(pEntrada, contato, pMensagem);
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

}
