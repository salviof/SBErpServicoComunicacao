package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas;

import br.com.casanovadigital.servicos.chat.AplicacaoWsChat;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.cucumber.fluxomensagemorigemwhatsapp.EtapasFluxoMensagemOrigemWhatsapp;
import cucumber.api.java.pt.Entao;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.lang.UnsupportedOperationException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.FluxoMensagemOrigemWhatsapp;
import org.junit.Assert;

public class D_Entao_a_mensagem_Ola_tudo_bem_e_encaminhada_para_o_usuario_Atendimento_no_Matrix {

    @Entao(EtapasFluxoMensagemOrigemWhatsapp.ENTAO_A_MENSAGEM_OLA_TUDO_BEM_E_ENCAMINHADA_PARA_O_USUARIO_ATENDIMENTO_NO_MATRIX)
    public void implementacaoEtapa() {
        MensagemWhatsapp mensagem = FluxoMensagemOrigemWhatsapp.getPacoteEnvioMensagem().getMensagens().get(0);
        ItfUsuarioChat usuarioAtendimento = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(
                FluxoMensagemOrigemWhatsapp.getPacoteEnvioMensagem().getMensagens().get(0).getEntrada(),
                FluxoMensagemOrigemWhatsapp.getPacoteEnvioMensagem().getMensagens().get(0).getContatoOrigem());
        ItfChatSalaBean sala;
        try {
            sala = AplicacaoWsChat.getCentralLogicaProcesasmento().getSalaPadrao(mensagem.getEntrada(), mensagem.getContatoOrigem());
            JsonArray mensagens = AplicacaoWsChat.SERVICO_MATRIX.salaLerUltimasMensagens(sala.getCodigoChat());
            Optional<JsonObject> pesquisaMensagem = mensagens.stream().map(valor -> valor.asJsonObject()).filter(valorJson -> UtilSBCoreJson.getValorApartirDoCaminho("content.body", valorJson).toString().contains("Olá")).findFirst();
            if (!pesquisaMensagem.isPresent()) {
                Assert.fail("Mensagem olá não encontrada");
            }

        } catch (ErroFalhaGerandoSalaAtendimento | ErroConexaoServicoChat ex) {
            Assert.fail("Falha obtendo mensagens das salasa");
        }

    }
}
