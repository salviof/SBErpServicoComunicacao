/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.chat;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config.ConfigCoreServicoComunicacao;
import com.super_bits.casanovadigital.servicos.messagens.model.configModel.ConfigPercistenciaServicoComunicacao;
import com.super_bits.modulosSB.Persistencia.ConfigGeral.SBPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author salvio
 */
public class AplicacaoWsChatTest {

    public AplicacaoWsChatTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    /**
     * Test of getCentralLogicaProcesasmento method, of class AplicacaoWsChat.
     */
    @Test
    public void testGetCentralLogicaProcesasmento() throws Exception {

        SBCore.configurar(new ConfigCoreServicoComunicacao(), SBCore.ESTADO_APP.DESENVOLVIMENTO);
        SBPersistencia.configuraJPA(new ConfigPercistenciaServicoComunicacao());
        AplicacaoWsChat.iniciarAplicacao();

        while (true) {
            Thread.sleep(10000);
        }
    }

}
