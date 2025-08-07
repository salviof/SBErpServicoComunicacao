/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.evento;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfProcessadorPacoteMatrixWhatsap;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;

/**
 *
 * @author salvio
 */
public class ProcessadorMtxEventoLeituraMatrix implements
        ItfProcessadorPacoteMatrixWhatsap {

    private ItfUsuarioChat atendente;
    private ItfEventoMatix evento;
    private ItfChatSalaBean sala;

    public ProcessadorMtxEventoLeituraMatrix(ItfEventoMatix pEvento, ItfChatSalaBean pSala, MensagemTrOrigemMatrix pMensagem, Contato pContato, ItfUsuarioChat pAtendente) {
        atendente = pAtendente;
        sala = pSala;
        evento = pEvento;
    }

    @Override
    public void processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento, ErroConexaoServicoChat {

        System.out.println(atendente.getNome() + " leu mensagens em " + sala.getNome() + " mas o whatsapp não suporta confirmação de leitura");
        //String codReciboEvento = pEventoSala.getContent().keys().next();
        //MensagemTrOrigemWhatsapp mensagemLeitura = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getMensagemEnviadaPorWhatsappByRegistroMatrix(codReciboEvento);
        //String codigoWhatsapp = mensagemLeitura.getCodigoRegistroMensagemWhatsapp();
    }

}
