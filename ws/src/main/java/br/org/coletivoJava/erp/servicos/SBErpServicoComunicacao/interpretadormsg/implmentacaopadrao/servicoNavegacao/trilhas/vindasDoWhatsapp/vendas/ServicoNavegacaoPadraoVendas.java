/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.implmentacaopadrao.servicoNavegacao.trilhas.vindasDoWhatsapp.vendas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.ServicoNavegacaoAbs;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;

/**
 *
 * @author salvio
 */
public class ServicoNavegacaoPadraoVendas extends ServicoNavegacaoAbs implements ItfServicoNavegacao {

    public ServicoNavegacaoPadraoVendas(EntradaNumeroWhatsapp pEntrada) {
        super(pEntrada);
    }

    public Class<? extends ItfTrilhaNavegacao> getClasseTrilhaDeNavegacao(Contato pContato, String pCaminho) {
        if (pCaminho == null) {
            return TrilhaVendasPadrao.class;
        }
        switch (pCaminho) {
            case "REUNIAO_AGENDADA":

                break;
            default:
                throw new AssertionError();
        }
        return TrilhaVendasPadrao.class;
    }

}
