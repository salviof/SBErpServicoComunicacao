/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.servicoServer.escutas.spark.whataspp;

import br.com.casanovadigital.servicos.chat.interpretadormsg.FabProcessadorMensagensEstatus;
import static br.com.casanovadigital.servicos.chat.interpretadormsg.FabTipoRetornoEncaminhamentoWhatsapp.ENCAMINHAMENTO;
import static br.com.casanovadigital.servicos.chat.interpretadormsg.FabTipoRetornoEncaminhamentoWhatsapp.MENU_OPCOES;
import static br.com.casanovadigital.servicos.chat.interpretadormsg.FabTipoRetornoEncaminhamentoWhatsapp.RESPOSTA_WEBSERVICE;
import static br.com.casanovadigital.servicos.chat.interpretadormsg.FabTipoRetornoEncaminhamentoWhatsapp.RETORNO_LINK;
import br.com.casanovadigital.servicos.chat.interpretadormsg.interfaces.ItfProcessadorEventoWhatsapp;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.FabSistemasErp;

import br.com.casanovadigital.servicos.chat.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.chat.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import br.com.casanovadigital.servicos.chat.servicoClient.UtilServicoAdministrativo;
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
import br.com.casanovadigital.servicos.chat.interpretadormsg.interfaces.ItfProcessadorMensagemWhatsapp;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.contato.ErroCriandoContato;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salvio
 */
public class ApiWhatsappRecepMensagem extends RotaSparkPadrao {

    public static Route recepcaoNotificacao() {
        return new ApiWhatsappRecepMensagem();
    }

    @Override
    public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
        try {
            //   UtilAgenciaContatos.getContatoByTelefone(telefone);
            JsonObjectBuilder resp = Json.createObjectBuilder();
            JsonObject json = UtilSBCoreJson.getJsonObjectByTexto(requisicao.body());
            //resp.add("retorno", json);

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

    @Override
    public void validarParamentros() throws ErroParamentosInvalidos {
        System.out.println(requisicao.body());
        String telefone = requisicao.attribute("telefone");
    }

}
