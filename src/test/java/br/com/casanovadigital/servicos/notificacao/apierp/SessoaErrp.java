/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.apierp;

import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;

/**
 *
 * @author salvio
 */
public class SessoaErrp {

    private String urlServidor;

    private String jsessionId;
    private String viewid;
    private String usuarioLogado;

    public SessoaErrp(String pUrl) {
        urlServidor = pUrl;
    }

    public ItfRespostaWebServiceSimples getResposta(String url) {
        return null;
    }

}
