/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ErroCriandoContato;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.statusMensagem.StatusMensagemWtzap;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappMedia;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author salvio
 */
public class PacoteMemensagemRecebidoWhatsappTest {

    private final String mensagemMediaFoto = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhgWM0VCMEE5OUVFMzVBOEU5MUU1RkVDMwA=\",\"timestamp\":\"1691001224\",\"type\":\"image\",\"image\":{\"caption\":\"Apenas teste\",\"mime_type\":\"image\\/jpeg\",\"sha256\":\"Tzuq3Jktfo+8TlbHtDXbiFpeFRkvFs2yLdUijLEXl68=\",\"id\":\"250269507869031\"}}]},\"field\":\"messages\"}]}]}";
    private final String mensagemSimples = " {\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhgWM0VCMDNCOUM5REZCN0I0RjY3Nzg1RgA=\",\"timestamp\":\"1691003496\",\"text\":{\"body\":\"Oi uma mensagem simples com icone \\ud83e\\udd18\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";
    private final String msgFoto2 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhgWM0VCMEE4NDAyRUY0N0NEM0YyOENCNAA=\",\"timestamp\":\"1691865226\",\"type\":\"image\",\"image\":{\"caption\":\"teste\",\"mime_type\":\"image\\/jpeg\",\"sha256\":\"rYYSGPd9juvooJgBOv\\/+Cu8XNKyBpYvGUJUZnu2GQgg=\",\"id\":\"1043652053462941\"}}]},\"field\":\"messages\"}]}]}";
    private final String msgFoto3 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhgWM0VCMDg3N0Y0NUU3NTUyMURCOTMwMQA=\",\"timestamp\":\"1694790284\",\"type\":\"image\",\"image\":{\"mime_type\":\"image\\/jpeg\",\"sha256\":\"M7AYuA84NyBoeVQt3Vy9beZcm0c8X\\/kFJv8ZCK9m1d4=\",\"id\":\"614543020758485\"}}]},\"field\":\"messages\"}]}]}";
    private final String msgAudio = " {\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhggNTc3NkU5N0RCRENCOUU2RUZFNUM2MzQ3NjhDNkI1MDMA\",\"timestamp\":\"1694632099\",\"type\":\"audio\",\"audio\":{\"mime_type\":\"audio\\/ogg; codecs=opus\",\"sha256\":\"Mw3LoEejzcR14TbYQvNQ1WFYV5HUjVTerXtmRMvfMGI=\",\"id\":\"623270176348847\",\"voice\":true}}]},\"field\":\"messages\"}]}]}";
    private final String retornoMensagemNAOLida = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"statuses\":[{\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSNDY3N0JCQzJGMkZBMDhFNUYxAA==\",\"status\":\"failed\",\"timestamp\":\"1694459327\",\"recipient_id\":\"553184178550\",\"errors\":[{\"code\":131047,\"title\":\"Re-engagement message\",\"message\":\"Re-engagement message\",\"error_data\":{\"details\":\"Message failed to send because more than 24 hours have passed since the customer last replied to this number.\"},\"href\":\"https:\\/\\/developers.facebook.com\\/docs\\/whatsapp\\/cloud-api\\/support\\/error-codes\\/\"}]}]},\"field\":\"messages\"}]}]}";
    private final String retornoMensagemLida = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"statuses\":[{\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSNjlERjU2Q0RFOTVFRDk2OEZFAA==\",\"status\":\"read\",\"timestamp\":\"1695651348\",\"recipient_id\":\"553184178550\"}]},\"field\":\"messages\"}]}]}";
    private final String retornoMensagemEntregue = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"statuses\":[{\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSMDNGOTE0NTMxNzgxRDY4OEYyAA==\",\"status\":\"delivered\",\"timestamp\":\"1695592957\",\"recipient_id\":\"553184178550\",\"conversation\":{\"id\":\"7c8948f5db8176397e470ba2a9725c0b\",\"origin\":{\"type\":\"service\"}},\"pricing\":{\"billable\":true,\"pricing_model\":\"CBP\",\"category\":\"service\"}}]},\"field\":\"messages\"}]}]}";
    private final String reciboEnvioMensagem = " {\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"statuses\":[{\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSMDNGOTE0NTMxNzgxRDY4OEYyAA==\",\"status\":\"sent\",\"timestamp\":\"1695592957\",\"recipient_id\":\"553184178550\",\"conversation\":{\"id\":\"7c8948f5db8176397e470ba2a9725c0b\",\"expiration_timestamp\":\"1695679380\",\"origin\":{\"type\":\"service\"}},\"pricing\":{\"billable\":true,\"pricing_model\":\"CBP\",\"category\":\"service\"}}]},\"field\":\"messages\"}]}]}";
    private final String recebimentoMesagemFoto3 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhgWM0VCMEY5ODY1MzAxNzQzMUM2MzkwNAA=\",\"timestamp\":\"1695747266\",\"type\":\"image\",\"image\":{\"mime_type\":\"image\\/jpeg\",\"sha256\":\"qFBsvIu2kZKmJaZH\\/Zrx3wTVV7OJoJ7PAAeUCJLGFZQ=\",\"id\":\"1375883766668243\"}}]},\"field\":\"messages\"}]}]}";
    private final String recebmentoPDF = " {\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"PARALELA\"},\"wa_id\":\"5511942383774\"}],\"messages\":[{\"from\":\"5511942383774\",\"id\":\"wamid.HBgNNTUxMTk0MjM4Mzc3NBUCABIYFjNFQjAwNUM3NEFBMjFEMzhGQzI1NjIA\",\"timestamp\":\"1703172884\",\"type\":\"document\",\"document\":{\"filename\":\"Cota\\u00e7\\u00e3o-10.pdf\",\"mime_type\":\"application\\/pdf\",\"sha256\":\"G09MDSNpk6Rf7DMSwe6kbTZoJpXFn1OvbrsJrAnugT0=\",\"id\":\"1065743027903085\"}}]},\"field\":\"messages\"}]}]}";

