/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.evento.ProcessadorMtxEventoDigitanto;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.evento.ProcessadorMtxEventoLeituraMatrix;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.mensagem.ProcessadorMtxMensagem;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.mensagem.ProcessadorMtxReacaoMensagem;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfProcessadorPacoteMatrixWhatsap;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoUsuarioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.FabTipoPacoteDeAcaoMatrix;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Atendente;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTransito;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;

/**
 *
 * @author salvio
 */
public class FabTipoProcessamentoMatrix {

    public static ItfProcessadorPacoteMatrixWhatsap getProcessadorMatrix(FabTipoPacoteDeAcaoMatrix pTipo, ItfEventoMatix pEvento, ComoChatSalaBean pSala, MensagemTransito pMensagem, Contato pContato, ComoUsuarioChat pAtendente) {
        switch (pTipo) {
            case MENSAGEM:
                return new ProcessadorMtxMensagem(pEvento, pSala, (MensagemTrOrigemMatrix) pMensagem, pContato, pAtendente);

            case DIGITANDO:
                return new ProcessadorMtxEventoDigitanto(pEvento, pSala, (MensagemTrOrigemMatrix) pMensagem, pContato, pAtendente);

            case LEITURA:
                return new ProcessadorMtxEventoLeituraMatrix(pEvento, pSala, (MensagemTrOrigemMatrix) pMensagem, pContato, pAtendente);

            case REACAO:
                return new ProcessadorMtxReacaoMensagem(pEvento, pSala, (MensagemTrOrigemWhatsapp) pMensagem, pContato, pAtendente);

            default:
                throw new AssertionError();
        }
    }

}
