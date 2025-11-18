package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config.ConfigCoreServicoComunicacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import com.super_bits.casanovadigital.servicos.messagens.model.configModel.ConfigPercistenciaServicoComunicacao;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.Persistencia.ConfigGeral.SBPersistencia;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.json.ErroProcessandoJson;
import cucumber.api.CucumberOptions;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.runner.RunWith;
import testesFW.cucumber.CucumberSBTestes;
import testesFW.cucumber.TesteIntegracaoFuncionalidadeCucumber;
import testesFW.devOps.DevOpsCucumberPersistenciaMysql;

/**
 *
 *
 *
 * @author salvio
 */
@RunWith(CucumberSBTestes.class)
@CucumberOptions(features = "classpath:cucumber/mensagensOrigemWhatsapp", tags = "@FluxoMensagemOrigemWhatsapp",
        glue = "org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp",
        monochrome = true, dryRun = false)
public class FluxoMensagemOrigemWhatsapp extends TesteIntegracaoFuncionalidadeCucumber {

    public static final String MENSAGEM_whatsapp_SIMPLES_payload = " {\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Eugênia\"},\"wa_id\":\"553184178551\"}],\"messages\":[{\"from\":\"553184178551\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhgWM0VCDNCOUM5REZN0I0RjY3Nzg1RgA=\",\"timestamp\":\"1691003496\",\"text\":{\"body\":\"Oi uma mensagem simples com icone \\ud83e\\udd18\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";
    private static PacoteMemensagemRecebidoWhatsapp pacoteEnvioMensagemSimples;

    public static final PacoteMemensagemRecebidoWhatsapp getPacoteEnvioMensagem() {
        try {
            if (pacoteEnvioMensagemSimples == null) {
                pacoteEnvioMensagemSimples = new PacoteMemensagemRecebidoWhatsapp(MENSAGEM_whatsapp_SIMPLES_payload);
            }
            return pacoteEnvioMensagemSimples;
        } catch (ErroProcessandoJson ex) {
            fail(ex.getMessage());
        }
        return null;
    }

    @Override
    protected void configAmbienteDesevolvimento() {
        SBCore.configurar(new ConfigCoreServicoComunicacao(), SBCore.ESTADO_APP.DESENVOLVIMENTO);
        SBPersistencia.configuraJPA(new ConfigPercistenciaServicoComunicacao());
        DevOpsCucumberPersistenciaMysql.commpilarResultadoRequisito(FluxoMensagemOrigemWhatsapp.class);
        List<MensagemTrOrigemWhatsapp> mensagens = UtilSBPersistencia.getListaTodos(MensagemTrOrigemWhatsapp.class);
        AplicacaoWsChat.iniciarAplicacao();

        String nomeSala = "Casanova digital";
        // System.out.println(salaRegistrada.getCodigoChat());
        //  System.out.println(salaRegistrada.getNome());
        //  System.out.println(salaRegistrada.getApelido());

        ComoChatSalaBean salaCasanovaTEstes;
        try {

            salaCasanovaTEstes = AplicacaoWsChat.SERVICO_MATRIX.getSalaByNome(nomeSala);
            AplicacaoWsChat.SERVICO_MATRIX.salaAbrirSessao(salaCasanovaTEstes);
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(FluxoMensagemOrigemWhatsapp.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(mensagens);
    }

}
