/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller;

import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringFiltros;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.FabTipoConexaoRest;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.RespostaWebServiceSimples;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.implementacao.UtilSBApiRestClient;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author salvio
 */
public class UtilAgenciaContatos {

    private static final Map<String, Map<String, ItfUsuarioChat>> ATENDENTE_BY_LEAD = new HashMap<>();

    private static void registrarAtendenteLead(String pCodigo, String pTelefonelEAD, ItfUsuarioChat pUsuarioAtendimento) {
        if (!ATENDENTE_BY_LEAD.containsKey(pCodigo)) {
            ATENDENTE_BY_LEAD.put(pCodigo, new HashMap<>());
        }
        ATENDENTE_BY_LEAD.get(pCodigo).put(pTelefonelEAD, pUsuarioAtendimento);
    }

    public static ItfUsuarioChat getContatoAtendimentoByTelefone(String pCodigoOrigem, String pTelefoneLead) {
        Map<String, String> cabecalho = new HashMap<>();
        cabecalho.put("telefone", pTelefoneLead);

        if (ATENDENTE_BY_LEAD.containsKey(pCodigoOrigem)) {
            if (ATENDENTE_BY_LEAD.get(pCodigoOrigem).containsKey(pTelefoneLead)) {
                return ATENDENTE_BY_LEAD.get(pCodigoOrigem).get(pTelefoneLead);
            }
        }

        RespostaWebServiceSimples resposta = UtilSBApiRestClient.
                getRespostaRest("https://crm.casanovadigital.com.br/documentoByTelefone", FabTipoConexaoRest.GET, false, cabecalho, null);

        if (!resposta.isSucesso()) {
            return MapAtendentesMatrixCAsanovadigital.getUsuarioAtendimentoPadrao(pCodigoOrigem);
        }
        if (resposta.isSucesso()) {
            System.out.println(resposta.getRespostaTexto());

            if (resposta.getRespostaComoObjetoJson().containsKey("documento") && !resposta.getRespostaComoObjetoJson().getString("documento").isEmpty()) {

                String documento = resposta.getRespostaComoObjetoJson().getString("documento");
                if (documento != null && !documento.isEmpty()) {
                    documento = UtilSBCoreStringFiltros.filtrarApenasNumeros(documento);
                } else {
                    return null;
                }

                RespostaWebServiceSimples respostaEquipe = UtilSBApiRestClient
                        .getRespostaRest("https://contatows.casanovadigital.com.br/api/v1/teste/contato/atendimnetoByCNPJ/?cnpj=" + documento, FabTipoConexaoRest.GET, false, cabecalho, null);

                if (respostaEquipe.isSucesso()) {
                    JsonObject respostaJsonEquipe = respostaEquipe.getRespostaComoObjetoJson();
                    if (respostaJsonEquipe.getBoolean("sucesso")) {
                        JsonObject retornoJson = respostaJsonEquipe.getJsonObject("retorno");

                        retornoJson.getString("emailRepresentanteComercial");
                        String emailtendimento = null;
                        ItfUsuarioChat usuarioAtt = null;
                        if (pCodigoOrigem.contains(MapAtendentesMatrixCAsanovadigital.CODIGO_ENTRADA_TELEVENDAS_CASANOVA_BH9755)) {
                            if (retornoJson.containsKey("emailRepresentanteComercial")) {
                                emailtendimento = retornoJson.getString("emailRepresentanteComercial");
                            }
                        } else {
                            if (retornoJson.containsKey("emailGestorDesucesso")) {
                                emailtendimento = retornoJson.getString("emailGestorDesucesso");
                            }
                        }
                        if (emailtendimento != null) {
                            usuarioAtt = MapAtendentesMatrixCAsanovadigital.getUserAtendimentoByEmail(pCodigoOrigem, emailtendimento);
                            if (usuarioAtt != null) {
                                registrarAtendenteLead(pCodigoOrigem, pTelefoneLead, usuarioAtt);
                                return usuarioAtt;
                            }
                        }
                    }

                }

            } else {
                JsonObject jsonREspCRM = resposta.getRespostaComoObjetoJson();
                if (!jsonREspCRM.containsKey("usuarioResponsavel")) {
                    return MapAtendentesMatrixCAsanovadigital.getUsuarioAtendimentoPadrao(pCodigoOrigem);
                }
                String usuarioREsponsavel = jsonREspCRM.getString("usuarioResponsavel");
                ItfUsuarioChat usuarioAtendimento = MapAtendentesMatrixCAsanovadigital.getUserAtendimentoByEmail(pCodigoOrigem, usuarioREsponsavel);
                if (usuarioAtendimento != null) {
                    return usuarioAtendimento;
                }
                if (usuarioAtendimento == null) {
                    JsonArray usuariosResponsaveisCRM = jsonREspCRM.getJsonArray("usuariosResponsaveis");

                    for (JsonValue json : usuariosResponsaveisCRM) {
                        String emailREsponsavel = json.toString().replace("\"", "");
                        ItfUsuarioChat usuarioAtendimentoAlternativo = MapAtendentesMatrixCAsanovadigital.getUserAtendimentoByEmail(pCodigoOrigem, emailREsponsavel);
                        if (usuarioAtendimentoAlternativo != null) {
                            return usuarioAtendimentoAlternativo;
                        }
                    }
                }

            }
            return MapAtendentesMatrixCAsanovadigital.getUsuarioAtendimentoPadrao(pCodigoOrigem);
        }
        return null;
    }

}
