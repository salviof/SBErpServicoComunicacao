/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller;

import br.org.coletivoJava.fw.api.erp.chat.ERPChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import junit.framework.Assert;
import org.junit.Test;
import testesFW.ConfigCoreJunitPadraoDevAcaoPermissao;

/**
 *
 * @author salvio
 */
public class AtendentesMatrixTest {

    public AtendentesMatrixTest() {
    }

    @Test
    public void testSomeMethod() {
        SBCore.configurar(new ConfigCoreJunitPadraoDevAcaoPermissao(), SBCore.ESTADO_APP.DESENVOLVIMENTO);
        MapAtendentesMatrixCAsanovadigital atendente = new MapAtendentesMatrixCAsanovadigital();
        ItfUsuarioChat usuario = MapAtendentesMatrixCAsanovadigital.getUserAtendimentoByEmail(MapAtendentesMatrixCAsanovadigital.CODIGO_ENTRADA_TELEVENDAS_CASANOVA_BH9755, "salvio@casanovadigital.com.br");
        Assert.assertNotNull("Esperado um usuario", usuario);
        System.out.println(usuario.getNome());

    }

}
