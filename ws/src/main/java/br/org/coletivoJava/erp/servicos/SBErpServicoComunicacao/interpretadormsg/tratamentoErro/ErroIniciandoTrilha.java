/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro;

/**
 *
 * @author salvio
 */
public class ErroIniciandoTrilha extends Exception {

    private final ErroComDevolucaoMensagemUsuario erroComDevolucao;

    public ErroIniciandoTrilha(ErroComDevolucaoMensagemUsuario erroComDevolucao) {
        this.erroComDevolucao = erroComDevolucao;
    }

    public ErroComDevolucaoMensagemUsuario getErroComDevolucao() {
        return erroComDevolucao;
    }

}
