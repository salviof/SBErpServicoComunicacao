package br.com.casanovadigital.servicos.notificacao.contextoChat;

import jakarta.json.JsonObject;
import java.util.Date;

/**
 *
 * @author salvio
 */
public class ContextoChat {

    protected JsonObject dadosDeContexto;
    private JsonObject scriptConversa;
    protected ItfContextoLoadContexto leituraContexto;
    private String acaoAtualizacaoDadosContexto;

    private final FabTipoContexto tipoContexto;

    protected Date dataHoraUltimaMensagem = new Date();

    public ContextoChat(FabTipoContexto pTipoContexto) {
        tipoContexto = pTipoContexto;

    }

    public JsonObject getDadosDeContexto() {
        return dadosDeContexto;
    }

    public JsonObject getScriptConversa() {
        return scriptConversa;
    }

    public String getAcaoAtualizacaoDadosContexto() {
        return acaoAtualizacaoDadosContexto;
    }

    public FabTipoContexto getTipoContexto() {
        return tipoContexto;
    }

    public Date getDataHoraUltimaMensagem() {
        return dataHoraUltimaMensagem;
    }

}
