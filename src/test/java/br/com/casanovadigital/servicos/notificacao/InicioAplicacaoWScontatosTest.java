/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao;

import br.com.casanovadigital.servicos.notificacao.config.ConfigCoreCNDNotificacaoContato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author salvio
 */
public class InicioAplicacaoWScontatosTest {

    public InicioAplicacaoWScontatosTest() {
    }

    /**
     * Test of main method, of class InicioAplicacaoWScontatos.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        System.out.println("main");

        SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.HOMOLOGACAO);

        InicioAplicacaoWScontatos.main(args);
        while (!SBCore.isEmModoProducao()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                System.out.println("Thread de execução interrompida");
                Logger.getLogger(InicioAplicacaoWScontatos.class.getName()).log(Level.FINE, null, ex);
            }
        }
        // TODO review the generated test code and remove the default call to fail.

    }
}
