/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.config;

import br.com.casanovadigital.servicos.notificacao.chat.controller.FabSistemasErp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens.ItfDefinicaoContextoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.statusMensagem.StatusMensagemWtzap;
import br.com.casanovadigital.servicos.notificacao.contextoChat.whatsapp.ContextoWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.modulosSB.SBCore.modulos.erp.ItfSistemaERP;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salvio
 */
public class DefinicaoContextosWtzap implements ItfDefinicaoContextoWhatsapp {

    public DefinicaoContextosWtzap() {
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
    public ContextoWhatsapp gerarNovoContextoByStatusMensagemWtsp(EntradaNumeroWhatsapp pEntrada, StatusMensagemWtzap pMensagem) {
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
    public Class getClasseDadosContextoIntranetAtendimento(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ItfChatSalaBean getSalaPadrao(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato) throws ErroFalhaGerandoSalaAtendimento {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ItfUsuarioChat getUsuarioAtendimentoPadrao(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato) {
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
