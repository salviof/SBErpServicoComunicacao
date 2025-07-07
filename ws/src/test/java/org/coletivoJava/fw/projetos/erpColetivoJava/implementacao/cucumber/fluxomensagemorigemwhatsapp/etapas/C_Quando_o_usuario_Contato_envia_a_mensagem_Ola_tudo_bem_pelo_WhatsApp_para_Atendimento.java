package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas;

import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.ApiWhatsappRecepcaoMensagem;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.cucumber.fluxomensagemorigemwhatsapp.EtapasFluxoMensagemOrigemWhatsapp;
import cucumber.api.java.pt.Quando;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.FluxoMensagemOrigemWhatsapp;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas.testes.UtilTestesSpark;

public class C_Quando_o_usuario_Contato_envia_a_mensagem_Ola_tudo_bem_pelo_WhatsApp_para_Atendimento {

    @Quando(EtapasFluxoMensagemOrigemWhatsapp.QUANDO_O_USUARIO_CONTATO_ENVIA_A_MENSAGEM_OLA_TUDO_BEM_PELO_WHATSAPP_PARA_ATENDIMENTO)
    public void implementacaoEtapa() {
        MensagemWhatsapp mensagem = FluxoMensagemOrigemWhatsapp.getPacoteEnvioMensagem().getMensagens().get(0);
        UtilTestesSpark.CriarRequisicao(ApiWhatsappRecepcaoMensagem.class, FluxoMensagemOrigemWhatsapp.MENSAGEM_whatsapp_SIMPLES_payload);

    }
}
