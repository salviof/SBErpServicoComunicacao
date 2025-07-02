/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.casanovadigital.servicos.notificacao;

import br.com.casanovadigital.servicos.notificacao.chat.ServicoNotificacaoChat;
import br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens.MotorControleDeMensagens;
import br.com.casanovadigital.servicos.notificacao.config.ConfigCoreCNDNotificacaoContato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivojava.fw.api.tratamentoErros.FabErro;
import spark.Spark;

/**
 *
 * @author sfurbino
 */
public class InicioAplicacaoWScontatos {

    public static void main(String[] args) {
        if (!SBCore.isAmbienteCoreConfigurado()) {
            SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.PRODUCAO);
        }
        if (SBCore.isEmModoProducao()) {
            Spark.staticFiles.externalLocation("/home/git/publicados/agenciaForms/paginas/");
        } else {
            Spark.staticFiles.externalLocation("/home/superBits/projetos/Casa_Nova/source/agenciaForms/ws/src/main/resources/chatClient/app/");

        }

        Spark.port(8666);

//        ServicoNotificacaoContato.iniciarServico();
        try {
            ServicoNotificacaoChat.iniciarServico();
            MotorControleDeMensagens.configurarMotor();
        } catch (Throwable t) {
            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "falha iniciando serviço", t);
        }

        Spark.awaitInitialization();
        Spark.awaitStop();
        Spark.awaitStop();
        while (SBCore.isEmModoProducao()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                System.out.println("Thread de execução interrompida");
                Logger.getLogger(InicioAplicacaoWScontatos.class.getName()).log(Level.FINE, null, ex);
            }
        }
    }
}
