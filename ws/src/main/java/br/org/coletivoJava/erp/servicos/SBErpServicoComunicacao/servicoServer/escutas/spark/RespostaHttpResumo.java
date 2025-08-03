/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark;

import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import jakarta.json.JsonObject;

/**
 *
 * @author salvio
 */
public class RespostaHttpResumo {

    private JsonObject corpo;
    private String corpoTexto;
    private int status;

    public RespostaHttpResumo(JsonObject pCorpo, int status) {

        this.corpo = pCorpo;
        this.corpoTexto = UtilSBCoreJson.getTextoByJsonObjeect(pCorpo);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public JsonObject getCorpo() {
        return corpo;
    }

    public String getCorpoTexto() {
        return corpoTexto;
    }

}
