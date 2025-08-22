package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.whataspp;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.whatsapp.evento.ProcessadorEventoWhatsappPadrao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.whatsapp.mensagem.ProcessadorWtzpMsg;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfProcessadorEventoWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoClient.UtilServicoAdministrativo;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import javax.persistence.EntityManager;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfProcessadorMensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.RotaPadraoWtzp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.integracoes.matrixChat.FabApiRestIntMatrixChatSalas;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappMensagem;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.json.ErroProcessandoJson;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import javax.ws.rs.Path;
import spark.Request;

/**
 *
 * @author salvio
 */
@Path("/api/v1/whatsapp/recepcao/evento")
public class ApiWhatsappRecepMensagem extends RotaPadraoWtzp {

    @Override
    public void validarParamentros(Request requisicao) throws ErroParamentosInvalidos {
        System.out.println(requisicao.body());
        //     String telefone = requisicao.attribute("telefone");
    }

    @Override
    public String executarRegraDeNegocio(String pCorpo) throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
        try {
            return processar(new PacoteMemensagemRecebidoWhatsapp(pCorpo));
        } catch (ErroProcessandoJson ex) {
            throw new ErroRegraDeNegocio("Json enviado inválido");
        }
    }

    public String processar(PacoteMemensagemRecebidoWhatsapp pPacote) throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro, ErroProcessandoJson {
//   UtilAgenciaContatos.getContatoByTelefone(telefone);
        //resp.add("retorno", json);
        PacoteMemensagemRecebidoWhatsapp pacoteMensagemWtzp = pPacote;
        /// ATENÇÃO COM leituras incoerentes, conflitos ou deadlocks dos registros de banco de dados
            /// POIS VÁRIAS THREADS PODEM ESTAR RODANDO AO MESMO TEMPO,
        /// A GESTÃO DE CONCORRENCIA DAS TRANSAÇÕES do JPA NÃO LIDARÁ BEM COM A MANIPULÇÃO




        for (MensagemWhatsapp msgWtsap : pacoteMensagemWtzp.getMensagens()) {
            EntityManager em = UtilSBPersistencia.getEMPadraoNovo();
            UtilSBPersistencia.iniciarTransacao(em);

            MensagemTrOrigemWhatsapp logTransidoDeMensagem = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getMensagemEnviadaPorWhatsappByRegistrWhatsapp(msgWtsap.getId());
            if (logTransidoDeMensagem == null) {
                logTransidoDeMensagem = new MensagemTrOrigemWhatsapp();
            }
            logTransidoDeMensagem.setCorpoJsonRecebido(UtilSBCoreJson.getTextoByJsonObjeect(pPacote.getDadosJson()));
            logTransidoDeMensagem.setRegistrado(true);
            logTransidoDeMensagem.setEncaminhado(false);
            logTransidoDeMensagem.setCodigoRegistroMensagemWhatsapp(msgWtsap.getId());
            try {
                ItfProcessadorMensagemWhatsapp processador = new ProcessadorWtzpMsg(msgWtsap, logTransidoDeMensagem);
                try {
                    processador.processar();
                } catch (ErroConexaoServicoChat ex) {
                    throw new ErroComDevolucaoMensagemUsuario("Erro de conexão com serviço chat" + ex.getMessage(), "Erro conectando com serviço de entrega, entre em contato com o administrador");
                }
            } catch (ErroComDevolucaoMensagemUsuario ex) {
                ItfRespostaWebServiceSimples retornoFalhaProcessamento = FabApiRestIntWhatsappMensagem.MENSAGEM_ENVIAR.
                        getAcao(msgWtsap.getEntrada().getCodigo(), msgWtsap.getContatoOrigem().getWa_id(), "Falha encontrando usuário associado ao contato " + ex.getMessage()).getResposta();
                if (!retornoFalhaProcessamento.isSucesso()) {
                    throw new ErroConexaoSistemaTerceiro("Falha retornando mensagem de erro para o usuário, o pacote foi recusado");
                }
                continue;
            } catch (ErroFalhaEncaminhando | ErroFalhaGerandoSalaAtendimento | ErroFalhaGerandoUsuarioAtendimento | AssertionError t) {
                // TODO IMPLEMENTAR NOTIFICAÇÃO DE ERRO

                UtilServicoAdministrativo.notificarAdmiministrador("ATENÇÃO! FALHA PROCESSANDO PACOTE " + t.getClass().getSimpleName() + ":" + t.getMessage() + "PAYLOAD:" + pPacote.getDadosJson());
                throw new ErroConexaoSistemaTerceiro("falha processando mensagem vinda do whatsapp " + t.getMessage());
            } finally {
                logTransidoDeMensagem = UtilSBPersistencia.mergeRegistro(logTransidoDeMensagem);
                if (logTransidoDeMensagem == null) {
                    throw new ErroConexaoSistemaTerceiro("Falha persistindo mensagem no repositório");
                }
            }
            UtilSBPersistencia.finzalizaTransacaoEFechaEM(em);
        }

        for (EventoMensagemWtzap evento : pacoteMensagemWtzp.getStatusMensagem()) {
            ItfProcessadorEventoWhatsapp processadorEvento = new ProcessadorEventoWhatsappPadrao(evento);
            try {

                processadorEvento.processar();
                //processadorEvento.isSucesso();
            } catch (ErroComDevolucaoMensagemUsuario pErro) {
                ItfRespostaWebServiceSimples resp = FabApiRestIntMatrixChatSalas.SALA_ENVIAR_MENSAGEM_TEXTO_SIMPLES
                        .getAcao(processadorEvento.getMensagemRelacionada().getMensagem().getSalaCodigoMatrix(),
                                processadorEvento.getMensagemRelacionada().getId().toString() + "fail", "O Sistema falhou ao entregar a mensagem com o erro: "
                                + evento.getDescricaoErro()).getResposta();
            } catch (ErroFalhaEncaminhando | ErroFalhaGerandoSalaAtendimento | ErroFalhaGerandoUsuarioAtendimento | ErroConexaoServicoChat ex) {
                UtilServicoAdministrativo.notificarAdmiministrador("Falha processando evento, o evento foi ignorado" + ex.getMessage());
                continue;
            }
        }
        return "OK";
    }
}
