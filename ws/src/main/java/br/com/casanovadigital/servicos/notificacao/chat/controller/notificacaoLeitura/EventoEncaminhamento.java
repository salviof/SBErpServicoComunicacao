/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.notificacaoLeitura;

import java.util.Date;

/**
 *
 * @author salvio
 */
public class EventoEncaminhamento {

    private final String codigoEventoWhatsapp;
    private final String codigoEventoMatrix;
    private final Date dataHora = new Date();

    public EventoEncaminhamento(String codigoEventoWhatsapp, String codigoEventoMatrix) {

        this.codigoEventoWhatsapp = codigoEventoWhatsapp;
        this.codigoEventoMatrix = codigoEventoMatrix;

    }

    public String getCodigoEventoWhatsapp() {
        return codigoEventoWhatsapp;
    }

    public String getCodigoEventoMatrix() {
        return codigoEventoMatrix;
    }

    public Date getDataHora() {
        return dataHora;
    }

}
