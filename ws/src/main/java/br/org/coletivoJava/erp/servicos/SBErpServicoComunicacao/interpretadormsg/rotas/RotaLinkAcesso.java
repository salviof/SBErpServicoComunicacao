/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;

/**
 *
 * @author salvio
 */
public class RotaLinkAcesso extends RotaMensagemContato {

    private String descricaoLink;
    private String nomeAcao;
    private String linkAcao;

    public RotaLinkAcesso(Contato p) {
        super(FabTipoRotaMensagem.RETORNO_LINK, p);
    }

    public String getNomeAcao() {
        return nomeAcao;
    }

    public void setNomeAcao(String nomeAcao) {
        this.nomeAcao = nomeAcao;
    }

    public String getLinkAcao() {
        return linkAcao;
    }

    public void setLinkAcao(String linkAcao) {
        this.linkAcao = linkAcao;
    }

    public String getDescricaoLink() {
        return descricaoLink;
    }

    public void setDescricaoLink(String descricaoLink) {
        this.descricaoLink = descricaoLink;
    }

}
