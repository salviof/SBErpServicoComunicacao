/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.interpretadormsg.interfaces;

import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.chat.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import br.com.casanovadigital.servicos.chat.legado.contextoChat.whatsapp.ContextoWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.modulosSB.SBCore.modulos.erp.ItfSistemaERP;
import java.util.List;

/**
 *
 * @author salvio
 */
public interface ItfCentralLogicasProcessamentoMsg {

    public Class<? extends ItfProcessadorMensagemWhatsapp> getClasseProcessadorMensagemContatoViaWhataspp(MensagemWhatsapp pMensagemWhatsapp);

    public Class<? extends ItfProcessadorEventoWhatsapp> getClasseProcessadorEventoRecebidoPeloWhatsapp(EventoMensagemWtzap pPacote);

    public Class getClasseDadosContextoWhatsapp(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato);

    public ContextoWhatsapp gerarNovoCotextoByMensagem(EntradaNumeroWhatsapp pEntrada, MensagemWhatsapp pMensagem);

    public ContextoWhatsapp gerarNovoCotexto(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pMensagem);

    public ContextoWhatsapp gerarNovoContextoByStatusMensagemWtsp(EntradaNumeroWhatsapp pEntrada, EventoMensagemWtzap pMensagem);

    public EntradaNumeroWhatsapp getEntradaWhatappBySala(ItfChatSalaBean psala);

    public Class getClasseRecepcaoByContatoWhatsapp(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato);

//    public Class getClasseDadosContextoIntranetAtendimento(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato);
    public ItfChatSalaBean getSalaPadrao(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato) throws ErroFalhaGerandoSalaAtendimento;

    public ItfUsuarioChat getUsuarioAtendimentoPadrao(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato);

    public List<ItfSistemaERP> getSistemas();

}
