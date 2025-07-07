/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.interpretadormsg;

import br.com.casanovadigital.servicos.chat.AplicacaoWsChat;
import br.com.casanovadigital.servicos.chat.interpretadormsg.interfaces.ItfCentralLogicasProcessamentoMsg;
import br.com.casanovadigital.servicos.chat.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import java.lang.reflect.InvocationTargetException;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.com.casanovadigital.servicos.chat.interpretadormsg.interfaces.ItfProcessadorMensagemWhatsapp;
import br.com.casanovadigital.servicos.chat.interpretadormsg.interfaces.ItfProcessadorEventoWhatsapp;
import br.com.casanovadigital.servicos.chat.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;

/**
 *
 * @author salvio
 */
public class FabProcessadorMensagensEstatus {

    public static ItfProcessadorMensagemWhatsapp getProcessadorMensageRecebidaPeloWhatsapp(MensagemWhatsapp pPacote) {
        Class classe = AplicacaoWsChat.getCentralLogicaProcesasmento().getClasseProcessadorMensagemContatoViaWhataspp(pPacote);
        ItfProcessadorMensagemWhatsapp processador;

        try {
            processador = (ItfProcessadorMensagemWhatsapp) classe.getConstructor(MensagemWhatsapp.class).newInstance(pPacote);

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new UnsupportedOperationException("impossível processar pacote vindo do whatsapp;" + ex.getClass().getSimpleName() + "" + ex.getMessage());
        }

        return processador;
    }

    public static ItfProcessadorEventoWhatsapp getProcessadorEventoWhatsapp(EventoMensagemWtzap pStatus) {
        Class classe = AplicacaoWsChat.getCentralLogicaProcesasmento().getClasseProcessadorEventoRecebidoPeloWhatsapp(pStatus);
        ItfProcessadorEventoWhatsapp processador;

        try {
            processador = (ItfProcessadorEventoWhatsapp) classe.getConstructor(EventoMensagemWtzap.class).newInstance(pStatus);

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new UnsupportedOperationException("impossível processar pacote vindo do whatsapp;");
        }

        return processador;
    }

}
