/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ErroCriandoContato;
import br.com.casanovadigital.servicos.notificacao.config.ConfigCoreCNDNotificacaoContato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author salvio
 */
public class PacoteMensagemAudio {

    private final String msgAudio = " {\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhggMzVDNzlCNkE1ODBCMUREMDdEN0YyMDE1RjVGOTBBMzUA\",\"timestamp\":\"1691876766\",\"type\":\"audio\",\"audio\":{\"mime_type\":\"audio\\/ogg; codecs=opus\",\"sha256\":\"x70ZrDTcLIJ1egle+1rojTrlR2DrPayklAd7YM7H9KA=\",\"id\":\"995829408227048\",\"voice\":true}}]},\"field\":\"messages\"}]}]}";

    @Test
    public void testeMensagemMedia3() throws ErroCriandoContato {
        SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.HOMOLOGACAO);
        PacoteMemensagemRecebidoWhatsapp pacoteSimples = new PacoteMemensagemRecebidoWhatsapp(msgAudio);
        System.out.println(pacoteSimples);

        assertEquals("nenhuma mensagem encontrada", 1, pacoteSimples.getMensagens().size());
        System.out.println(pacoteSimples.getMensagens().get(0).getCodigoMedia());

        System.out.println(pacoteSimples.getMensagens().get(0).getMensagem());
    }
}
