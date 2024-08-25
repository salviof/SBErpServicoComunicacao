/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat;

import br.com.casanovadigital.servicos.notificacao.chat.controller.ApiSalasChat;
import br.com.casanovadigital.servicos.notificacao.chat.controller.ApiUsuariosChat;
import br.com.casanovadigital.servicos.notificacao.chat.controller.ApiWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.chatcliente.ApiChatClient;
import br.com.casanovadigital.servicos.notificacao.chat.controller.chatcliente.ApiChatClientCredenciais;
import br.com.casanovadigital.servicos.notificacao.chat.controller.repostas.v1.oauth.RestFullRecepcaoCodigoSolicitacao;
import br.com.casanovadigital.servicos.notificacao.chat.controller.repostas.v1.teste.contato.AtendimentoByCNPJ;
import br.com.casanovadigital.servicos.notificacao.chat.controller.repostas.v1.teste.contato.AtendimentoByNumeroLead;
import br.org.coletivoJava.fw.api.erp.chat.ERPChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.ChatMatrixOrgimpl;
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
        options("/*",
                (request, response) -> {
                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }
                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }
                    return "OK";
                });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        path("/api/v1/contatos/novoContato", () -> {
            before("/*", (q, a)
                    -> System.out.println("Conexão endpoint usuário usando ip: " + q.ip()));
            //SBCore.getServicoLogEventos().registrarLogDeEvento(FabMensagens.AVISO, "Conexão com " + q.ip()));
            get("dadosSessao.json", new ApiChatClientCredenciais());
            get("*", new ApiChatClient());

        });

        path("/api/v1/chatClient/", () -> {
            before("/*", (q, a)
                    -> System.out.println("Conexão endpoint usuário usando ip: " + q.ip()));
            //SBCore.getServicoLogEventos().registrarLogDeEvento(FabMensagens.AVISO, "Conexão com " + q.ip()));
            get("dadosSessao.json", new ApiChatClientCredenciais());
            get("*", new ApiChatClient());

        });

        path("/api/v1/usuario/", () -> {
            before("/*", (q, a)
                    -> System.out.println("Conexão endpoint usuário usando ip: " + q.ip()));
            //SBCore.getServicoLogEventos().registrarLogDeEvento(FabMensagens.AVISO, "Conexão com " + q.ip()));

            get("/online/*", ApiUsuariosChat.usuarioOnline());

        });

        path("/api/v1/whatsapp/", () -> {
            before("/*", (q, a)
                    -> {
                System.out.println("Conexão endpoint whatsapp usando ip: " + q.ip());
                System.out.println(q.queryString());
                System.out.println(q.pathInfo());
                System.out.println(q.requestMethod());

            }
            );
            //SBCore.getServicoLogEventos().registrarLogDeEvento(FabMensagens.AVISO, "Conexão com " + q.ip()));
            post("/recepcao/evento", ApiWhatsapp.recepcaoEvento());
            post("/recepcao/notificacao", ApiWhatsapp.recepcaoNotificacao());
            get("/recepcao/notificacao", ApiWhatsapp.recepcaoNotificacao());
            post("/envio", ApiWhatsapp.envioMensagem());

        });

        path("/api/v1/salas/", () -> {
            before("/*", (q, a)
                    -> System.out.println("Conexão endpoint salas ip: " + q.ip()));
            //SBCore.getServicoLogEventos().registrarLogDeEvento(FabMensagens.AVISO, "Conexão com " + q.ip()));

            path("salaPrimeiroContato/", () -> {
                post("", ApiSalasChat.salaPrimeiroContatoNovaPost());
                get("membros/*", ApiSalasChat.salaPrimeiroContatoGetMembros());
                get("*", ApiSalasChat.salaPrimeiroContatoGet());

            }
            );

            /**
             * path("/chatWhatsapp", () -> { post("",
             * ApiSalasChat.novaSalaPrimeiroContatoPost()); get("",
             * ApiSalasChat.novaSalaPrimeiroContatoGet());
             *
             * });
             *
             * path("/debateInternoIntranet", () -> { post("",
             * ApiSalasChat.novaSalaPrimeiroContatoPost()); get("",
             * ApiSalasChat.novaSalaPrimeiroContatoGet());
             *
             * });
             *
             * path("/chamadoCliente", () -> { post("",
             * ApiSalasChat.novaSalaPrimeiroContatoPost()); get("",
             * ApiSalasChat.novaSalaPrimeiroContatoGet()); });
             *
             * path("/debateCliente", () -> { post("",
             * ApiSalasChat.novaSalaPrimeiroContatoPost()); get("",
             * ApiSalasChat.novaSalaPrimeiroContatoGet()); });
             */
        });
        //AtendimentoByNumeroLead

        path("/solicitacaoAuth2Recept/code/SISTEMA", () -> {
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

        });

    }
}
