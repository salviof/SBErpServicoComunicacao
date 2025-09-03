/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;

/**
 *
 * @author salvio
 */
public class AcaoGatilhoTrilha {

    private FabAcaoGatilhosTrilha tipoAcao;
    private String mensagemParaContato;
    private String novaRota;
    private String mensagemParaAtendimento;
    private final ContextoContato contexto;
    private final ItfTrilhaNavegacao trilha;

    public AcaoGatilhoTrilha(FabAcaoGatilhosTrilha ptipoAcao, ItfTrilhaNavegacao pTrinha, ContextoContato pContexto) {
        this.tipoAcao = ptipoAcao;
        this.trilha = pTrinha;
        contexto = pContexto;

    }

    public String getMensagemParaContato() {
        return mensagemParaContato;
    }

    public void setMensagemParaContato(String mensagemParaContato) {
        this.mensagemParaContato = mensagemParaContato;
    }

    public String getNovaRota() {
        return novaRota;
    }

    public void setNovaRota(String novaRota) {
        this.novaRota = novaRota;
    }

    public String getMensagemParaAtendimento() {
        return mensagemParaAtendimento;
    }

    public void setMensagemParaAtendimento(String mensagemParaAtendimento) {
        this.mensagemParaAtendimento = mensagemParaAtendimento;
    }

    public FabAcaoGatilhosTrilha getTipoAcao() {
        return tipoAcao;
    }

    public void setTipoAcao(FabAcaoGatilhosTrilha tipoAcao) {
        this.tipoAcao = tipoAcao;
    }

    public ContextoContato getContexto() {
        return contexto;
    }

    public ItfTrilhaNavegacao getTrilha() {
        return trilha;
    }

}
