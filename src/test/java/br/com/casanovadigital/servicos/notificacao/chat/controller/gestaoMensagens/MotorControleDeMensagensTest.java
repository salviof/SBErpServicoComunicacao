/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ErroCriandoContato;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import br.com.casanovadigital.servicos.notificacao.contextoChat.whatsapp.ContextoWhatsapp;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.json.JSONObject;

/**
 *
 * @author salvio
 */
public class MotorControleDeMensagensTest extends TesteInputWhatsapp {

    public MotorControleDeMensagensTest() {
    }
    //HBgMNTU4NDk4NDgwMDAwFQIAEhgQM0VCMDJDREE1OUY2N0RBNAA

    private String pacoteMilene = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Milene Magalh\\u00e3es\"},\"wa_id\":\"553171723989\"}],\"messages\":[{\"from\":\"553171723989\",\"id\":\"wamid.HBgMNTUzMTcxNzIzOTg5FQIAEhggOURERTIzMjlCMkM4MjcxMEVBODEwNTdCOTU1MDVEQ0IA\",\"timestamp\":\"1723833872\",\"text\":{\"body\":\"Teste\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";
    private String pacote1 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Grupo Artcap\"},\"wa_id\":\"558498480000\"}],\"messages\":[{\"from\":\"558498480000\",\"id\":\"wamid.HBgMNTU4NDk4NDgwMDAwFQIAEhgQM0VCMDJDREE1OUY2N0RBNAA=\",\"timestamp\":\"1722081991\",\"type\":\"document\",\"document\":{\"caption\":\"ARTCAP LAYOUT - CASA NOVA digital.pdf\",\"filename\":\"file\",\"mime_type\":\"application\\/pdf\",\"sha256\":\"AhhQYyGg51uqbJMD+xLotnCW4YBszCWstrLoguyYoMg=\",\"id\":\"1133198671088453\"}}]},\"field\":\"messages\"}]}]}";
    private String pacote2 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Grupo Artcap\"},\"wa_id\":\"558498480000\"}],\"messages\":[{\"from\":\"558498480000\",\"id\":\"wamid.HBgMNTU4NDk4NDgwMDAwFQIAEhgQM0VCMDU2OEREREZFQkMxOAA=\",\"timestamp\":\"1722081966\",\"text\":{\"body\":\"*Camila*:\\nMe chamo Camila, Sou Consultora Comercial do Grupo Artcap (Artcap - bon\\u00e9s personalizados, Meu T\\u00e9rmico - conservadores t\\u00e9rmicos em geral e Neobest - brindes de alto padr\\u00e3o para empresas), confeccionamos nossos produtos para todo o Brasil!\\n\\nGostaria de apresentar um Layout com a logomarca de voc\\u00eas em alguns produtos que trabalhamos, sem qualquer compromisso, pode ser?\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";
    private String pacote3 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"statuses\":[{\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSNUIxMzJCMDRCNEEzMDUwNENFAA==\",\"status\":\"read\",\"timestamp\":\"1722346663\",\"recipient_id\":\"553184178550\"}]},\"field\":\"messages\"}]}]}";
    private String pacote4 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhggNzlCRkU4RDNDQUE4QUEyRUIzNzVCQ0NBNzhEQUI5QzAA\",\"timestamp\":\"1722275755\",\"type\":\"audio\",\"audio\":{\"mime_type\":\"audio\\/ogg; codecs=opus\",\"sha256\":\"OXJppPvlSGY5+f8R982SVN5G88JAS5IF1v+fUwjgqzY=\",\"id\":\"1236909894346960\",\"voice\":true}}]},\"field\":\"messages\"}]}]}";
    private String pacote5 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhggRjkxMTBEMUM0M0Y5QTJDNzkzOTQ1NTlGN0Y0ODdGNjUA\",\"timestamp\":\"1722276055\",\"text\":{\"body\":\"\\ud83c\\udfc1\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";
    private String pacote6 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSMjIyQjExNkE0NUU2OTJGQjFFAA==\",\"timestamp\":\"1722276051\",\"text\":{\"body\":\"ppppp6\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";
    private String pacote7 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhggMDhDMzVEQzEyMEUwOTYyRTA2NUFDNjMyRDNGMDFENjkA\",\"timestamp\":\"1722276046\",\"text\":{\"body\":\"000005\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";
    private String pacote8 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhgWM0VCMDA2ODJCRkZEQ0YxMTBFQTdCQgA=\",\"timestamp\":\"1722292949\",\"text\":{\"body\":\"Teste um texto longo bem longo,Teste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longoTeste um texto longo bem longo\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";
    private String pacote9 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhgWM0VCMDVFRUU5Njk1MzQ0RkVBQjY4MwA=\",\"timestamp\":\"1722292935\",\"text\":{\"body\":\"000999999999999999\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";
    private String pacote10 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhggRjkxMTBEMUM0M0Y5QTJDNzkzOTQ1NTlGN0Y0ODdGNjUA\",\"timestamp\":\"1722276055\",\"text\":{\"body\":\"\\ud83c\\udfc1\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";
    private String pacote0 = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"statuses\":[{\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSOTlBMjVCQTkwQTAzMkJFOTAxAA==\",\"status\":\"failed\",\"timestamp\":\"1723090973\",\"recipient_id\":\"553184178550\",\"errors\":[{\"code\":131047,\"title\":\"Re-engagement message\",\"message\":\"Re-engagement message\",\"error_data\":{\"details\":\"Message failed to send because more than 24 hours have passed since the customer last replied to this number.\"},\"href\":\"https:\\/\\/developers.facebook.com\\/docs\\/whatsapp\\/cloud-api\\/support\\/error-codes\\/\"}]}]},\"field\":\"messages\"}]}]}";
    private String lida = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"statuses\":[{\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSMjIyQjExNkE0NUU2OTJGQjFFAA==\",\"status\":\"read\",\"timestamp\":\"1723162703\",\"recipient_id\":\"553184178550\"}]},\"field\":\"messages\"}]}]}";
    private String pacoteNovoContato = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Igor fk 7 t \"},\"wa_id\":\"553175621137\"}],\"messages\":[{\"from\":\"553175621137\",\"id\":\"wamid.HBgMNTUzMTc1NjbxMTMyFQIAEhgWM0VCMDU0NTRFREFBNUM0MTM0QThBQQA=\",\"timestamp\":\"1724099050\",\"text\":{\"body\":\"Meu nome \\u00e9 Igor, estou a procura de uma assessoria para marketing digital e gest\\u00e3o de redes sociais da minha empresa. Gostaria de conhecer os planos e valores oferecidos pela sua ag\\u00eancia.\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";
    private static String linhaTeste = "{}";
    private static boolean executarTesteLinhaDinamica = false;

