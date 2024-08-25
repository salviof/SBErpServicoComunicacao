/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.contato;

import br.com.casanovadigital.servicos.notificacao.config.ConfigCoreCNDNotificacaoContato;
import com.amazonaws.services.sns.AmazonSNS;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author salvio
 */
public class ServicoNotificacaoContatoTest {

    public ServicoNotificacaoContatoTest() {
    }

    /**
     * Test of iniciarServico method, of class ServicoNotificacaoContato.
     */
    @Test
    public void testIniciarServico() {
        SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.HOMOLOGACAO);
        ServicoNotificacaoContato.iniciarServico();
        // TODO review the generated test code and remove the default call to fail.
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServicoNotificacaoContatoTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
