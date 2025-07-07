/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.chat.controller.gestaoMensagens;

import br.com.casanovadigital.servicos.chat.config.ConfigCoreCNDNotificacaoContato;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.gestaoMensagens.MotorControleDeMensagens;
import br.com.casanovadigital.servicos.chat.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.contato.ErroCriandoContato;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.chat.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import java.util.logging.Level;
import java.util.logging.Logger;
import testesFW.TesteJunit;

/**
 *
 * @author salvio
 */
public class TesteInputWhatsapp extends TesteJunit {

    public static boolean simularEnvioEventoWhatsapp(String pJson) {
        new TesteRepcaoWhatsapp(pJson).start();
        return true;
    }

    public void executarSequencia(String... pacotesMensagens) {

        //pacote1, pacote2, pacote3, pacote4, pacote5, pacote6,pacote7 ,pacote8, pacote9, pacote10);
        int i = 0;

        for (String pacote : pacotesMensagens) {

            simularEnvioEventoWhatsapp(pacote);

            try {
                Thread.sleep(20000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MotorControleDeMensagensTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    protected void configAmbienteDesevolvimento() {
        SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.HOMOLOGACAO);
        MotorControleDeMensagens.configurarMotor();
    }

    public static class TesteRepcaoWhatsapp extends Thread {

        private String corpoRequisicao;

        public TesteRepcaoWhatsapp(String pCorpo) {
            corpoRequisicao = pCorpo;
        }

        @Override
        public void run() {
            PacoteMemensagemRecebidoWhatsapp msg;
            try {
                msg = new PacoteMemensagemRecebidoWhatsapp(corpoRequisicao);
                if (!msg.getMensagens().isEmpty()) {
                    MensagemWhatsapp msgWatzapp = msg.getMensagens().get(0);
                    EntradaNumeroWhatsapp entrada = MotorControleDeMensagens.getEntradaByCodigoEntrada(msgWatzapp.getCodigoContaConectada());

                    MotorControleDeMensagens.executarMensagemWhatsapp(entrada, msgWatzapp);
                }
                if (!msg.getStatusMensagem().isEmpty()) {

                    for (EventoMensagemWtzap status : msg.getStatusMensagem()) {

                        MotorControleDeMensagens.executarMensagemStatusWhatsapp(status.getEntrada(), status);
                    }

                }
            } catch (ErroCriandoContato ex) {
                Logger.getLogger(TesteInputWhatsapp.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

}
