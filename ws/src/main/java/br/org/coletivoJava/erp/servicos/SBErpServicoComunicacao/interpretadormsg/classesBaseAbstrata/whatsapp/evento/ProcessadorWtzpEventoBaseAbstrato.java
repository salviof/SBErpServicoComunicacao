/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.whatsapp.evento;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.whatsapp.ProcessadorSocketWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfProcessadorEventoWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;

/**
 *
 * @author salvio
 */
public abstract class ProcessadorWtzpEventoBaseAbstrato extends ProcessadorSocketWhatsapp implements ItfProcessadorEventoWhatsapp {

    protected EventoMensagemWtzap eventoWhatsapp;

    public ProcessadorWtzpEventoBaseAbstrato(EventoMensagemWtzap pEvento) {
        super();
        eventoWhatsapp = pEvento;

    }

}
