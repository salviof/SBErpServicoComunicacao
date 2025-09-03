/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.implmentacaopadrao.servicoNavegacao.trilhas.vindasDoWhatsapp.atendimento;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.ServicoNavegacaoAbs;
import com.google.common.collect.Lists;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import jakarta.json.JsonObject;
import java.util.List;

/**
 *
 * @author salvio
 */
public class ServicoNavegacaoPadraoAtendimento extends ServicoNavegacaoAbs implements ItfServicoNavegacao {

    public ServicoNavegacaoPadraoAtendimento(EntradaNumeroWhatsapp pEntrada) {
        super(pEntrada, TrilhaAtendimentoSimples.class, new Class[]{TrilhaAtendimentoSimples.class});
    }

    @Override
    public Class<? extends ItfTrilhaNavegacao> getClasseTrilhaDeNavegacao(Contato pContato, String pCaminho) {
        return TrilhaAtendimentoSimples.class;
    }

    @Override
    public List<String> getPalavrasParaCaminhoTrilhaRaiz() {
        return Lists.newArrayList("menu");
    }

    @Override
    public JsonObject gerarJsonDadosDeSessao(Contato pContato) {
        return null;
    }

}
