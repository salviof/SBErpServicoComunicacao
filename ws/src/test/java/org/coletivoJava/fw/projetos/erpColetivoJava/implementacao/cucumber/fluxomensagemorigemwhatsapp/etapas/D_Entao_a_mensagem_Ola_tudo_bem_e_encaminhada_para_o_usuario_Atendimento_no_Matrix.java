package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroIniciandoTrilha;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.cucumber.fluxomensagemorigemwhatsapp.EtapasFluxoMensagemOrigemWhatsapp;
import cucumber.api.java.pt.Entao;
import jakarta.json.JsonArray;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.FluxoMensagemOrigemWhatsapp;
import org.junit.Assert;

public class D_Entao_a_mensagem_Ola_tudo_bem_e_encaminhada_para_o_usuario_Atendimento_no_Matrix {

    @Entao(EtapasFluxoMensagemOrigemWhatsapp.ENTAO_A_MENSAGEM_OLA_TUDO_BEM_E_ENCAMINHADA_PARA_O_USUARIO_ATENDIMENTO_NO_MATRIX)
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
            JsonArray mensagens;
            mensagens = AplicacaoWsChat.SERVICO_MATRIX.salaLerUltimasMensagens(sala.getCodigoChat());
        } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat | ErroComDevolucaoMensagemUsuario ex) {
            Logger.getLogger(D_Entao_a_mensagem_Ola_tudo_bem_e_encaminhada_para_o_usuario_Atendimento_no_Matrix.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail("Falhu lendo ultimas mensagens");
        } catch (ErroIniciandoTrilha ex) {
            Logger.getLogger(D_Entao_a_mensagem_Ola_tudo_bem_e_encaminhada_para_o_usuario_Atendimento_no_Matrix.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
