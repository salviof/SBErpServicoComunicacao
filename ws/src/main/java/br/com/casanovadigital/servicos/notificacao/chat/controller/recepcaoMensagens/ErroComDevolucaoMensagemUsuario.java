/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens;

/**
 *
 * @author salvio
 */
public class ErroComDevolucaoMensagemUsuario extends Exception {

    private String mensagemRetorno;

    public ErroComDevolucaoMensagemUsuario(String pFalha, String pMensagemRetorno) {
        super(pFalha);
        mensagemRetorno = pMensagemRetorno;
    }

    public String getMensagemRetorno() {
        return mensagemRetorno;
    }

}
