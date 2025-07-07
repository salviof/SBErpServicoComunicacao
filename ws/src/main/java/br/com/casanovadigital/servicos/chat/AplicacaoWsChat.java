/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat;

import br.com.casanovadigital.servicos.chat.interpretadormsg.interfaces.ItfCentralLogicasProcessamentoMsg;
import br.com.casanovadigital.servicos.chat.logdeMensagens.RepositorioComunicacaoChat;
import br.org.coletivoJava.fw.api.erp.chat.ERPChat;
import br.org.coletivoJava.fw.api.erp.chat.ItfErpChatService;
import java.util.ServiceLoader;

/**
 *
 * @author salvio
 */
public class AplicacaoWsChat {

    private static ItfCentralLogicasProcessamentoMsg centralLogicasDeProcessamento;
    public static final ItfErpChatService SERVICO_MATRIX = ERPChat.MATRIX_ORG.getImplementacaoDoContexto();
    public static final RepositorioComunicacaoChat REPOSITORIO_COMUNICACAO_CHAT = new RepositorioComunicacaoChat();

    public static ItfCentralLogicasProcessamentoMsg getCentralLogicaProcesasmento() {
        if (centralLogicasDeProcessamento == null) {
            ServiceLoader<ItfCentralLogicasProcessamentoMsg> services = ServiceLoader.load(ItfCentralLogicasProcessamentoMsg.class);
            centralLogicasDeProcessamento = services.iterator().next();
        }
        return centralLogicasDeProcessamento;
    }

}
