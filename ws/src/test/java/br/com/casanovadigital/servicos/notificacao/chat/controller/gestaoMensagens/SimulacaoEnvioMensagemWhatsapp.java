/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens;

import br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens.ItfExecucaoLogicaRecepcaoWtzap;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ErroCriandoContato;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author salvio
 */
public class SimulacaoEnvioMensagemWhatsapp extends Thread {

    private final String codigoMensagemWhatsapp;
    private String codigoMensagemMatrix;
    private boolean finalizado;
    private final MensagemWhatsapp msg;
    EntradaNumeroWhatsapp entrada;

    public SimulacaoEnvioMensagemWhatsapp(String pTextoWhatsapp) throws ErroCriandoContato {

        PacoteMemensagemRecebidoWhatsapp pacote = new PacoteMemensagemRecebidoWhatsapp(pTextoWhatsapp);
        msg = pacote.getMensagens().get(0);
        entrada = MotorControleDeMensagens.getEntradaByCodigoEntrada(msg.getCodigoContaConectada());

        codigoMensagemWhatsapp = msg.getId();
    }

    @Override
    public void run() {
        MotorControleDeMensagens.executarMensagemWhatsapp(entrada, msg);
        while (!finalizado) {
            ItfExecucaoLogicaRecepcaoWtzap exec = MotorControleDeMensagens.monitorGetProcessamentoWhatsappByMesagem(msg);
            if (exec != null && exec.isEncaminhada()) {

                codigoMensagemMatrix = MotorControleDeMensagens.getControleRecibo().getCodigoEventoMatrixByCodigoWhatsapp(msg.getId());

                finalizado = codigoMensagemMatrix != null;
            }
            try {
                if (!finalizado) {
                    sleep(10000);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(SimulacaoEnvioMensagemWhatsapp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String getCodigoMensagemWhatsapp() {
        return codigoMensagemWhatsapp;
    }

    public String aguardarReciboMatrix() {
        while (!finalizado) {
            try {
                sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(SimulacaoEnvioMensagemWhatsapp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return codigoMensagemMatrix;
    }

}
