/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.servicoServer.whatsapp;

import br.com.casanovadigital.servicos.notificacao.interpretadormsg.FabProcessadorMensagensEstatus;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.interfaces.ItfProcessadorEventoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.FabSistemasErp;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.whatsapp.ApiWhatsappRecepcaoMensagem;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import br.com.casanovadigital.servicos.notificacao.servicoClient.UtilServicoAdministrativo;
import br.com.casanovadigital.servicos.notificacao.servicoClient.UtilServicoEncaminhar;
import br.org.coletivoJava.fw.api.erp.erpintegracao.contextos.ERPIntegracaoSistemasApi;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.SistemaERPConfiavel;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.parametros.ParametroListaRestful;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import br.org.coletivoJava.integracoes.restInterprestfull.api.FabIntApiRestIntegracaoERPRestfull;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulos.SBAcessosModel.fabricas.FabSegurancaGruposPadrao;
import com.super_bits.modulos.SBAcessosModel.model.UsuarioSB;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
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
import javax.persistence.EntityManager;
import spark.Route;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.interfaces.ItfProcessadorMensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.whatsapp.contato.ErroCriandoContato;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                parametroEquipe.setId(1l);
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

                    PacoteMemensagemRecebidoWhatsapp pacoteMensagemWtzp = new PacoteMemensagemRecebidoWhatsapp(requisicao.body());

                    EntityManager em = UtilSBPersistencia.getEMDoContexto();

                    /// ATENÇÃO COM leituras incoerentes, conflitos ou deadlocks POIS VÁRIAS THREADS PODEM ESTAR RODANDO AO MESMO TEMPO,
                    /// COM GESTÃO DE CONCORRENCIA DAS TRANSAÇÕES


                    UtilSBPersistencia.iniciarTransacao(em);

                    List<ItfProcessadorMensagemWhatsapp> processadoresDeMensagens = new ArrayList<>();
                    List<ItfProcessadorEventoWhatsapp> processadoresDeEventosMensagem = new ArrayList<>();

                    for (MensagemWhatsapp msg : pacoteMensagemWtzp.getMensagens()) {
                        processadoresDeMensagens.add(FabProcessadorMensagensEstatus.getProcessadorMensageRecebidaPeloWhatsapp(msg));
                    }
                    for (EventoMensagemWtzap evento : pacoteMensagemWtzp.getStatusMensagem()) {
                        processadoresDeEventosMensagem.add(FabProcessadorMensagensEstatus.getProcessadorEventoWhatsapp(evento));
                    }

                    for (ItfProcessadorMensagemWhatsapp processador : processadoresDeMensagens) {

                        MensagemTrOrigemWhatsapp mensagem = new MensagemTrOrigemWhatsapp();
                        mensagem.setRegistrado(true);
                        mensagem.setEncaminhado(false);
                        mensagem.setCodigoRegistroMensagemWhatsapp(processador.getMensagemWhatsapp().getId());

                        try {
                            processador.isSucesso();
                        } catch (ErroComDevolucaoMensagemUsuario ex) {
                            // TENTA VOLTAR UMA MENSAGEM PARA O USUÁRIO COM O ERROO.
                            // CaSO fALHE DISPARA UM ERRO REGRA DE NEGOCIO.
                        }

                        switch (processador.getTipoEncaminhamento()) {

                            case MENU_OPCOES:

                                break;
                            case RESPOSTA_WEBSERVICE:

                                break;
                            case RETORNO_LINK:

                                break;
                            case ENCAMINHAMENTO:
                                mensagem.setCodigoEncaminhamentoMatrix(processador.getReciboEncaminhamentoMatrix());

                                break;
                            default:
                                throw new AssertionError();
                        }
                        //
                        if (UtilSBPersistencia.mergeRegistro(mensagem) == null) {
                            throw new ErroRegraDeNegocio("Falha resgistrando mensagem recebida pelo Whataspp;");
                        }

                    }
                    for (ItfProcessadorEventoWhatsapp processadorEvento : processadoresDeEventosMensagem) {
                        try {
                            processadorEvento.isSucesso();
                        } catch (ErroComDevolucaoMensagemUsuario ex) {
                            // TENTA VOLTAR UMA MENSAGEM PARA O USUÁRIO COM O ERROO.
                            // CaSO fALHE DISPARA UM ERRO REGRA DE NEGOCIO.
                        }
                    }

                    UtilSBPersistencia.fecharEM(em);
                    /// salvar no banco;
                    ///

                    return "OK";
                } catch (ErroFalhaEncaminhando | ErroFalhaGerandoSalaAtendimento | ErroFalhaGerandoUsuarioAtendimento | ErroCriandoContato | ErroRegraDeNegocio | AssertionError t) {
                    // TODO IMPLEMENTAR NOTIFICAÇÃO DE ERRO
                    UtilServicoAdministrativo.notificarAdmiministrador("ATENÇÃO! FALHA PROCESSANDO PACOTE " + t.getClass().getSimpleName() + ":" + t.getMessage() + "PAYLOAD:" + requisicao.body());
                    throw new ErroRegraDeNegocio("falha processando mensagem vinda do whatsapp ");
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
