/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.sessao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;

/**
 *
 * @author salvio
 */
public class SessaoDeContato {

    private ContextoContato contexto;
    private ItfChatSalaBean salaPadrao;

    public SessaoDeContato(ContextoContato contexto, ItfChatSalaBean salaPadrao) {
        this.contexto = contexto;
        this.salaPadrao = salaPadrao;

    }

    public void setContexto(ContextoContato contexto) {
        this.contexto = contexto;
    }

    public ContextoContato getContexto() {
        return contexto;
    }

    public ItfChatSalaBean getSalaPadrao() {
        return salaPadrao;
    }

}
