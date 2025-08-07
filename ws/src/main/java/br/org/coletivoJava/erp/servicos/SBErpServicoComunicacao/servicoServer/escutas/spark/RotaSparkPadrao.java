package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark;

import br.org.coletivoJava.fw.ws.restFull.ErroAcessoNegado;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJsonRest;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import spark.Request;
import spark.Response;

/**
 *
 *
 *
 * @author salvio
 */
public abstract class RotaSparkPadrao implements ItfRecepcaoPacoteServidor {

    private RespostaHttpResumo repostaHttpResumo;

    protected void defineResposta(JsonObject pCorpo, int codigoResposta) {

        repostaHttpResumo = new RespostaHttpResumo(pCorpo, codigoResposta);

    }

    @Override
    public Object handle(Request pRequest, Response pResposta) throws Exception {

        try {
            validarParamentros(pRequest);
            validarPermissao();

            String repostaTexto = executarRegraDeNegocio(pRequest, pResposta);
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseSucesso(repostaTexto, JsonValue.EMPTY_JSON_OBJECT).build(), 200);
            return repostaHttpResumo.getCorpoTexto();
        } catch (ErroParamentosInvalidos ex) {
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Paramentros inválidos: " + ex.getMessage()).build(), 400);
            return repostaHttpResumo.getCorpoTexto();
        } catch (ErroAcessoNegado ex) {
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Acesso negado: " + ex.getMessage()).build(), 403);
            return repostaHttpResumo.getCorpoTexto();
        } catch (ErroRegraDeNegocio ex) {
            pResposta.status(500);
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Falha: " + ex.getMessage()).build(), 500);
            return repostaHttpResumo.getCorpoTexto();
        } catch (ErroRecursoNaoEncontrado ex) {
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Recurso não encontrado " + ex.getMessage()).build(), 404);

            return repostaHttpResumo.getCorpoTexto();
        } catch (ErroConexaoSistemaTerceiro ex) {
            pResposta.status(503);
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Falha conectando com serviço de terceiros " + ex.getMessage()).build(), 503);
            return repostaHttpResumo.getCorpoTexto();

        } catch (Throwable ex) {
            pResposta.status(500);
            defineResposta(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Erro interno:" + ex.getMessage()).build(), 500);
            return repostaHttpResumo.getCorpoTexto();
        }
    }

    public String executarRegraDeNegocio(Request pRequest, Response pResposta) throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
        return executarRegraDeNegocio(pRequest.body());
    }

    @Override
    public void validarPermissao() throws ErroAcessoNegado {

    }

    @Override
    public RespostaHttpResumo getRepostaHttpResumo() {
        return repostaHttpResumo;
    }

}