    @Test
    public void testeMensagemPDF() throws ErroCriandoContato {
        PacoteMemensagemRecebidoWhatsapp pacoteSimples = new PacoteMemensagemRecebidoWhatsapp(recebmentoPDF);
        System.out.println(pacoteSimples);
        assertEquals("nenhum status encontrada", 1, pacoteSimples.getMensagens().size());
        for (MensagemWhatsapp msg : pacoteSimples.getMensagens()) {
            System.out.println(msg.getTipoMensagem().isTipoMedia());
            System.out.println(msg.getCodigoMedia());
            System.out.println(msg.getTipoMensagem());
        }
        System.out.println(pacoteSimples.getMensagens().get(0).getTelefone());
        System.out.println(pacoteSimples.getMensagens().get(0).getNome());
    }

    @Test
    public void testeMensagemFoto3() throws ErroCriandoContato {
        PacoteMemensagemRecebidoWhatsapp pacoteSimples = new PacoteMemensagemRecebidoWhatsapp(recebimentoMesagemFoto3);
        System.out.println(pacoteSimples);
        assertEquals("nenhum status encontrada", 1, pacoteSimples.getMensagens().size());
        for (MensagemWhatsapp msg : pacoteSimples.getMensagens()) {
            System.out.println(msg.getTipoMensagem().isTipoMedia());
            System.out.println(msg.getCodigoMedia());
            System.out.println(msg.getTipoMensagem());
        }
        System.out.println(pacoteSimples.getMensagens().get(0).getTelefone());
        System.out.println(pacoteSimples.getMensagens().get(0).getNome());
    }

    @Test
    public void testeMensagemEntregue() throws ErroCriandoContato {
        PacoteMemensagemRecebidoWhatsapp pacoteSimples = new PacoteMemensagemRecebidoWhatsapp(retornoMensagemEntregue);
        System.out.println(pacoteSimples);
        assertEquals("nenhum status encontrada", 1, pacoteSimples.getStatusMensagem().size());
        for (StatusMensagemWtzap statusMensagem : pacoteSimples.getStatusMensagem()) {

            System.out.println(statusMensagem.getDescricaoErro());
        }
        System.out.println(pacoteSimples.getStatusMensagem().get(0).getDescricaoErro());
    }

