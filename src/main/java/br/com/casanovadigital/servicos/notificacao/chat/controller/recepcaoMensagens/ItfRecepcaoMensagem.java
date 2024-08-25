/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.contextoChat.whatsapp.ContextoWhatsapp;

/**
 *
 * @author salvio
 */
public interface ItfRecepcaoMensagem extends Runnable {

    public void declararfoiLida();

    public ContextoWhatsapp getContextoWhatsapp();

    public MensagemWhatsapp getMensagem();

    public boolean isEncaminhada();

    public boolean isLida();

    public boolean isFinalizada();

    public int getQuantidadeTentativasEncaminhar();

    public String getCodigoReciboEncaminhamento();

}
