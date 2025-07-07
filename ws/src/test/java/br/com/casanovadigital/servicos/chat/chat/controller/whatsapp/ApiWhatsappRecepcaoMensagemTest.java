/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.chat.controller.whatsapp;

import br.com.casanovadigital.servicos.chat.config.ConfigCoreCNDNotificacaoContato;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.ApiWhatsappRecepcaoMensagem;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.FabSistemasErp;
import br.org.coletivoJava.fw.api.erp.erpintegracao.contextos.ERPIntegracaoSistemasApi;
import br.org.coletivoJava.fw.api.erp.erpintegracao.model.ItfSistemaERPLocal;

import br.org.coletivoJava.fw.api.erp.erpintegracao.servico.ItfIntegracaoERP;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.SistemaERPConfiavel;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.parametros.ParametroListaRestful;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import com.super_bits.modulos.SBAcessosModel.fabricas.FabSegurancaGruposPadrao;
import com.super_bits.modulos.SBAcessosModel.model.UsuarioSB;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
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

            //    OAUTH 2
            //    1- > Cliente Envia uma solicitação para obter chave de acesso.
            //    2-> Servidor Recebe a solicitação, e apresenta uma tela de login informando quais acessos vão ser disponibilizados.
            //   3-> Servidor Atutentica o usuario e senha e retonarna O cliente para umagina com O ID do pedidido de acesso.
            //   3-> cliente receber o código de pedido, e dá um post no servidor com a chave de pededido, e obtem um token de acesso como resposta.
            System.out.println("validarParamentros");
            SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.DESENVOLVIMENTO);
            System.out.println(SBCore.getNomeProjeto());
            System.out.println(SBCore.getGrupoProjeto());
            SistemaERPConfiavel sistemaFatura = (SistemaERPConfiavel) FabSistemasErp.FATURA_CASANOVA.getRegistro();
            Map<String, String> parametros = new HashMap<>();

            ItfIntegracaoERP erpFaturaIntegracao = ERPIntegracaoSistemasApi.RESTFUL.getImplementacaoDoContexto();

            System.out.println(erpFaturaIntegracao.getSistemaAtual().getChavePrivada());
            System.out.println("_____________________________________");
            System.out.println(erpFaturaIntegracao.getSistemaAtual().getChavePublica());
            System.out.println("_____________________________________");
            ServicoRecepcaoOauthTestes.iniciarServico();

            erpFaturaIntegracao.gerarTokenSistemaComoAdmin(sistemaFatura);

            ItfSistemaERPLocal sistemaTestes = erpFaturaIntegracao.getSistemaAtual();
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

            //   ItfResposta respostaListaClientes = ERPIntegracaoSistemasApi.RESTFUL.getImplementacaoDoContexto().getResposta(sistemaFatura, "FabAcaoMktFaturamentoAdmin.CLIENTE_FRM_LISTAR", parametro);
            //  System.out.println(respostaListaClientes.getRetorno());
            ParametroListaRestful parametroEquipe = new ParametroListaRestful();
            parametroEquipe.setId(1l);
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
