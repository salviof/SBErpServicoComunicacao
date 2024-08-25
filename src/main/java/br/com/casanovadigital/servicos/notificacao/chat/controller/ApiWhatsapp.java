/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.ApiWhatsappRecepcaoMensagem;
import br.org.coletivoJava.fw.api.erp.erpintegracao.contextos.ERPIntegracaoSistemasApi;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.SistemaERPConfiavel;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.parametros.ParametroListaRestful;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import br.org.coletivoJava.integracoes.restInterprestfull.api.FabIntApiRestIntegracaoERPRestfull;
import com.super_bits.modulos.SBAcessosModel.fabricas.FabSegurancaGruposPadrao;
import com.super_bits.modulos.SBAcessosModel.model.UsuarioSB;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJsonRest;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.api.token.ItfTokenGestaoOauth;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.ItfResposta;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.util.Date;
import java.util.HashMap;
import spark.Route;

/**
 *
 * @author salvio
 */
public class ApiWhatsapp {

    private final static SistemaERPConfiavel sistemaFatura = (SistemaERPConfiavel) FabSistemasErp.FATURA_CASANOVA.getRegistro();

    public static Route recepcaoNotificacao() {
        return new ApiWhatsappRecepcaoMensagem();
    }

    public static Route testesResponsaveisPeloCNPJ() {
        return new RotaSparkPadrao() {
            @Override
            public void validarParamentros() throws ErroParamentosInvalidos {
                requisicao.attribute("");
            }

            @Override
            public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
                if (SBCore.getUsuarioLogado().getEmail().equals("financeiro@casanovadigital.com.br")) {
                    UsuarioSB usuario = new UsuarioSB();
                    usuario.setNome("Financeiro");
                    usuario.setEmail("financeiro@casanovadigital.com.br");
                    usuario.setGrupo(FabSegurancaGruposPadrao.GRUPO_ADMINISTRADOR.getRegistro());
                    SBCore.getServicoSessao().getSessaoAtual().setUsuario(usuario);
                }
                ItfTokenGestaoOauth gestaoToken = (ItfTokenGestaoOauth) FabIntApiRestIntegracaoERPRestfull.ACOES_GET_OPCOES.getGestaoToken(sistemaFatura);
                if (!gestaoToken.isTemTokemAtivo()) {
                    System.out.println("Não tem token para o fatura ativo");
                    System.out.println("Solicitando novo token");
                    gestaoToken.gerarNovoToken();
                }
                int tentativas = 0;
                while (!gestaoToken.isTemTokemAtivo()) {
                    System.out.print("Aguardando token " + new Date());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {

                    }
                    if (tentativas > 10) {
                        throw new ErroConexaoSistemaTerceiro("Falha conectando com sistema fatura em " + sistemaFatura.getUrlPublicaEndPoint() + " o sistema aguardou o token por 10 segundos");
                    }
                }
                ParametroListaRestful parametroEquipe = new ParametroListaRestful();
                parametroEquipe.setId(1);
                parametroEquipe.setPagina(0);
                parametroEquipe.setFiltros(new HashMap<>());
                parametroEquipe.getFiltros().put("id", 1);
                parametroEquipe.setAtributo("timeAtual");
                ItfResposta respostaLista = ERPIntegracaoSistemasApi.RESTFUL.getImplementacaoDoContexto().getResposta(sistemaFatura, "FabAcaoMktFaturamentoAdmin.ESCALACAO_TIME_CLIENTE_FRM_LISTAR", parametroEquipe);

                return respostaLista.getRetorno().toString();
            }
        };
    }

    public static Route recepcaoEvento() {

        return new RotaSparkPadrao() {
            private String telefone;

            @Override
            public void validarParamentros() throws ErroParamentosInvalidos {
                System.out.println(requisicao.body());
                telefone = requisicao.attribute("telefone");
            }

            @Override
            public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {

                try {
                    //   UtilAgenciaContatos.getContatoByTelefone(telefone);
                    JsonObjectBuilder resp = Json.createObjectBuilder();
                    JsonObject json = UtilSBCoreJson.getJsonObjectByTexto(requisicao.body());
                    resp.add("retorno", json);
                    return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseSucesso(resp.build()).build());
                } catch (Throwable t) {
                    throw new ErroConexaoSistemaTerceiro("Erro não tratado" + t.getMessage());
                }

            }
        };
    }

    public static Route envioMensagem() {

        return new RotaSparkPadrao() {
            private String telefone;

            @Override
            public void validarParamentros() throws ErroParamentosInvalidos {
                System.out.println("Envio mensagem");
                System.out.println();
            }

            @Override
            public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {

                return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseSucesso(JsonValue.EMPTY_JSON_OBJECT).build());

            }
        };
    }
}
