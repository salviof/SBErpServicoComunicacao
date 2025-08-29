package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.whataspp.ApiWhatsappRecepMensagem;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.cucumber.fluxomensagemorigemwhatsapp.EtapasFluxoMensagemOrigemWhatsapp;
import cucumber.api.java.pt.Quando;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.FluxoMensagemOrigemWhatsapp;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas.testes.UtilTestesSpark;
import org.junit.Assert;

public class E_Quando_o_usuario_Atendimento_le_a_mensagem_Ola_tudo_bem_no_Matrix {

    @Quando(EtapasFluxoMensagemOrigemWhatsapp.QUANDO_O_USUARIO_ATENDIMENTO_LE_A_MENSAGEM_OLA_TUDO_BEM_NO_MATRIX)
    public void implementacaoEtapa() {
        MensagemWhatsapp mensagem = FluxoMensagemOrigemWhatsapp.getPacoteEnvioMensagem().getMensagens().get(0);
        Contato contato;
        try {
            contato = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato(mensagem.getContatoOrigem());
            ItfUsuarioChat usuarioAtendimento = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(
                    FluxoMensagemOrigemWhatsapp.getPacoteEnvioMensagem().getMensagens().get(0).getEntrada(),
                    contato);
            ItfChatSalaBean sala;
            ItfTrilhaNavegacao trilha = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getTrilhaByMensagemWhatasapp(mensagem.getEntrada(), contato, mensagem);
            sala = trilha.getRotaAtual().getComoRotaEncaminhamentoMatrix().getSala();
            String mensagemJson = FluxoMensagemOrigemWhatsapp.MENSAGEM_whatsapp_SIMPLES_payload;
            boolean enviarPacote = false;
            while (true) {
                AplicacaoWsChat.SERVICO_MATRIX.salaLerUltimoEvento(sala.getCodigoChat(), usuarioAtendimento);

                if (enviarPacote) {
                    UtilTestesSpark.criarRequisicao(ApiWhatsappRecepMensagem.class, mensagemJson);
                }
            }

        } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat ex) {
            Assert.fail("Falha lendo ultimo evento");
        } catch (ErroComDevolucaoMensagemUsuario ex) {
            Assert.fail("Falha lendo ultimo evento");
        }

    }
}
