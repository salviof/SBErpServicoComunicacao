/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro;

/**
 *
 * @author salvio
 */
public class ErroComEncaminhamentoRotaRaiz extends Exception {

    private String mensagemUsuario;

    public ErroComEncaminhamentoRotaRaiz(String pMensagemUSuario) {
        super(pMensagemUSuario);
        mensagemUsuario = pMensagemUSuario;
    }

    public String getMensagemUsuario() {
        return mensagemUsuario;
    }

}
