/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.chat.controller.whatsapp.statusMensagem;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import jakarta.json.JsonObject;
import org.junit.Test;
import static org.junit.Assert.*;
import testesFW.ConfigCoreJunitPadraoDevAcaoPermissao;

/**
 *
 * @author salvio
 */
public class FabTipoStatusMensagemTest {

    public FabTipoStatusMensagemTest() {
    }

    /**
     * Test of values method, of class FabTipoStatusMensagem.
     */
    /**
     * Test of gerarStatusMensgem method, of class FabTipoStatusMensagemWhtzap.
     */
    @Test
    public void testGerarStatusMensgem() {

        SBCore.configurar(new ConfigCoreJunitPadraoDevAcaoPermissao(), SBCore.ESTADO_APP.DESENVOLVIMENTO);

        JsonObject pJson = null;
        EventoMensagemWtzap expResult = null;
//        EventoMensagemWtzap result = FabTipoStatusMensagemWhtzap.gerarStatusMensgem(pJson);
        //      assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
