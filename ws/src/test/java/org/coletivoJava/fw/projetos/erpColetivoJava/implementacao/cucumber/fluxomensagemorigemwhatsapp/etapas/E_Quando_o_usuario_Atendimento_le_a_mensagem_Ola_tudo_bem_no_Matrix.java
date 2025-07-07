package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas;

import br.com.casanovadigital.servicos.chat.AplicacaoWsChat;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.integracoes.matrixChat.FabApiRestIntMatrixChatSalas;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.cucumber.fluxomensagemorigemwhatsapp.EtapasFluxoMensagemOrigemWhatsapp;
import cucumber.api.java.pt.Quando;
import java.lang.UnsupportedOperationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.FluxoMensagemOrigemWhatsapp;
import org.coletivojava.fw.api.objetoNativo.mensagem.Mensagem;

public class E_Quando_o_usuario_Atendimento_le_a_mensagem_Ola_tudo_bem_no_Matrix {

    @Quando(EtapasFluxoMensagemOrigemWhatsapp.QUANDO_O_USUARIO_ATENDIMENTO_LE_A_MENSAGEM_OLA_TUDO_BEM_NO_MATRIX)
    public void implementacaoEtapa() {
        MensagemWhatsapp mensagem = FluxoMensagemOrigemWhatsapp.getPacoteEnvioMensagem().getMensagens().get(0);
        ItfUsuarioChat usuarioAtendimento = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(
                mensagem.getEntrada(),
                mensagem.getContatoOrigem());
        try {
            ItfChatSalaBean salaPadrao = AplicacaoWsChat.getCentralLogicaProcesasmento().getSalaPadrao(mensagem.getEntrada(),
                    mensagem.getContatoOrigem());
            AplicacaoWsChat.SERVICO_MATRIX.salaLerUltimoEvento(salaPadrao.getCodigoChat(), usuarioAtendimento);
        } catch (ErroFalhaGerandoSalaAtendimento ex) {
            Logger.getLogger(E_Quando_o_usuario_Atendimento_le_a_mensagem_Ola_tudo_bem_no_Matrix.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(E_Quando_o_usuario_Atendimento_le_a_mensagem_Ola_tudo_bem_no_Matrix.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
