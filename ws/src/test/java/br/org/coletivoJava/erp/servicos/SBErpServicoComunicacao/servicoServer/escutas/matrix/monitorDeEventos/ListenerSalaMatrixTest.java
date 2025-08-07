/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos;

import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author salvio
 */
public class ListenerSalaMatrixTest {

    public ListenerSalaMatrixTest() {
    }

    /**
     * Test of isElegivel method, of class ListenerSalaMatrix.
     */
    @Test
    public void testIsElegivel() {
        System.out.println("isElegivel");
        ItfEventoMatix pEvento = null;
        ListenerSalaMatrix instance = null;
        boolean expResult = false;
        boolean result = instance.isElegivel(pEvento);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
