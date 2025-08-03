package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas;

import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.cucumber.fluxomensagemorigemwhatsapp.EtapasFluxoMensagemOrigemWhatsapp;
import cucumber.api.java.pt.Entao;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class F_Entao_o_usuario_Contato_e_notificado_da_leitura_da_mensagem_no_WhatsApp {

    @Entao(EtapasFluxoMensagemOrigemWhatsapp.ENTAO_O_USUARIO_CONTATO_E_NOTIFICADO_DA_LEITURA_DA_MENSAGEM_NO_WHATSAPP)
    public void implementacaoEtapa() {
        List<MensagemTrOrigemWhatsapp> mensagens = UtilSBPersistencia.getListaTodos(MensagemTrOrigemWhatsapp.class);
        for (MensagemTrOrigemWhatsapp msg : mensagens) {
            System.out.println(msg.isLido());
        }
        while (true) {
            try {
                Thread.sleep(1000);

            } catch (InterruptedException ex) {
                Logger.getLogger(F_Entao_o_usuario_Contato_e_notificado_da_leitura_da_mensagem_no_WhatsApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
