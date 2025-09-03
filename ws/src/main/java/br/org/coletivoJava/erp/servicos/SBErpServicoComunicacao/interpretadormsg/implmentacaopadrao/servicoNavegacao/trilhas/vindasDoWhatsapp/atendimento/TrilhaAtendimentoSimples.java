/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.implmentacaopadrao.servicoNavegacao.trilhas.vindasDoWhatsapp.atendimento;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.AcaoGatilhoTrilha;
import br.org.coletivoJava.fw.api.erp.chat.model.ComandoDeAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.TrilhaNavegacaoAbs;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
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
    public AcaoGatilhoTrilha getAcaoTrilhaPorMensgemContato(String pMensagem, String pComando) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public AcaoGatilhoTrilha getAcaoTrilhaPorMensgemAtendimento(String pMensagem, String pComando) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void iniciarTrilha() throws ErroConexaoServicoChat, ErroComDevolucaoMensagemUsuario {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public AcaoGatilhoTrilha getAcaoDeGatilhoLoadDadosSessao(ContextoContato pContexto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
