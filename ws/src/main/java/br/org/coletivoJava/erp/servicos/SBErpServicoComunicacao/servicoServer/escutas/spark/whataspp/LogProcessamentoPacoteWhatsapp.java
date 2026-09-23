/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.whataspp;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salvio
 */
public class LogProcessamentoPacoteWhatsapp {

    private boolean processouComSucesso;

    private List<LogAcaoProcessamentoPacoteWhatsapp> acoes = new ArrayList<>();

    public List<LogAcaoProcessamentoPacoteWhatsapp> getAcoes() {
        return acoes;
    }

    public boolean isProcessouComSucesso() {
        return processouComSucesso;
    }

    public void setProcessouComSucesso(boolean processouComSucesso) {
        this.processouComSucesso = processouComSucesso;
    }

}