    public void testeMensagemAudio() throws ErroCriandoContato {
        System.out.println(msgAudio);
        PacoteMemensagemRecebidoWhatsapp pacoteSimples = new PacoteMemensagemRecebidoWhatsapp(msgAudio);
        System.out.println(pacoteSimples);
        assertEquals("nenhuma mensagem encontrada", 1, pacoteSimples.getMensagens().size());
        System.out.println(pacoteSimples.getMensagens().get(0).getMensagem());

        for (MensagemWhatsapp msg : pacoteSimples.getMensagens()) {
            System.out.println(msg.getMediaNome());
            System.out.println(msg.getNome());

        }
    }

    public void testeMensagemMedia2() throws ErroCriandoContato {

        PacoteMemensagemRecebidoWhatsapp pacoteSimples = new PacoteMemensagemRecebidoWhatsapp(msgFoto2);
        System.out.println(pacoteSimples);
        assertEquals("nenhuma mensagem encontrada", 1, pacoteSimples.getMensagens().size());
        System.out.println(pacoteSimples.getMensagens().get(0).getMensagem());
    }

    public void testeMensagemMediaFoto() throws ErroCriandoContato {

        PacoteMemensagemRecebidoWhatsapp pacoteSimples = new PacoteMemensagemRecebidoWhatsapp(mensagemMediaFoto);
        System.out.println(pacoteSimples);
        assertEquals("nenhuma mensagem encontrada", 1, pacoteSimples.getMensagens().size());
        System.out.println(pacoteSimples.getMensagens().get(0).getMensagem());
    }

    public void testeMensagemSimples() throws ErroCriandoContato {
        PacoteMemensagemRecebidoWhatsapp pacoteSimples = new PacoteMemensagemRecebidoWhatsapp(mensagemSimples);
        System.out.println(pacoteSimples);
        assertEquals("nenhuma mensagem encontrada", 1, pacoteSimples.getMensagens().size());
        System.out.println(pacoteSimples.getMensagens().get(0).getMensagem());
    }

    public void testeImagem() throws ErroCriandoContato {
        PacoteMemensagemRecebidoWhatsapp pacoteSimples = new PacoteMemensagemRecebidoWhatsapp(msgFoto3);
        System.out.println(pacoteSimples);
        assertEquals("nenhuma mensagem encontrada", 1, pacoteSimples.getMensagens().size());

        for (MensagemWhatsapp msg : pacoteSimples.getMensagens()) {
            System.out.println(msg.getMediaNome());

            ItfRespostaWebServiceSimples resp = FabApiRestIntWhatsappMedia.MEDIA_GET_URL.getAcao(msg.getCodigoMedia()).getResposta();
            System.out.println(resp.getRespostaTexto());
            System.out.println(msg.getCodigoMedia());
        }

        System.out.println(pacoteSimples.getMensagens().get(0).getMensagem());
    }

    public void testeMensagemFalha24HorasSemContato() throws ErroCriandoContato {
        PacoteMemensagemRecebidoWhatsapp pacoteSimples = new PacoteMemensagemRecebidoWhatsapp(retornoMensagemNAOLida);
        System.out.println(pacoteSimples);
        assertEquals("nenhum status encontrada", 1, pacoteSimples.getStatusMensagem().size());
        for (StatusMensagemWtzap statusMensagem : pacoteSimples.getStatusMensagem()) {
            System.out.println(statusMensagem.getDescricaoErro());
        }
        System.out.println(pacoteSimples.getStatusMensagem().get(0).getDescricaoErro());
    }

    public void testeMensagemLida() throws ErroCriandoContato {
        PacoteMemensagemRecebidoWhatsapp pacoteSimples = new PacoteMemensagemRecebidoWhatsapp(retornoMensagemLida);
        System.out.println(pacoteSimples);
        assertEquals("nenhum status encontrada", 1, pacoteSimples.getStatusMensagem().size());
        for (StatusMensagemWtzap statusMensagem : pacoteSimples.getStatusMensagem()) {
            System.out.println(statusMensagem.getDescricaoErro());
        }
        System.out.println(pacoteSimples.getStatusMensagem().get(0).getDescricaoErro());
    }

}
