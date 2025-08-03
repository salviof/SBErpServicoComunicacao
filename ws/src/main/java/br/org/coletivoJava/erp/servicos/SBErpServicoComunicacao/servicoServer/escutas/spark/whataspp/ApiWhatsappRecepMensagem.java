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
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.RotaSparkPadrao;
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
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappMensagem;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Path;

/**
 *
 * @author salvio
 */
@Path("/api/v1/whatsapp/recepcao/evento")
public class ApiWhatsappRecepMensagem extends RotaSparkPadrao {

    @Override
    public void validarParamentros() throws ErroParamentosInvalidos {
        System.out.println(requisicao.body());
        String telefone = requisicao.attribute("telefone");
    }

    @Override
    public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {

        //   UtilAgenciaContatos.getContatoByTelefone(telefone);
        //resp.add("retorno", json);
        PacoteMemensagemRecebidoWhatsapp pacoteMensagemWtzp = getPacoteMensagem();
        /// ATENÇÃO COM leituras incoerentes, conflitos ou deadlocks dos registros de banco de dados
            /// POIS VÁRIAS THREADS PODEM ESTAR RODANDO AO MESMO TEMPO,
        /// A GESTÃO DE CONCORRENCIA DAS TRANSAÇÕES do JPA NÃO LIDARÁ BEM COM A MANIPULÇÃO


        EntityManager em = UtilSBPersistencia.getEMPadraoNovo();
        UtilSBPersistencia.iniciarTransacao(em);

        for (MensagemWhatsapp msgWtsap : pacoteMensagemWtzp.getMensagens()) {
            MensagemTrOrigemWhatsapp logTransidoDeMensagem = new MensagemTrOrigemWhatsapp();
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
                ItfRespostaWebServiceSimples retornoFalhaProcessamento = FabApiRestIntWhatsappMensagem.MENSAGEM_ENVIAR.getAcao(msgWtsap.getEntrada().getCodigo(), msgWtsap.getContatoOrigem().getWa_id(), "Falha encontrando usuário associado ao contato " + ex.getMessage()).getResposta();
                if (!retornoFalhaProcessamento.isSucesso()) {
                    throw new ErroConexaoSistemaTerceiro("Falha retornando mensagem de erro para o usuário, o pacote foi recusado");
                }
                continue;
            } catch (ErroFalhaEncaminhando | ErroFalhaGerandoSalaAtendimento | ErroFalhaGerandoUsuarioAtendimento | AssertionError t) {
                // TODO IMPLEMENTAR NOTIFICAÇÃO DE ERRO

                UtilServicoAdministrativo.notificarAdmiministrador("ATENÇÃO! FALHA PROCESSANDO PACOTE " + t.getClass().getSimpleName() + ":" + t.getMessage() + "PAYLOAD:" + requisicao.body());
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
            try {
                ItfProcessadorEventoWhatsapp processadorEvento = new ProcessadorEventoWhatsappPadrao(evento);
                processadorEvento.processar();
                //processadorEvento.isSucesso();
            } catch (ErroFalhaEncaminhando | ErroFalhaGerandoSalaAtendimento | ErroFalhaGerandoUsuarioAtendimento | ErroConexaoServicoChat | ErroComDevolucaoMensagemUsuario ex) {
                UtilServicoAdministrativo.notificarAdmiministrador("Falha processando evento, o evento foi ignorado" + ex.getMessage());
                continue;
            }
        }
        return "OK";
    }
}
