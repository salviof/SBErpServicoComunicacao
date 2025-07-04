/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.legado.chat;

import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.ApiSalasChat;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.ApiUsuariosChat;
import br.com.casanovadigital.servicos.notificacao.servicoServer.whatsapp.ApiWhatsapp;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.chatcliente.ApiChatClient;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.chatcliente.ApiChatClientCredenciais;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.repostas.v1.oauth.RestFullRecepcaoCodigoSolicitacao;
import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.ConfigGeral.arquivosConfiguracao.ConfigModulo;

import static spark.Spark.*;

/**
 *
 * @author salvio
 */
public class ServicoNotificacaoChat {

    public static void iniciarServico() {
        ConfigModulo config = SBCore.getConfigModulo(FabConfigApiMatrixChat.class);

        System.out.println("Iniciando aplicação em:");
        System.out.println(config.getPropriedade(FabConfigApiMatrixChat.URL_MATRIX_SERVER));

        if (!SBCore.isEmModoProducao()) {
            System.out.println(config.getPropriedade(FabConfigApiMatrixChat.SEGREDO).substring(0, 7));
            System.out.println(config.getPropriedade(FabConfigApiMatrixChat.USUARIO_ADMIN));
            System.out.println(config.getPropriedade(FabConfigApiMatrixChat.SENHA_USUARIO_ADMIN).substring(0, 4));
        }

        path("/api/v1/whatsapp/", () -> {
            before("/*", (q, a)
                    -> {
                System.out.println("Conexão endpoint whatsapp usando ip: " + q.ip());
                System.out.println(q.queryString());
                System.out.println(q.pathInfo());
                System.out.println(q.requestMethod());

            }
            );

            post("/recepcao/evento", ApiWhatsapp.recepcaoEvento());

        });

        path(
                "/solicitacaoAuth2Recept/code/SISTEMA", () -> {
                    before("/", (q, a)
                            -> {
                        System.out.println("Conexão endpoint recepcção código de concessao " + q.ip());
                        System.out.println(q.queryString());
                        System.out.println(q.pathInfo());
                        System.out.println(q.requestMethod());
                    }
                    );
                    //SBCore.getServicoLogEventos().registrarLogDeEvento(FabMensagens.AVISO, "Conexão com " + q.ip()));
                    get("/*", new RestFullRecepcaoCodigoSolicitacao());

                }
        );

    }
}
