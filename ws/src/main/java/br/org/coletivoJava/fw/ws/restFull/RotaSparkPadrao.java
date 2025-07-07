/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.fw.ws.restFull;

import br.com.casanovadigital.servicos.chat.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.contato.ErroCriandoContato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJsonRest;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import org.coletivojava.fw.api.tratamentoErros.FabErro;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 *
 * @author salvio
 */
public abstract class RotaSparkPadrao implements Route {

    protected Request requisicao;
    protected Response resposta;

    @Override
    public Object handle(Request pRequest, Response pResposta) throws Exception {
        requisicao = pRequest;
        resposta = pResposta;
        return processar();

    }
    private PacoteMemensagemRecebidoWhatsapp pacoteMensagem = null;

    private PacoteMemensagemRecebidoWhatsapp buildMensagem() {

        try {
            if (pacoteMensagem == null) {
                pacoteMensagem = new PacoteMemensagemRecebidoWhatsapp(requisicao.body());
            }

        } catch (ErroCriandoContato ex) {
            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha lendo pacote ", ex);
        }
        return pacoteMensagem;
    }

    private String processar() {
        try {
            validarParamentros();
            validarPermissao();
            buildMensagem();
            return executarRegraDeNegocio();
        } catch (ErroParamentosInvalidos ex) {
            resposta.status(400);
            return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Paramentros inválidos: " + ex.getMessage()).build());
        } catch (ErroAcessoNegado ex) {
            resposta.status(403);
            return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Acesso negado: " + ex.getMessage()).build());
        } catch (ErroRegraDeNegocio ex) {
            resposta.status(500);
            return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Falha: " + ex.getMessage()).build());
        } catch (ErroRecursoNaoEncontrado ex) {
            resposta.status(404);
            return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Recurso não encontrado " + ex.getMessage()).build());
        } catch (ErroConexaoSistemaTerceiro ex) {
            resposta.status(503);
            return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Falha conectando com serviço de terceiros " + ex.getMessage()).build());
        } catch (Throwable ex) {
            resposta.status(500);
            return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Erro interno:" + ex.getMessage()).build());
        }

    }

    public abstract void validarParamentros() throws ErroParamentosInvalidos;

    public void validarPermissao() throws ErroAcessoNegado {

    }

    public abstract String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro;

    public PacoteMemensagemRecebidoWhatsapp getPacoteMensagem() {
        if (pacoteMensagem == null) {
            buildMensagem();
        }
        return pacoteMensagem;
    }

    public Request getRequisicao() {
        return requisicao;
    }

}
