package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.json.ErroProcessandoJson;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.cucumber.fluxomensagemorigemwhatsapp.EtapasFluxoMensagemOrigemWhatsapp;
import cucumber.api.java.pt.Dado;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.FluxoMensagemOrigemWhatsapp;
import org.junit.Assert;

public class A_Dado_que_o_usuario_Contato_esta_conectado_no_WhatsApp {

    @Dado(EtapasFluxoMensagemOrigemWhatsapp.DADO_QUE_O_USUARIO_CONTATO_ESTA_CONECTADO_NO_WHATSAPP)
    public void implementacaoEtapa() {
        try {
            System.out.println(SBCore.getNomeProjeto());
            PacoteMemensagemRecebidoWhatsapp pacote = new PacoteMemensagemRecebidoWhatsapp(FluxoMensagemOrigemWhatsapp.MENSAGEM_whatsapp_SIMPLES_payload);
            System.out.println("Contato");
            System.out.println("Nome" + pacote.getMensagens().get(0).getContatoOrigem().getNome());
            System.out.println("WaID" + pacote.getMensagens().get(0).getContatoOrigem().getWa_id());

        } catch (ErroProcessandoJson ex) {
            Assert.fail("Pacote inde wahtsapp incompativel");
        }

    }
}
