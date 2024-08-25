/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.notificacaoLeitura;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;

/**
 *
 * @author salvio
 */
public class EventoEncWhatsapParaMatrix extends EventoEncaminhamento {

    private final MensagemWhatsapp mensagem;

    public EventoEncWhatsapParaMatrix(MensagemWhatsapp pMensagem, String pCodigoEventoMatrix) {
        super(pMensagem.getId(), pCodigoEventoMatrix);
        mensagem = pMensagem;
    }

    public MensagemWhatsapp getMensagem() {
        return mensagem;
    }

    @Override
    public String toString() {
        return mensagem.getId();
    }

}
