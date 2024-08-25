/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.salaChat;

import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import testesFW.ConfigCoreJunitPadraoDesenvolvedor;

/**
 *
 * @author salvio
 */
public class ApiSalaChatMembrosTest {

    public ApiSalaChatMembrosTest() {
    }

    /**
     * Test of validarParamentros method, of class ApiSalaChatMembros.
     */
    @Test
    public void testValidarParamentros() throws Exception {
        SBCore.configurar(new ConfigCoreJunitPadraoDesenvolvedor(), SBCore.ESTADO_APP.DESENVOLVIMENTO);
        System.out.println("validarParamentros");
        ApiSalaChatMembros instance = new ApiSalaChatMembros();
        instance.setCodigoSala("!dzWzluEMaVaVMuMwXi:casanovadigital.com.br");
        try {
            instance.executarRegraDeNegocio();
        } catch (ErroRegraDeNegocio ex) {
            Logger.getLogger(ApiSalaChatMembrosTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ErroRecursoNaoEncontrado ex) {
            Logger.getLogger(ApiSalaChatMembrosTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ErroConexaoSistemaTerceiro ex) {
            Logger.getLogger(ApiSalaChatMembrosTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        fail("The test case is a prototype.");
    }

}
