/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.chatcliente;

import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;

/**
 *
 * @author salvio
 */
public class ApiChatClient extends RotaSparkPadrao {

    private String jsessionID;
    private String urlCRM;
    private String salaid;

    @Override
    public void validarParamentros() throws ErroParamentosInvalidos {
        jsessionID = requisicao.queryParamOrDefault("JSESSIONID", null);
        urlCRM = requisicao.queryParamOrDefault("URL_CRM", null);
        salaid = requisicao.queryParamOrDefault("ROOM_ID", null);

    }

    @Override
    public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {

        resposta.cookie("/", "JSESSIONID_CRM", jsessionID, 60, true);
        resposta.cookie("/", "URL_CRM", urlCRM, 60, true);
        resposta.cookie("/", "ROOM_ID", salaid, 60, true);
        resposta.redirect("/hidrogenio/index.html");
        return "<script>window.location='/hidrogenio/index.html'</script>";

    }

}
