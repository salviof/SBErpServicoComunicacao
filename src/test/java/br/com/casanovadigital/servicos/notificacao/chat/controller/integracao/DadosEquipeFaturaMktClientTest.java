/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.integracao;

import br.com.casanovadigital.servicos.notificacao.InicioAplicacaoWScontatos;
import br.org.coletivoJava.fw.api.erp.erpintegracao.contextos.ERPIntegracaoSistemasApi;
import br.org.coletivoJava.fw.api.erp.erpintegracao.servico.ItfIntegracaoERP;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.SistemaERPAtual;
import br.org.coletivoJava.integracoes.restInterprestfull.api.FabIntApiRestIntegracaoERPRestfull;
import com.super_bits.modulos.SBAcessosModel.model.GrupoUsuarioSB;
import com.super_bits.modulos.SBAcessosModel.model.UsuarioSB;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UTilSBCoreInputs;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.ItensGenericos.basico.GrupoUsuariosDoSistema;
import com.super_bits.modulosSB.SBCore.modulos.objetos.MapaObjetosProjetoAtual;
import java.util.List;
import org.junit.Test;
import testesFW.ConfigCoreJunitPadraoDesenvolvedor;

/**
 *
 * @author salvio
 */
public class DadosEquipeFaturaMktClientTest {

    public DadosEquipeFaturaMktClientTest() {
    }

    /**
     * Test of validarParamentros method, of class DadosEquipeFaturaMktClient.
     */
    @Test
    public void testValidarParamentros() throws Exception {
        SBCore.configurar(new ConfigCoreJunitPadraoDesenvolvedor(), SBCore.ESTADO_APP.HOMOLOGACAO);
        MapaObjetosProjetoAtual.adcionarObjeto(GrupoUsuarioSB.class);
        MapaObjetosProjetoAtual.adcionarObjeto(GrupoUsuariosDoSistema.class);
        MapaObjetosProjetoAtual.adcionarObjeto(UsuarioSB.class);
        new Servico().start();
        Thread.sleep(500);
        ItfIntegracaoERP erp = ERPIntegracaoSistemasApi.RESTFUL.getImplementacaoDoContexto();
        SistemaERPAtual sistemaClienteRef = (SistemaERPAtual) erp.getSistemaAtual();
        boolean teste = false;
        while (teste) {
            Thread.sleep(500);
        }

        System.out.println(sistemaClienteRef.getChavePublica().hashCode());
        System.out.println(sistemaClienteRef.getHashChavePublica());
        List<String> resposta = UTilSBCoreInputs.getStringsByURL("http://localhost:8666/api/v1/teste/contato/?cnpj=32371685000179");
        for (String linha : resposta) {
            System.out.println(linha);
        }
        teste = false;
        while (teste) {
            Thread.sleep(5000000);
        }
    }

    public class Servico extends Thread {

        @Override
        public void run() {
            InicioAplicacaoWScontatos.main(null);
        }

    }
}
