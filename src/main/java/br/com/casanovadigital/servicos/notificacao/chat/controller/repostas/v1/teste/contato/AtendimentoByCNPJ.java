/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.repostas.v1.teste.contato;

import br.com.casanovadigital.servicos.notificacao.chat.controller.FabSistemasErp;
import br.org.coletivoJava.fw.api.erp.erpintegracao.contextos.ERPIntegracaoSistemasApi;
import br.org.coletivoJava.fw.api.erp.erpintegracao.servico.ItfIntegracaoERP;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.ApiIntegracaoRestfulimpl;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.SistemaERPAtual;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.SistemaERPConfiavel;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.parametros.ParametroListaRestful;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import com.super_bits.modulos.SBAcessosModel.fabricas.FabSegurancaGruposPadrao;
import com.super_bits.modulos.SBAcessosModel.model.UsuarioSB;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJsonRest;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.ItfResposta;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import java.util.HashMap;

/**
 *
 * @author salvio
 */
public class AtendimentoByCNPJ extends RotaSparkPadrao {

    private static final SistemaERPConfiavel sistemaFatura = (SistemaERPConfiavel) FabSistemasErp.FATURA_CASANOVA.getRegistro();
    private String cnpj;

    @Override
    public void validarParamentros() throws ErroParamentosInvalidos {
        cnpj = requisicao.raw().getParameter("cnpj");
    }

    @Override
    public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
        try {

            System.out.println("Hash sistema fatura= " + sistemaFatura.getHashChavePublica());

            ItfIntegracaoERP erp = ERPIntegracaoSistemasApi.RESTFUL.getImplementacaoDoContexto();
            SistemaERPAtual sistemaClinete = (SistemaERPAtual) erp.getSistemaAtual();
            System.out.println(sistemaClinete.getHashChavePublica());

            ParametroListaRestful parametroEquipe = new ParametroListaRestful();
            parametroEquipe.setId(0);
            parametroEquipe.setPagina(0);
            parametroEquipe.setFiltros(new HashMap<>());
            //parametroEquipe.getParametros().put("id", 1);
            parametroEquipe.getFiltros().put("cpfCnpj", cnpj);
            parametroEquipe.setAtributo("escalacaoOficial");
            if (!SBCore.getUsuarioLogado().getEmail().equals("financeiro@casanovadigital.com.br")) {
                UsuarioSB usuario = new UsuarioSB();
                usuario.setNome("Financeiro");
                usuario.setEmail("financeiro@casanovadigital.com.br");
                usuario.setGrupo(FabSegurancaGruposPadrao.GRUPO_ADMINISTRADOR.getRegistro());
                SBCore.getServicoSessao().getSessaoAtual().setUsuario(usuario);
            }
            ApiIntegracaoRestfulimpl interacaoERP = (ApiIntegracaoRestfulimpl) ERPIntegracaoSistemasApi.RESTFUL.getImplementacaoDoContexto();
            ItfResposta respostaLista = interacaoERP
                    .getResposta(sistemaFatura, "FabAcaoMktFaturamentoAdmin.CLIENTE_FRM_LISTAR", parametroEquipe);
            if (respostaLista.isSucesso()) {
                return respostaLista.getRetorno().toString();
            } else {
                if (!respostaLista.getMensagens().isEmpty()) {
                    return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Erro obtendo dados" + respostaLista.getMensagens().get(0).getMenssagem()).build());
                } else {
                    return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha("Erro obtendo dados").build());
                }
            }

        } catch (Throwable t) {
            throw new ErroConexaoSistemaTerceiro("Erro não tratado" + t.getMessage());
        }
    }

}
