/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;

/**
 *
 * @author salvio
 */
public interface ItfProcessadorMensagemWhatsapp extends ItfProcessadorPacoteMatrixWhatsap {

    public MensagemWhatsapp getMensagemWhatsapp();

}
