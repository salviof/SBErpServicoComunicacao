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
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;

/**
 *
 * @author salvio
 */
public class ProcessadorEventoWhatsappPadrao extends ProcessadorWtzpEventoBaseAbstrato {

    public ProcessadorEventoWhatsappPadrao(EventoMensagemWtzap pEvento) throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {
        super(pEvento);

    }

    @Override
    public void processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento, ErroConexaoServicoChat {
        try {
            final String CONTATO_WP_ID = eventoWhatsapp.getWaIdContatoOrigem();
            Contato contato;
            try {
                contato = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato(CONTATO_WP_ID);
            } catch (ErroRegraDeNEgocioChat ex) {
                throw new ErroComDevolucaoMensagemUsuario("Falha de regra de negocio ao receber mensagem",
                        "A mensagem não foi entregue: " + ex.getMessage());
            }
            ItfUsuarioChat usuarioChatContato;
            try {
                usuarioChatContato = AplicacaoWsChat.SERVICO_MATRIX.gerarUsuarioContato(contato.getNome(), contato.getTelefone());
            } catch (ErroRegraDeNEgocioChat ex) {
                throw new ErroFalhaEncaminhando("Falha obtendo contato no sistema matrix");
            }

            MensagemTrOrigemMatrix mensagemOriginadaPeloMatrix = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getMensagemEnviadaPorMatrixByRegistroWhatsapp(eventoWhatsapp.getCodigoMensagem());
            ItfChatSalaBean sala = AplicacaoWsChat.SERVICO_MATRIX.getSalaByCodigo(mensagemOriginadaPeloMatrix.getSalaCodigoMatrix());
            String codigoEventoMatrix = mensagemOriginadaPeloMatrix.getCodigoReciboMensagemMatrix();
            switch (eventoWhatsapp.getTipoStatus()) {

                case FALHA_ENTREGA:
                    // notificar o atendente que houve falha na entrega da mensagem.
                    AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(sala, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(),
                            codigoEventoMatrix, "O Sistema falhou ao entregar a mensagem com o erro" + eventoWhatsapp.getDescricaoErro());

                    break;
                case MENSAGEM_ENTREGUE:
                    //marcar no matrix que a mensagem foi entregue

                    mensagemOriginadaPeloMatrix.setEncaminhado(true);
                    if (UtilSBPersistencia.mergeRegistro(mensagemOriginadaPeloMatrix) == null) {
                        throw new ErroFalhaEncaminhando("Falha persistindo historico de entrega de mensagem");
                    }

                    break;
                case MENSAGEM_ENVIADA:
                    //marcar no banco de dados, que a mensagem foi enviada
                    mensagemOriginadaPeloMatrix.setEncaminhado(true);
                    if (UtilSBPersistencia.mergeRegistro(mensagemOriginadaPeloMatrix) == null) {
                        throw new ErroFalhaEncaminhando("Falha persistindo historico de encaminhamento de mensagem");
                    }
                    break;
                case MENSAGEM_LIDA:
                    //marcar no banco de dados que a mensagem foi lida
                    if (AplicacaoWsChat.SERVICO_MATRIX.salaNotificarLeitura(sala, usuarioChatContato, codigoEventoMatrix)) {
                        mensagemOriginadaPeloMatrix.setLido(true);
                        if (UtilSBPersistencia.mergeRegistro(mensagemOriginadaPeloMatrix) == null) {
                            throw new ErroFalhaEncaminhando("Falha persistindo historico de entrega de mensagem");
                        }
                    } else {
                        throw new ErroFalhaEncaminhando("Falha notificando leitura de mensagem");
                    }
                    break;
                case MENSAGEM_EXCLUIDA:
                    //notificar o atendente na sala de atendimento que a mensagem foi excluida

                    AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(sala, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(),
                            codigoEventoMatrix, "Uma mensagem foi excluida:" + eventoWhatsapp.getDescricaoErro());
                    break;
                case DESCONHECIDO:
                    AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(sala, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(),
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
