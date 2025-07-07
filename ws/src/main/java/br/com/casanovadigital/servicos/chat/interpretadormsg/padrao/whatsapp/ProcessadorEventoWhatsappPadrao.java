/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.interpretadormsg.padrao.whatsapp;

import br.com.casanovadigital.servicos.chat.AplicacaoWsChat;
import br.com.casanovadigital.servicos.chat.interpretadormsg.interfaces.ItfProcessadorEventoWhatsapp;
import br.com.casanovadigital.servicos.chat.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author salvio
 */
public class ProcessadorEventoWhatsappPadrao extends ProcessadorWtzp implements ItfProcessadorEventoWhatsapp {

    private EventoMensagemWtzap eventoWhatsapp;

    public ProcessadorEventoWhatsappPadrao(EventoMensagemWtzap eventoWhatsapp) throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {
        super();
    }

    @Override
    protected boolean processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {
        try {
            final String CONTATO_WP_ID = eventoWhatsapp.getWaIdContatoOrigem();
            Contato contato = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContatoByWhatsapID(CONTATO_WP_ID);
            ItfUsuarioChat usuarioChatContato;
            try {
                usuarioChatContato = AplicacaoWsChat.SERVICO_MATRIX.gerarUsuarioContato(contato.getNome(), contato.getTelefone());
            } catch (ErroRegraDeNEgocioChat ex) {
                throw new ErroFalhaEncaminhando("Falha obtendo contato no sistema matrix");
            }

            MensagemTrOrigemMatrix mensagemOriginadaPeloMatrix = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getMensagemEnviadaPorMatrixByRegistroWhatsapp(eventoWhatsapp.getCodigoMensagem());
            ItfChatSalaBean sala = AplicacaoWsChat.SERVICO_MATRIX.getSalaByCodigo(mensagemOriginadaPeloMatrix.getSalaCodigoMatrix());
            String codigoEventoMatrix = mensagemOriginadaPeloMatrix.getCodigoReciboEntregaMatrix();
            switch (eventoWhatsapp.getTipoStatus()) {

                case FALHA_ENTREGA:
                    // notificar o atendente que houve falha na entrega da mensagem.
                    AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(sala, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(), codigoEventoMatrix, "O Sistema falhou ao entregar a mensagem com o erro" + eventoWhatsapp.getDescricaoErro());

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

                    AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(sala, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(), codigoEventoMatrix, "Uma mensagem foi excluida:" + eventoWhatsapp.getDescricaoErro());
                    break;
                case DESCONHECIDO:
                    AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(sala, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(), codigoEventoMatrix, "Evento desconhecido recebido:" + eventoWhatsapp.getDescricaoErro());

                    break;
                default:
                    throw new AssertionError();
            }
        } catch (ErroConexaoServicoChat ex) {
            throw new ErroFalhaEncaminhando("Serviço Matrix indisponível");
        }
        return true;
    }

    @Override
    public boolean isSucesso() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {

        return sucesso;
    }

}
