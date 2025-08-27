/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.implmentacaopadrao.servicoNavegacao.trilhas.vindasDoWhatsapp.atendimento;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.TrilhaNavegacaoAbs;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.eventos.EventoSalaMatrix;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;

/**
 *
 * @author salvio
 */
public class TrilhaAtendimentoSimples extends TrilhaNavegacaoAbs {

    public TrilhaAtendimentoSimples(ContextoContato pContato, TrilhaNavegacaoAbs pTrilhaOrigem, EntradaNumeroWhatsapp pEntrada, String pCaminhoTrilha) {
        super(pContato, pTrilhaOrigem, pEntrada, pCaminhoTrilha);
    }

    @Override
    public String iniciarTrilha() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getDesvioTrilhaPorMensgemWhatsapp(MensagemWhatsapp p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getDesvioTrilhaPorEventoMatrix(EventoSalaMatrix p) throws ErroComDevolucaoMensagemUsuario {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void acaoTimeoutAguardandoRespostaAtendimento() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void acaoTimeoutAguardandoInteracaoContato() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
