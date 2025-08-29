/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos.ListenerSalaMatrix;
import br.org.coletivoJava.fw.api.erp.chat.ErroMtxParalizacaoDeProcessamento;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;

/**
 *
 * @author salvio
 */
public class ListenerSalaMatrixTestesLive extends ListenerSalaMatrix {

    public ListenerSalaMatrixTestesLive(ItfChatSalaBean pSala) {
        super(pSala);
    }

    @Override
    public synchronized boolean isElegivel(ItfEventoMatix pEvento) {
        return true;

    }

    @Override
    public synchronized void processarEvento(ItfEventoMatix pEvento) throws ErroMtxParalizacaoDeProcessamento {
        super.processarEvento(pEvento); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public boolean isSalaComAutoMonitoramento(String pNomeSAla) {
        if (pNomeSAla == null) {
            return false;
        }
        if (pNomeSAla.equals("#5531984178550wc:casanovadigital.com.br")) {
            return true;
        }
        return pNomeSAla.equals("#5531986831481wc:casanovadigital.com.br");
    }

}
