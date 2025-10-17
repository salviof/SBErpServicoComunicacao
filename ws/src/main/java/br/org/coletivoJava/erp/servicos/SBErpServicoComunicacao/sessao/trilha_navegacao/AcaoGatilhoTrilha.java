package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.integracoes.restIntwhatsapp.api.model.mensagem.MensagemSimplesEnvioWhatsapp;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;

/**
 *
 * @author salvio
 */
public class AcaoGatilhoTrilha {

    private FabAcaoGatilhosTrilha tipoAcao;
    private MensagemSimplesEnvioWhatsapp mensagemParaContato;
    private String novaRota;
    private String mensagemParaAtendimento;
    private final ContextoContato contexto;
    private final ItfTrilhaNavegacao trilha;

    public AcaoGatilhoTrilha(FabAcaoGatilhosTrilha ptipoAcao, ItfTrilhaNavegacao pTrilhaOrigem, ContextoContato pContexto) {
        this.tipoAcao = ptipoAcao;
        this.trilha = pTrilhaOrigem;
        contexto = pContexto;

    }

    public MensagemSimplesEnvioWhatsapp getMensagemParaContato() {
        return mensagemParaContato;
    }

    public AcaoGatilhoTrilha setMensagemParaContato(String mensagemParaContato) {
        this.mensagemParaContato = new MensagemSimplesEnvioWhatsapp().setCorpo(mensagemParaContato);
        return this;
    }

    public AcaoGatilhoTrilha setMensagemParaContato(MensagemSimplesEnvioWhatsapp mensagemParaContato) {
        this.mensagemParaContato = mensagemParaContato;
        return this;
    }

    public String getNovaRota() {
        return novaRota;
    }

    public AcaoGatilhoTrilha setNovaRota(String novaRota) {
        this.novaRota = novaRota;
        return this;
    }

    public String getMensagemParaAtendimento() {
        return mensagemParaAtendimento;
    }

    public AcaoGatilhoTrilha setMensagemParaAtendimento(String mensagemParaAtendimento) {
        this.mensagemParaAtendimento = mensagemParaAtendimento;
        return this;
    }

    public FabAcaoGatilhosTrilha getTipoAcao() {
        return tipoAcao;
    }

    public ContextoContato getContexto() {
        return contexto;
    }

    public ItfTrilhaNavegacao getTrilha() {
        return trilha;
    }

}
