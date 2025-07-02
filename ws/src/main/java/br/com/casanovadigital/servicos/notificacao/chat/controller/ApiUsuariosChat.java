/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller;

import br.com.casanovadigital.servicos.notificacao.chat.ServicoNotificacaoChat;
import br.com.casanovadigital.servicos.notificacao.contextoChat.matrix.ContextoMatrix;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJsonRest;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import spark.Route;

/**
 *
 * @author salvio
 */
public class ApiUsuariosChat {

    public static Route usuarioOnline() {

        return new RotaSparkPadrao() {
            private String codigoUsuario;

            @Override
            public void validarParamentros() throws ErroParamentosInvalidos {
                if (requisicao.splat().length != 1) {
                    throw new ErroParamentosInvalidos("Sala não encontrada");

                }
                codigoUsuario = requisicao.splat()[0];
            }

            @Override
            public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {

                try {
                    JsonObjectBuilder resp = Json.createObjectBuilder();
                    resp.add("presenca", ContextoMatrix.getChatMatrixService().isUsuarioOnlineByCodUser(codigoUsuario));
                    return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseSucesso(resp.build()).build());
                } catch (Throwable t) {
                    throw new ErroConexaoSistemaTerceiro("Erro não tratado" + t.getMessage());
                }

            }
        };
    }
}
