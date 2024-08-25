/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.statusMensagem.StatusMensagemWtzap;
import br.com.casanovadigital.servicos.notificacao.contextoChat.whatsapp.ContextoWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;

/**
 *
 * @author salvio
 */
public interface ItfDefinicaoContextoWhatsapp {

    public ContextoWhatsapp gerarNovoCotextoByMensagem(EntradaNumeroWhatsapp pEntrada, MensagemWhatsapp pMensagem);

    public ContextoWhatsapp gerarNovoCotexto(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pMensagem);

    public ContextoWhatsapp gerarNovoContextoByStatusMensagemWtsp(EntradaNumeroWhatsapp pEntrada, StatusMensagemWtzap pMensagem);

    public EntradaNumeroWhatsapp getEntradaWhatappBySala(ItfChatSalaBean psala);

    public Class getClasseRecepcaoByContatoWhatsapp(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato);

    public Class getClasseDadosContextoWhatsapp(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato);

    public Class getClasseDadosContextoIntranetAtendimento(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato);

    public ItfChatSalaBean getSalaPadrao(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato) throws ErroFalhaGerandoSalaAtendimento;

    public ItfUsuarioChat getUsuarioAtendimentoPadrao(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato);

}
