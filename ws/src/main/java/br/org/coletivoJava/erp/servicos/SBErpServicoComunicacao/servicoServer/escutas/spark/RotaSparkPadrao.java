package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.org.coletivoJava.fw.ws.restFull.ErroAcessoNegado;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJsonRest;
import com.super_bits.modulosSB.SBCore.UtilGeral.json.ErroProcessandoJson;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.coletivojava.fw.api.tratamentoErros.FabErro;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 *
 *
 *
 * @author salvio
 */
public abstract class RotaSparkPadrao implements ItfRecepcaoPacoteServidor {

    protected Request requisicao;
    protected Response resposta;

    private RespostaHttpResumo repostaHttpResumo;

    protected void defineResposta(JsonObject pCorpo, int codigoResposta) {

        repostaHttpResumo = new RespostaHttpResumo(pCorpo, codigoResposta);

    }

    @Override
    public Object handle(Request pRequest, Response pResposta) throws Exception {
        requisicao = pRequest;
        resposta = pResposta;
        try {
            validarParamentros();
            validarPermissao();
            buildPacoteMensagemWhatsapp();
            String repostaTexto = executarRegraDeNegocio();
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseSucesso(repostaTexto, JsonValue.EMPTY_JSON_OBJECT).build(), 200);
            return repostaHttpResumo.getCorpoTexto();
        } catch (ErroParamentosInvalidos ex) {
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Paramentros inválidos: " + ex.getMessage()).build(), 400);
            return repostaHttpResumo.getCorpoTexto();
        } catch (ErroAcessoNegado ex) {
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Acesso negado: " + ex.getMessage()).build(), 403);
            return repostaHttpResumo.getCorpoTexto();
        } catch (ErroRegraDeNegocio ex) {
            resposta.status(500);
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Falha: " + ex.getMessage()).build(), 500);
            return repostaHttpResumo.getCorpoTexto();
        } catch (ErroRecursoNaoEncontrado ex) {
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Recurso não encontrado " + ex.getMessage()).build(), 404);

            return repostaHttpResumo.getCorpoTexto();
        } catch (ErroConexaoSistemaTerceiro ex) {
            resposta.status(503);
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Falha conectando com serviço de terceiros " + ex.getMessage()).build(), 503);
            return repostaHttpResumo.getCorpoTexto();

        } catch (Throwable ex) {
            resposta.status(500);
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Erro interno:" + ex.getMessage()).build(), 500);
            return repostaHttpResumo.getCorpoTexto();
        }
    }
    private PacoteMemensagemRecebidoWhatsapp pacoteMensagem = null;

    private PacoteMemensagemRecebidoWhatsapp buildPacoteMensagemWhatsapp() {

        try {
            if (pacoteMensagem == null) {
                pacoteMensagem = new PacoteMemensagemRecebidoWhatsapp(requisicao.body());
            }

        } catch (ErroProcessandoJson ex) {
            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha lendo pacote ", ex);
        }
        return pacoteMensagem;
    }


    @Override
    public void validarPermissao() throws ErroAcessoNegado {

    }


    public PacoteMemensagemRecebidoWhatsapp getPacoteMensagem() {
        if (pacoteMensagem == null) {
            buildPacoteMensagemWhatsapp();
        }
        return pacoteMensagem;
    }

    @Override
    public RespostaHttpResumo getRepostaHttpResumo() {
        return repostaHttpResumo;
    }

}
