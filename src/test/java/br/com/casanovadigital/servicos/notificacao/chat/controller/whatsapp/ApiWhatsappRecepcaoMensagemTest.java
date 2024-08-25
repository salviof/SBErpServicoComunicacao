/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp;

import br.com.casanovadigital.servicos.notificacao.chat.controller.FabSistemasErp;
import br.com.casanovadigital.servicos.notificacao.config.ConfigCoreCNDNotificacaoContato;
import br.org.coletivoJava.fw.api.erp.erpintegracao.contextos.ERPIntegracaoSistemasApi;
import br.org.coletivoJava.fw.api.erp.erpintegracao.model.ItfSistemaERPLocal;

import br.org.coletivoJava.fw.api.erp.erpintegracao.servico.ItfIntegracaoERP;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.SistemaERPConfiavel;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.parametros.ParametroListaRestful;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.integracoes.restInterprestfull.api.FabIntApiRestIntegracaoERPRestfull;
import com.super_bits.modulos.SBAcessosModel.fabricas.FabSegurancaGruposPadrao;
import com.super_bits.modulos.SBAcessosModel.model.UsuarioSB;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.api.token.ItfTokenGestaoOauth;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.ItfResposta;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivojava.fw.api.tratamentoErros.FabErro;
import org.junit.Test;
import static org.junit.Assert.*;
import testes.testesSupers.ServicoRecepcaoOauthTestes;

/**
 *
 * @author salvio
 */
public class ApiWhatsappRecepcaoMensagemTest {

    public ApiWhatsappRecepcaoMensagemTest() {
    }

    /**
     * Test of validarParamentros method, of class ApiWhatsappRecepcaoMensagem.
     */
    @Test
    public void testValidarParamentros() throws Exception {
        try {
            System.out.println("validarParamentros");
            SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.DESENVOLVIMENTO);
            System.out.println(SBCore.getNomeProjeto());
            System.out.println(SBCore.getGrupoProjeto());
            SistemaERPConfiavel sistemaFatura = (SistemaERPConfiavel) FabSistemasErp.FATURA_CASANOVA.getRegistro();
            Map<String, String> parametros = new HashMap<>();
            ItfIntegracaoERP integracao = ERPIntegracaoSistemasApi.RESTFUL.getImplementacaoDoContexto();
            ItfSistemaERPLocal sistemaTestes = integracao.getSistemaAtual();
            System.out.println(sistemaTestes.getHashChavePublica());
            System.out.println(sistemaTestes.getDominio());
            System.out.println(sistemaTestes.getUrlPublicaEndPoint());
            ParametroListaRestful parametro = new ParametroListaRestful();
            parametro.setPagina(0);
            parametro.setFiltros(new HashMap<>());
            parametro.getFiltros().put("cpfCnpj", "06321298670");
            UsuarioSB usuario = new UsuarioSB();
            usuario.setNome("Financeiro");
            usuario.setEmail("financeiro@casanovadigital.com.br");
            usuario.setGrupo(FabSegurancaGruposPadrao.GRUPO_ADMINISTRADOR.getRegistro());
            SBCore.getServicoSessao().getSessaoAtual().setUsuario(usuario);
            System.out.println(sistemaFatura.getUrlRecepcaoCodigo());
            ItfTokenGestaoOauth gestaoToken = (ItfTokenGestaoOauth) FabIntApiRestIntegracaoERPRestfull.ACOES_GET_OPCOES.getGestaoToken(sistemaFatura);

            ServicoRecepcaoOauthTestes.iniciarServico();
            if (!gestaoToken.isTemTokemAtivo()) {
                gestaoToken.gerarNovoToken();
            }
            //   ItfResposta respostaListaClientes = ERPIntegracaoSistemasApi.RESTFUL.getImplementacaoDoContexto().getResposta(sistemaFatura, "FabAcaoMktFaturamentoAdmin.CLIENTE_FRM_LISTAR", parametro);
            //  System.out.println(respostaListaClientes.getRetorno());
            ParametroListaRestful parametroEquipe = new ParametroListaRestful();
            parametroEquipe.setId(1);
            parametroEquipe.setPagina(0);
            parametroEquipe.setFiltros(new HashMap<>());
            parametroEquipe.getFiltros().put("id", 1);
            parametroEquipe.setAtributo("timeAtual");

            ItfResposta respostaLista = ERPIntegracaoSistemasApi.RESTFUL.getImplementacaoDoContexto().getResposta(sistemaFatura, "FabAcaoMktFaturamentoAdmin.ESCALACAO_TIME_CLIENTE_FRM_LISTAR", parametroEquipe);
            System.out.println(respostaLista.getRetorno());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha com conexão restfull", ex);
            }

            ApiWhatsappRecepcaoMensagem instance = new ApiWhatsappRecepcaoMensagem();
            instance.executarRegraDeNegocio();
            // TODO review the generated test code and remove the default call to fail.
            fail("The test case is a prototype.");
        } catch (ErroRegraDeNegocio ex) {
            Logger.getLogger(ApiWhatsappRecepcaoMensagemTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ErroRecursoNaoEncontrado ex) {
            Logger.getLogger(ApiWhatsappRecepcaoMensagemTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ErroConexaoSistemaTerceiro ex) {
            Logger.getLogger(ApiWhatsappRecepcaoMensagemTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