    @Test
    public void testeNotificacaoLeituraMensagemEnviadaPorCliente() {

        //
        executarSequencia(pacoteNovoContato);
        try {
            Thread.sleep(650000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MotorControleDeMensagensTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testeNotificacaoLeituraMensagemEnviadaNoChat() {

        EntradaNumeroWhatsapp entrada = MotorControleDeMensagens.getEntradaByCodigoEntrada("114354588403482");
        PacoteMemensagemRecebidoWhatsapp pctmensam;
        try {
            pctmensam = new PacoteMemensagemRecebidoWhatsapp(pacote6);

            ContatoWhatsapp contato = pctmensam.getMensagens().get(0).getContatoOrigem();

            ContextoWhatsapp ctx = MotorControleDeMensagens.getContextoWhatsapp(entrada, contato);
            String jsonEnvioMensagem = "{\n"
                    + "  \"msgtype\": \"m.text\",\n"
                    + "  \"body\": \"Uuuuuuuuupppppppppppppppppppppppp 2 !🤙\"\n"
                    + "}";
            JSONObject eventoMatrixJzon = new JSONObject();
            eventoMatrixJzon.put("msgtype", "m.text");
            eventoMatrixJzon.put("body", "asdasdasdasda sdasd");

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(MotorControleDeMensagensTest.class.getName()).log(Level.SEVERE, null, ex);
            }

            RoomEvent eventoTEste = new RoomEvent(eventoMatrixJzon,
                    "m.room.message",
                    "$RRBcTWhx1yNDeLrVM2Fr-pnLKqSxT1-ITMA4_vd4qbA",
                    "@salvio_furbino930:casanovadigital.com.br",
                    "!QLOZIEkdxvNrMRxpfy:casanovadigital.com.br",
                    eventoMatrixJzon);

            MotorControleDeMensagens.executarEventoSalaMatrix(eventoTEste);

        } catch (ErroCriandoContato ex) {
            Logger.getLogger(MotorControleDeMensagensTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        // MotorControleDeMensagens.registrarEncaminhamentoWhatsapp(pacote1, pacote1);
        //MotorControleDeMensagens.getControleRecibo().registrarEventoEncaminhamentoMatrixParaWhatsapp(ctx.getCodigoContexto(), "$RRBcTWhx1yNDeLrVM2Fr-pnLKqSxT1-ITMA4_vd4qbA", "wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSMjIyQjExNkE0NUU2OTJGQjFFAA==");
        //  MotorControleDeMensagens.registrarReciboEntregaChat("$RRBcTWhx1yNDeLrVM2Fr-pnLKqSxT1-ITMA4_vd4qbA", ctx.getCodigoContexto());
        //    MotorControleDeMensagens.registrarReciboEntregaWhatsapp("$RRBcTWhx1yNDeLrVM2Fr-pnLKqSxT1-ITMA4_vd4qbA", "wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSMjIyQjExNkE0NUU2OTJGQjFFAA==");
        new Thread() {
            @Override
            public void run() {
                executarSequencia(pacote6, lida, lida, lida);
            }
        }.start();

        //
        while (true) {
            try {
                Thread.sleep(120000);
                if (executarTesteLinhaDinamica) {
                    if (linhaTeste != null && !linhaTeste.isEmpty()) {
                        simularEnvioEventoWhatsapp(linhaTeste);
                    }
                    System.out.println(linhaTeste);

                }

            } catch (InterruptedException ex) {

            }

        }
    }
}
