/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.interpretadormsg.padrao.whatsapp;

import br.com.casanovadigital.servicos.notificacao.AplicacaoWsChat;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.interfaces.ItfProcessadorEventoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;

/**
 *
 * @author salvio
 */
public class ProcessadorEventoWhatsappPadrao extends ProcessadorWtzpPadrao implements ItfProcessadorEventoWhatsapp {

    private EventoMensagemWtzap eventoWhatsapp;

    public ProcessadorEventoWhatsappPadrao(EventoMensagemWtzap eventoWhatsapp) {

    }

    @Override
    protected void processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {

        ItfUsuarioChat usuarioChatContato = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(eventoWhatsapp.getEntrada(), eventoWhatsapp.getWaIdContatoOrigem());
        ItfUsuarioChat usuarioChatAtendimento = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(eventoWhatsapp.getEntrada(), eventoWhatsapp.getWaIdContatoOrigem());
        MensagemTrOrigemMatrix mensagemOriginadaPeloMatrix = AplicacaoWsChat.HISTORICO_MENSAGENS.getMensagemEnviadaPorMatrixByRegistroWhatsapp(eventoWhatsapp.getCodigoMensagem());
        String codigoEventoMatrix = mensagemOriginadaPeloMatrix.getCodigoReciboEntregaMatrix();
        switch (eventoWhatsapp.getTipoStatus()) {

            case FALHA_ENTREGA:
                // notificar o atendente que houve falha na entrega da mensagem.
                AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(pSala, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(), codigoEventoMatrix, "O Sistema falhou ao entregar a mensagem com o erro" + eventoWhatsapp.getDescricaoErro());

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
                if (AplicacaoWsChat.SERVICO_MATRIX.salaNotificarLeitura(pSala, usuarioChatContato, codigoEventoMatrix)) {
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
                AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(pSala, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(), codigoEventoMatrix, "Uma mensagem foi excluida:" + eventoWhatsapp.getDescricaoErro());
                break;
            case DESCONHECIDO:
                AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(pSala, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(), codigoEventoMatrix, "Evento desconhecido recebido:" + eventoWhatsapp.getDescricaoErro());

                break;
            default:
                throw new AssertionError();
        }

    }

    @Override
    public boolean isSucesso() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {
        aguardarProcessamento();
        return sucesso;
    }

}
