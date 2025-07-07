/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.interpretadormsg.padrao.whatsapp;

import br.com.casanovadigital.servicos.chat.interpretadormsg.interfaces.ItfProcessadorEventoWhatsapp;
import br.com.casanovadigital.servicos.chat.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;

/**
 *
 * @author salvio
 */
public abstract class ProcessadorWtzpEventoBaseAbstrato extends ProcessadorSocketWhatsapp implements ItfProcessadorEventoWhatsapp {

    protected final EventoMensagemWtzap eventoWhatsapp;

    public ProcessadorWtzpEventoBaseAbstrato(EventoMensagemWtzap pEvento) throws ErroFalhaEncaminhando, ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {
        super();
        eventoWhatsapp = pEvento;
    }
}
