/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller;

import br.com.casanovadigital.servicos.notificacao.config.ConfigCoreCNDNotificacaoContato;
import br.org.coletivoJava.fw.api.erp.erpintegracao.contextos.ERPIntegracaoSistemasApi;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.parametros.ParametroListaRestful;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.ItfResposta;
import com.super_bits.modulosSB.SBCore.modulos.objetos.registro.Interfaces.basico.ItfUsuario;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author salvio
 */
public class UtilAgenciaContatosTest {

    public UtilAgenciaContatosTest() {
    }

    @Before
    public void teste() {
        SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.HOMOLOGACAO);
    }

    /**
     * Test of getContatoByTelefone method, of class UtilAgenciaContatos.
     */
    public void testGetContatoByTelefone() {

        //  String result = UtilAgenciaContatos.getContatoByTelefone("984175550");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void getCnpjByTelefone() {
        ItfUsuario result = UtilAgenciaContatos.getContatoAtendimentoByTelefone("114354588403482", "+31984178550");
        System.out.println(result.getEmail());
    }

    public void getContatosByCnpj() {
        ParametroListaRestful parametroEquipe = new ParametroListaRestful();
        parametroEquipe.setId(1);
        parametroEquipe.setPagina(0);
        parametroEquipe.setFiltros(new HashMap<>());
        parametroEquipe.getFiltros().put("id", 1);
        parametroEquipe.setAtributo("timeAtual");
        ItfResposta respostaLista = ERPIntegracaoSistemasApi.RESTFUL.getImplementacaoDoContexto()
                .getResposta(FabSistemasErp.FATURA_CASANOVA.getRegistro(),
                        "FabAcaoMktFaturamentoAdmin.ESCALACAO_TIME_CLIENTE_FRM_LISTAR", parametroEquipe);
        System.out.println(respostaLista.getRetorno());
    }

}
