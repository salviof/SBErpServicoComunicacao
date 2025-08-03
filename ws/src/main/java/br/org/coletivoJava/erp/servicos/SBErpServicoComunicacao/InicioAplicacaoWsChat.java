/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config.ConfigCoreCNDNotificacaoContato;

import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import java.util.logging.Level;
import java.util.logging.Logger;
import spark.Spark;

/**
 *
 * @author sfurbino
 */
public class InicioAplicacaoWsChat {

    public static void main(String[] args) {
        if (!SBCore.isAmbienteCoreConfigurado()) {
            SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.PRODUCAO);
        }

        Spark.port(8666);

//        ServicoNotificacaoContato.iniciarServico();
        AplicacaoWsChat.iniciarAplicacao();
        Spark.awaitStop();

        while (SBCore.isEmModoProducao()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                System.out.println("Thread de execução interrompida");
                Logger.getLogger(InicioAplicacaoWsChat.class.getName()).log(Level.FINE, null, ex);
            }
        }
    }
}
