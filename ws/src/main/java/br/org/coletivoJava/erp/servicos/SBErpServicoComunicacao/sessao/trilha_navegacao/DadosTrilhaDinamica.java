package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao;

import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import jakarta.json.JsonObject;

/**
 *
 * @author salvio
 */
public class DadosTrilhaDinamica {

    public DadosTrilhaDinamica(JsonObject pJson) {
        if (pJson == null) {
            throw new UnsupportedOperationException("Falha json não enviado");
        }
        nomeRota = pJson.getString("nomeRota");
        modeloMensagemBoasVindas = pJson.getString("modeloMensagemBoasVindas");
        atendenteSelecionada = pJson.getString("atendenteSelecionada");
        JsonObject jsonObjetoTrilha = pJson.getJsonObject("entidadeVinculada");
        entidadeTrilhaDinamica = new EntidadeDaTrilhaDinamica(Long.valueOf(jsonObjetoTrilha.getInt("codigo")), jsonObjetoTrilha.getString("nomeEntidade"), jsonObjetoTrilha.getString("descricao"));

    }

    private String nomeRota;
    private String modeloMensagemBoasVindas;
    private String atendenteSelecionada;
    private FabTipoSalaMatrix tipoSala;
    private EntidadeDaTrilhaDinamica entidadeTrilhaDinamica;

    public String getNomeRota() {
        return nomeRota;
    }

    public String getModeloMensagemBoasVindas() {
        return modeloMensagemBoasVindas;
    }

    public String getAtendenteSelecionada() {
        return atendenteSelecionada;
    }

    public FabTipoSalaMatrix getTipoSala() {
        return tipoSala;
    }

    public EntidadeDaTrilhaDinamica getEntidadeTrilhaDinamica() {
        return entidadeTrilhaDinamica;
    }

}
