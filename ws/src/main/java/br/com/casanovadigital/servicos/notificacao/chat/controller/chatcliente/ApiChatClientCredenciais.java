/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.chatcliente;

import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.FabTipoConexaoRest;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.RespostaWebServiceSimples;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.implementacao.UtilSBApiRestClient;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author salvio
 */
public class ApiChatClientCredenciais extends RotaSparkPadrao {

    private String urlCRM;
    private String jsessionID;
    private String roomID;

    @Override
    public void validarParamentros() throws ErroParamentosInvalidos {
        Map<String, String> cookies = requisicao.cookies();
        urlCRM = cookies.get("URL_CRM");
        jsessionID = cookies.get("JSESSIONID_CRM");
        roomID = cookies.get("ROOM_ID");
    }

    @Override
    public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {

        Map<String, String> mapa = new HashMap<>();
        resposta.header("Access-Control-Allow-Origin", urlCRM);
        resposta.header("Access-Control-Allow-Methods", "POST, GET");

        mapa.put("Cookie", "JSESSIONID=" + jsessionID);
        RespostaWebServiceSimples resp = UtilSBApiRestClient.getRespostaRest(
                urlCRM + "/matrixDados/.json?", FabTipoConexaoRest.GET, false, mapa, null);
        return resp.getRespostaTexto();

    }

}
