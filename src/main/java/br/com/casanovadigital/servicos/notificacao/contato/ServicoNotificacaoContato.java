/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.contato;

import br.com.casanovadigital.servicos.notificacao.chat.controller.ApiSalasChat;
import br.com.casanovadigital.servicos.notificacao.chat.controller.contatos.ApiContatosReceberContato;
import br.com.casanovadigital.servicos.notificacao.chat.controller.repostas.v1.teste.contato.AtendimentoByCNPJ;
import br.com.casanovadigital.servicos.notificacao.chat.controller.repostas.v1.teste.contato.AtendimentoByNumeroLead;
import br.com.casanovadigital.servicos.notificacao.config.FabConfigModuloSNSNotificacaoNovoContato;
import br.org.coletivoJava.integracoes.matrixChat.FabApiRestIntMatrixChatUsuarios;
import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.ConfigGeral.arquivosConfiguracao.ConfigModulo;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.ItfResposta;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabTipoAgenteDoSistema;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import java.util.HashMap;
import java.util.Map;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;

/**
 *
 * @author salvio
 */
public class ServicoNotificacaoContato {

    public static void iniciarServico() {
        //   ConfigModulo config = SBCore.getConfigModulo(FabConfigApiMatrixChat.class);
//        get("/chat/*", (request, response) -> {

        //String email = request.attribute("email");
        //String telefone = request.attribute("telefone");
        //String nome = request.attribute("nome");
        //String referencia = request.attribute("ref");
        //FabApiRestIntMatrixChatUsuarios.USUARIO_CRIAR.getAcao(nome, email, "senha");
        //return "";
        //});
        path("/api/v1/contato/teste", () -> {
            before("/", (q, a)
                    -> {
                System.out.println("Conexão endpoint teste contato " + q.ip());
                System.out.println(q.queryString());
                System.out.println(q.pathInfo());
                System.out.println(q.requestMethod());

            }
            );
            //SBCore.getServicoLogEventos().registrarLogDeEvento(FabMensagens.AVISO, "Conexão com " + q.ip()));
            get("/atendimnetoByCNPJ/*", new AtendimentoByCNPJ());
            get("/atendimentoByNumeroLead/*", new AtendimentoByNumeroLead());

        });

        path("/api/v1/contato/", () -> {
            before("/*", (q, a)
                    -> System.out.println("Conexão endpoint salas ip: " + q.ip()));
            //SBCore.getServicoLogEventos().registrarLogDeEvento(FabMensagens.AVISO, "Conexão com " + q.ip()));

            post("add", new ApiContatosReceberContato());

        });

        if (false) {
            get("/notificacao/testeSNS", (request, response) -> {

                String urlRequisicao = request.uri();

                String chaveAcesso = request.headers("chaveAcesso");
                try {
                    // if (chaveAcesso == null) {
                    //     throw new Throwable("Falha obtendo notificações");
                    // }
                    // String chaveacesso = config.getPropriedade(FabConfigApiMatrixChat.SEGREDO);
                    //  if (!chaveAcesso.equals(chaveacesso)) {
                    //     throw new Throwable("Falha obtendo notificações chn");
                    // }
                    String chavePublica = SBCore.getConfigModulo(FabConfigModuloSNSNotificacaoNovoContato.class).getPropriedade(FabConfigModuloSNSNotificacaoNovoContato.CHAVE_PRIVADA_SNS);
                    String chavePrivada = SBCore.getConfigModulo(FabConfigModuloSNSNotificacaoNovoContato.class).getPropriedade(FabConfigModuloSNSNotificacaoNovoContato.CHAVE_PUBLICA_SNS);
                    BasicAWSCredentials basicAwsCredentials = new BasicAWSCredentials(chavePublica, chavePrivada);

                    try {
                        AmazonSNS snsClient = AmazonSNSClient
                                .builder()
                                .withRegion("us-east-1")
                                .withCredentials(new AWSStaticCredentialsProvider(basicAwsCredentials))
                                .build();
                        sendSMSMessageToTopic(snsClient, "arn:aws:sns:us-east-1:743991738744:casanovaForms", "{teste: 'Colé doidin'}");

                    } catch (Throwable e) {
                        throw new Throwable("Falha publicando mensagem SNS");
                    }
                    JsonObjectBuilder resposta = Json.createObjectBuilder();
                    resposta.add("sucesso", true);
                    resposta.add("resultado", ItfResposta.Resultado.SUCESSO.toString());

                    return UtilSBCoreJson.getTextoByJsonObjeect(resposta.build());
                } catch (Throwable t) {
                    response.status(404);
                    JsonObjectBuilder resposta = Json.createObjectBuilder();
                    resposta.add("sucesso", false);
                    resposta.add("resultado", ItfResposta.Resultado.FALHOU.toString());
                    JsonArrayBuilder mensagens = Json.createArrayBuilder();
                    JsonObjectBuilder mensagem = Json.createObjectBuilder();

                    mensagem.add("tipoMensagem", FabMensagens.ERRO.toString());
                    mensagem.add("tipoDestinatario", FabTipoAgenteDoSistema.USUARIO.toString());
                    mensagem.add("mensagem", t.getMessage());
                    mensagens.add(mensagem);
                    resposta.add("mensagens", mensagens);
                    return UtilSBCoreJson.getTextoByJsonObjeect(resposta.build());
                }

            });
        }
    }

    public static void sendSMSMessageToTopic(AmazonSNS snsClient, String topicArn,
            String message) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withTopicArn(topicArn)
                .withMessage(message)
        );
        System.out.println(result);
    }
}
