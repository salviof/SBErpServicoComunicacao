/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.config;

import br.com.casanovadigital.servicos.notificacao.AplicacaoWsChat;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.FabSistemasErp;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import br.com.casanovadigital.servicos.notificacao.legado.contextoChat.whatsapp.ContextoWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.modulosSB.SBCore.modulos.erp.ItfSistemaERP;
import java.util.ArrayList;
import java.util.List;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.interfaces.ItfCentralLogicasProcessamentoMsg;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.interfaces.ItfProcessadorEventoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.interfaces.ItfProcessadorMensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.padrao.whatsapp.ProcessadorEventoWhatsappPadrao;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.padrao.whatsapp.ProcessadorMsgWhatsappPadrao;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author salvio
 */
public class DefinicaoLogicaProcessamentoChat implements ItfCentralLogicasProcessamentoMsg {

    @Override
    public Class<? extends ItfProcessadorMensagemWhatsapp> getClasseProcessadorMensagemContatoViaWhataspp(MensagemWhatsapp pMensagemWhatsapp) {
        return ProcessadorMsgWhatsappPadrao.class;
    }

    @Override
    public Class<? extends ItfProcessadorEventoWhatsapp> getClasseProcessadorEventoRecebidoPeloWhatsapp(EventoMensagemWtzap pPacote) {
        return ProcessadorEventoWhatsappPadrao.class;
    }

    @Override
    public ItfUsuarioChat getUsuarioAtendimentoPadrao(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato) {

        String email = FabConfigServicoComunicacao.USUARIO_ATENDIMENTO_PADRAO.getValorParametroSistema();
        try {
            return AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByEmail(email);
        } catch (ErroConexaoServicoChat ex) {
            return null;
        }
    }

    public DefinicaoLogicaProcessamentoChat() {
        System.out.println("up");
    }

    @Override
    public ContextoWhatsapp gerarNovoCotextoByMensagem(EntradaNumeroWhatsapp pEntrada, MensagemWhatsapp pMensagem) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ContextoWhatsapp gerarNovoCotexto(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pMensagem) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ContextoWhatsapp gerarNovoContextoByStatusMensagemWtsp(EntradaNumeroWhatsapp pEntrada, EventoMensagemWtzap pMensagem) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public EntradaNumeroWhatsapp getEntradaWhatappBySala(ItfChatSalaBean psala) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Class getClasseRecepcaoByContatoWhatsapp(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Class getClasseDadosContextoWhatsapp(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ItfChatSalaBean getSalaPadrao(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato) throws ErroFalhaGerandoSalaAtendimento {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<ItfSistemaERP> getSistemas() {
        List<ItfSistemaERP> sistemas = new ArrayList<>();
        for (FabSistemasErp sistema : FabSistemasErp.values()) {
            sistemas.add(sistema.getRegistro());
        }
        return sistemas;
    }

}
