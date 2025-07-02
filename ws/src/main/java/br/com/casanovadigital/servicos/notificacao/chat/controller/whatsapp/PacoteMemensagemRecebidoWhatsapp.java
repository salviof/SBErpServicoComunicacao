/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp;

import br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens.MotorControleDeMensagens;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ErroCriandoContato;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.FabTipoMensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.statusMensagem.FabTipoStatusMensagemWhtzap;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.statusMensagem.StatusMensagemWtzap;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringFiltros;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringSlugs;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.oauth.FabStatusToken;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author salvio
 */
public class PacoteMemensagemRecebidoWhatsapp {

    private JsonObject dados;

    private List<MensagemWhatsapp> mensagens = new ArrayList<>();
    private Map<String, String> mapaContatos = new HashMap<>();
    private List<StatusMensagemWtzap> statusMensagem = new ArrayList<>();

    public PacoteMemensagemRecebidoWhatsapp(String pDados) throws ErroCriandoContato {
        System.out.println(pDados);
        this.dados = UtilSBCoreJson.getJsonObjectByTexto(pDados);
        processarJson();

    }

    public List<MensagemWhatsapp> getMensagens() {
        return mensagens;
    }

    public List<StatusMensagemWtzap> getStatusMensagem() {
        return statusMensagem;
    }

