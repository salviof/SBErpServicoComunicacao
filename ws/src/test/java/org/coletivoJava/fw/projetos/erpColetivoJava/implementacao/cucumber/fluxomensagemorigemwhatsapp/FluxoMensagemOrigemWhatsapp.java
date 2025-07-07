package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp;

import br.com.casanovadigital.servicos.chat.config.ConfigCoreCNDNotificacaoContato;
import br.com.casanovadigital.servicos.chat.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.contato.ErroCriandoContato;
import com.super_bits.casanovadigital.servicos.messagens.model.configModel.ConfigPercistenciaServicoComunicacao;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.Persistencia.ConfigGeral.SBPersistencia;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import cucumber.api.CucumberOptions;
import java.util.List;
import org.junit.runner.RunWith;
import testesFW.cucumber.CucumberSBTestes;
import testesFW.cucumber.TesteIntegracaoFuncionalidadeCucumber;

/**
 *
 * @author salvio
 */
@RunWith(CucumberSBTestes.class)
@CucumberOptions(features = "classpath:cucumber/mensagensOrigemWhatsapp", tags = "@FluxoMensagemOrigemWhatsapp",
        glue = "org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp",
        monochrome = true, dryRun = false)
public class FluxoMensagemOrigemWhatsapp extends TesteIntegracaoFuncionalidadeCucumber {

    public static final String MENSAGEM_whatsapp_SIMPLES_payload = " {\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhgWM0VCMDNCOUM5REZCN0I0RjY3Nzg1RgA=\",\"timestamp\":\"1691003496\",\"text\":{\"body\":\"Oi uma mensagem simples com icone \\ud83e\\udd18\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";
    private static PacoteMemensagemRecebidoWhatsapp pacoteEnvioMensagemSimples;

    public static final PacoteMemensagemRecebidoWhatsapp getPacoteEnvioMensagem() {
        try {
            if (pacoteEnvioMensagemSimples == null) {
                pacoteEnvioMensagemSimples = new PacoteMemensagemRecebidoWhatsapp(MENSAGEM_whatsapp_SIMPLES_payload);
            }
            return pacoteEnvioMensagemSimples;
        } catch (ErroCriandoContato ex) {
            return null;
        }
    }

    @Override
    protected void configAmbienteDesevolvimento() {
        SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.DESENVOLVIMENTO);
        SBPersistencia.configuraJPA(new ConfigPercistenciaServicoComunicacao());
        List<MensagemTrOrigemWhatsapp> mensagens = UtilSBPersistencia.getListaTodos(MensagemTrOrigemWhatsapp.class);
        System.out.println(mensagens);
    }

}
