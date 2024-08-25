/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.contextoChat;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.SalaMatrxOrg;
import jakarta.json.JsonObject;

/**
 *
 * @author salvio
 */
public interface ItfContextoLoadContexto {

    public JsonObject gerarDadosDoCotexto(ContatoWhatsapp pContato);

}