    private void processarJson() throws ErroCriandoContato {
        if (dados.getString("object").equals("whatsapp_business_account")) {
            JsonArray entradas = dados.getJsonArray("entry");
            for (JsonValue jv : entradas) {
                String entradaCodigo = jv.asJsonObject().getString("id");

                EntradaNumeroWhatsapp entrada = MotorControleDeMensagens.getEntradaByCodigoEntrada(entradaCodigo);

                switch (jv.getValueType()) {

                    case OBJECT:
                        JsonArray jaAlteracoes = jv.asJsonObject().getJsonArray("changes");
                        for (JsonValue alteracao : jaAlteracoes) {

                            JsonObject valorAlteracao = alteracao.asJsonObject().getJsonObject("value");

                            if (valorAlteracao.containsKey("contacts")) {
                                JsonArray jaContatos = valorAlteracao.asJsonObject().getJsonArray("contacts");
                                for (JsonValue ct : jaContatos) {
                                    switch (ct.getValueType()) {
                                        case OBJECT:
                                            if (ct.asJsonObject().containsKey("wa_id")) {
                                                String telefone = ct.asJsonObject().getString("wa_id");
                                                if (ct.asJsonObject().containsKey("profile")) {
                                                    JsonObject profile = ct.asJsonObject().getJsonObject("profile");
                                                    String nome = profile.getString("name");
                                                    mapaContatos.put(telefone, nome);
                                                }
                                            }

                                            break;

                                    }

                                }
                            }
                            if (valorAlteracao.containsKey("statuses")) {
                                JsonArray jaStatus = valorAlteracao.asJsonObject().getJsonArray("statuses");
                                JsonObject metadataJson = valorAlteracao.getJsonObject("metadata");
                                for (JsonValue jvStatus : jaStatus) {
                                    StatusMensagemWtzap status = FabTipoStatusMensagemWhtzap.gerarStatusMensgem(entrada, metadataJson, jvStatus.asJsonObject());
                                    if (status == null) {
                                        System.out.println("Estatus não reconhecido para json:");
                                        UtilSBCoreJson.getTextoByJsonObjeect(jvStatus.asJsonObject());
                                    } else {
                                        statusMensagem.add(status);
                                    }
                                }
                            }

                            if (valorAlteracao.containsKey("messages")) {
                                JsonArray jaMessages = valorAlteracao.asJsonObject().getJsonArray("messages");
                                for (JsonValue jvMensagem : jaMessages) {
                                    JsonObject joMensagem = jvMensagem.asJsonObject();
                                    String typeMensagem = joMensagem.getString("type");
                                    FabTipoMensagemWhatsapp tipoMensagem = FabTipoMensagemWhatsapp.getTipoMensagemByType(typeMensagem);
                                    MensagemWhatsapp mensagem = new MensagemWhatsapp(tipoMensagem);
                                    mensagem.setEntrada(entrada);
                                    String id = joMensagem.getString("id");
                                    mensagem.setId(id);
                                    mensagem.setCodigoContaConectada(entradaCodigo);
                                    String from = joMensagem.getString("from");
                                    String nm = mapaContatos.get(from);
                                    mensagem.setNome(nm);
                                    mensagem.setTelefone(from);
                                    mensagem.setContatoOrigem(new ContatoWhatsapp(from, mapaContatos.get(from)));
                                    switch (mensagem.getTipoMensagem()) {

                                        case TEXTO_SIMPLES:
                                            JsonObject joTexto = joMensagem.getJsonObject("text");
                                            mensagem.setMensagem(joTexto.getString("body"));
                                            mensagens.add(mensagem);
                                            break;
                                        case REACAO:
                                            break;
                                        case DOCUMENTO:
                                            JsonObject documento = joMensagem.getJsonObject("document");

                                            String tipodocumento = documento.getString("mime_type");
                                            mensagem.setCodigoMedia(documento.getString("id"));

                                            String caption = documento.getString("caption");
                                            if (caption.contains(".")) {
                                                mensagem.setMediaNome(UtilSBCoreStringSlugs.gerarSlugSimples(caption));
                                            } else {
                                                mensagem.setMediaNome(documento.getString("filename"));
                                            }

                                            if (documento.containsKey("filename")) {
                                                String textoMensagem = documento.getString("filename");
                                                if (textoMensagem != null && !textoMensagem.isEmpty()) {
                                                    mensagem.setMensagem(textoMensagem);
                                                } else {
                                                    textoMensagem = "Arquivo enviado por " + from;
                                                    mensagem.setMensagem(textoMensagem);
                                                }
                                            } else {
                                                mensagem.setMensagem("Uma documento do tipo " + tipodocumento + " foi enviadada prara você");
                                            }
                                            mensagem.setMediaNome(documento.getString("id"));
                                            mensagem.setMediaMimeType(documento.getString("mime_type"));
                                            mensagens.add(mensagem);
                                            break;
                                        case IMAGEM:
                                            JsonObject imagem = joMensagem.getJsonObject("image");
                                            String tipoArquivo = imagem.getString("mime_type");
                                            mensagem.setCodigoMedia(imagem.getString("id"));
                                            if (imagem.containsKey("caption")) {
                                                String textoMensagem = imagem.getString("caption");
                                                if (textoMensagem != null && !textoMensagem.isEmpty()) {
                                                    mensagem.setMensagem(textoMensagem);
                                                } else {
                                                    textoMensagem = "Imagem enviada por " + from;
                                                    mensagem.setMensagem(textoMensagem);
                                                }
                                            } else {
                                                mensagem.setMensagem("Uma imagem do tipo " + tipoArquivo + " foi enviadada prara você");
                                            }
                                            mensagem.setMediaNome(imagem.getString("id"));
                                            mensagem.setMediaMimeType(imagem.getString("mime_type"));
                                            mensagens.add(mensagem);
                                            break;
                                        case AUDIO:
                                            JsonObject joAudio = joMensagem.getJsonObject("audio");
                                            String codigoArquivo = joAudio.getString("id");
                                            mensagem.setCodigoMedia(codigoArquivo);
                                            mensagem.setMediaMimeType(joAudio.getString("mime_type"));
                                            mensagens.add(mensagem);
                                            break;
                                        case VIDEO:

                                            break;
                                        case DESCONHECIDO:
                                            mensagem.setMensagem("Uma mensagem do tipo " + tipoMensagem + " foi enviadada prara você, nós estamos trabalhando no processamento deste tipo de arquivo, solicite ao cliente o envio em outro formato");
                                            mensagens.add(mensagem);
                                            break;
                                        default:
                                            throw new AssertionError();
                                    }

                                }
                            }

                        }
                        break;

                }
            }
        }
    }

}
