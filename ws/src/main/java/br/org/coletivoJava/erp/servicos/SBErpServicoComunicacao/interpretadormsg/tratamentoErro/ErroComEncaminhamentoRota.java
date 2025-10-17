/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro;

/**
 *
 * @author salvio
 */
public class ErroComEncaminhamentoRota extends Exception {

    private String rota;

    public ErroComEncaminhamentoRota(String pMotivo, String pRota) {
        super(pMotivo);
        rota = pRota;
    }

    public String getRota() {
        return rota;
    }

}
