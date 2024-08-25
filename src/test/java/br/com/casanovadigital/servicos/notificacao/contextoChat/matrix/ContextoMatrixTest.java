/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.contextoChat.matrix;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ErroCriandoContato;
import br.com.casanovadigital.servicos.notificacao.config.ConfigCoreCNDNotificacaoContato;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringFiltros;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author salvio
 */
public class ContextoMatrixTest {

    private String pacoteMilene = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Milene Magalh\\u00e3es\"},\"wa_id\":\"553171723989\"}],\"messages\":[{\"from\":\"553171723989\",\"id\":\"wamid.HBgMNTUzMTcxNzIzOTg5FQIAEhggOURERTIzMjlCMkM4MjcxMEVBODEwNTdCOTU1MDVEQ0IA\",\"timestamp\":\"1723833872\",\"text\":{\"body\":\"Teste\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";

    public ContextoMatrixTest() {
    }

    /**
     * Test of getUsuarioChatRelacionado method, of class ContextoMatrix.
     */
    @Test
    public void testGetUsuarioChatRelacionado() throws ErroCriandoContato {
        SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.HOMOLOGACAO);
        PacoteMemensagemRecebidoWhatsapp pacote = new PacoteMemensagemRecebidoWhatsapp(pacoteMilene);
        ContatoWhatsapp contatoMilene = pacote.getMensagens().get(0).getContatoOrigem();
        String slugMilene = UtilSBCoreStringFiltros.removeCaracteresEspeciais(contatoMilene.getNome());
        System.out.println(slugMilene);
        ItfUsuarioChat usuarioChat;
        try {
            usuarioChat = ContextoMatrix.getUsuarioLeadMatrixCriandoSeNaoExistir(contatoMilene);
            System.out.println(usuarioChat.getApelido());
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(ContextoMatrixTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
