/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.implementacao.ChamadaHttpSimples;

/**
 *
 * @author salvio
 */
public class RotaWebservice extends RotaMensagemContato {

    private ChamadaHttpSimples chamada;

    private String caminhoJsonMensagemContato;

    private String caminhoJsonMensagemAtendimento;

    private String codigoSalaAtendimento;

    private String codigoAtendimento;

    public RotaWebservice(Contato p) {
        super(FabTipoRotaMensagem.RESPOSTA_WEBSERVICE, p);
    }

    public ChamadaHttpSimples getChamada() {
        return chamada;
    }

    public void setChamada(ChamadaHttpSimples chamada) {
        this.chamada = chamada;
    }

    public String getCaminhoJsonMensagemContato() {
        return caminhoJsonMensagemContato;
    }

    public void setCaminhoJsonMensagemContato(String caminhoJsonMensagemContato) {
        this.caminhoJsonMensagemContato = caminhoJsonMensagemContato;
    }

    public String getCaminhoJsonMensagemAtendimento() {
        return caminhoJsonMensagemAtendimento;
    }

    public void setCaminhoJsonMensagemAtendimento(String caminhoJsonMensagemAtendimento) {
        this.caminhoJsonMensagemAtendimento = caminhoJsonMensagemAtendimento;
    }

    public String getCodigoSalaAtendimento() {
        return codigoSalaAtendimento;
    }

    public void setCodigoSalaAtendimento(String codigoSalaAtendimento) {
        this.codigoSalaAtendimento = codigoSalaAtendimento;
    }

    public String getCodigoAtendimento() {
        return codigoAtendimento;
    }

    public void setCodigoAtendimento(String codigoAtendimento) {
        this.codigoAtendimento = codigoAtendimento;
    }

}
