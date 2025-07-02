/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.notificacaoLeitura;

import de.jojii.matrixclientserver.Bot.Events.RoomEvent;

/**
 *
 * @author salvio
 */
public class EventoEncMatrixParaWtsap extends EventoEncaminhamento {

    private final RoomEvent eventoOrigem;

    public EventoEncMatrixParaWtsap(RoomEvent pEvento, String pCodigoEventoWhatsapp) {
        super(pCodigoEventoWhatsapp, pEvento.getEvent_id());
        eventoOrigem = pEvento;
    }

    public RoomEvent getEventoOrigem() {
        return eventoOrigem;
    }

    @Override
    public String toString() {
        return eventoOrigem.getEvent_id();
    }

}
