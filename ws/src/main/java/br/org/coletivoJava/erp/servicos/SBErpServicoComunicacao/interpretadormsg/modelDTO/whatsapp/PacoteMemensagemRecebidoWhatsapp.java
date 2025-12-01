/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.FabTipoStatusMensagemWhtzap;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroCriandoContato;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCStringSlugs;
import com.super_bits.modulosSB.SBCore.UtilGeral.json.ErroProcessandoJson;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author salvio
 */
public class PacoteMemensagemRecebidoWhatsapp {

    private JsonObject dados;

    private List<MensagemWhatsapp> mensagens = new ArrayList<>();
    private Map<String, String> mapaContatos = new HashMap<>();
    private List<EventoMensagemWtzap> statusMensagem = new ArrayList<>();

    public PacoteMemensagemRecebidoWhatsapp(String pDados) throws ErroProcessandoJson {
        System.out.println(pDados);
        this.dados = UtilCRCJson.getJsonObjectByTextoComTratamento(pDados);
        processarJson();

    }

    public JsonObject getDadosJson() {
        return dados;
    }

    public List<MensagemWhatsapp> getMensagens() {
        return mensagens;
    }

    public List<EventoMensagemWtzap> getStatusMensagem() {
        return statusMensagem;
    }

    private void processarJson() throws ErroProcessandoJson {
        if (dados.getString("object").equals("whatsapp_business_account")) {
            JsonArray entradas = dados.getJsonArray("entry");
            for (JsonValue jv : entradas) {
                String codigoAPlicacao = jv.asJsonObject().getString("id");

                switch (jv.getValueType()) {

                    case OBJECT:
                        JsonArray jsonAlteracoes = jv.asJsonObject().getJsonArray("changes");
                        for (JsonValue alteracao : jsonAlteracoes) {

                            JsonObject valorAlteracao = alteracao.asJsonObject().getJsonObject("value");
                            JsonObject metadata = valorAlteracao.getJsonObject("metadata");
                            String codigoTelfoneIntegrado = metadata.getString("phone_number_id");
                            EntradaNumeroWhatsapp entrada;
                            try {
                                entrada = AplicacaoWsChat.getEntradaByCodigoEntrada(codigoTelfoneIntegrado);
                            } catch (ErroRegraDeNegocio ex) {
                                throw new ErroProcessandoJson("Código de entrada não reconhecido " + codigoTelfoneIntegrado);
                            }

                            if (entrada == null) {
                                throw new ErroProcessandoJson("Código de entrada não reconhecido " + codigoTelfoneIntegrado);
                            }

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
                                    EventoMensagemWtzap status = FabTipoStatusMensagemWhtzap.gerarStatusMensgem(entrada, metadataJson, jvStatus.asJsonObject());
                                    if (status == null) {
                                        System.out.println("Estatus não reconhecido para json:");
                                        UtilCRCJson.getTextoByJsonObjeect(jvStatus.asJsonObject());
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
                                    mensagem.setCodigoContaConectada(codigoTelfoneIntegrado);
                                    String from = joMensagem.getString("from");
                                    String nm = mapaContatos.get(from);
                                    mensagem.setNome(nm);
                                    mensagem.setTelefone(from);
                                    try {
                                        mensagem.setContatoOrigem(new ContatoWhatsapp(from, mapaContatos.get(from)));
                                    } catch (ErroCriandoContato ex) {
                                        throw new ErroProcessandoJson("Falha criando contato com dados:" + from);
                                    }
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
                                                mensagem.setMediaNome(UtilCRCStringSlugs.gerarSlugSimples(caption));
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
                                            mensagem.setMensagem("Uma mensagem do tipo " + tipoMensagem + " foi enviadada prara você, nós estamos trabalhando no processamento deste tipo de arquivo, solicite ao cliente o envio em outro formato");
                                            mensagens.add(mensagem);
                                            break;
                                        case DESCONHECIDO:
                                            mensagem.setMensagem("Uma mensagem do tipo " + tipoMensagem + " foi enviadada prara você, nós estamos trabalhando no processamento deste tipo de arquivo, solicite ao cliente o envio em outro formato");
                                            mensagens.add(mensagem);
                                            break;
                                        case INTERATIVA:

                                            JsonObject interacao = joMensagem.getJsonObject("interactive");

                                            if (interacao.getString("type").equals("button_reply")) {
                                                JsonObject respostaBotao = interacao.getJsonObject("button_reply");
                                                if (respostaBotao.containsKey("payload")) {
                                                    mensagem.setPayloadRespostaProgramada(respostaBotao.getString("payload"));
                                                } else if (respostaBotao.containsKey("id")) {
                                                    mensagem.setPayloadRespostaProgramada(respostaBotao.getString("id"));
                                                }
                                            }
                                            if (interacao.getString("type").equals("list_reply")) {
                                                JsonObject respostaBotao = interacao.getJsonObject("list_reply");
                                                if (respostaBotao.containsKey("payload")) {
                                                    mensagem.setPayloadRespostaProgramada(respostaBotao.getString("payload"));
                                                } else if (respostaBotao.containsKey("id")) {
                                                    mensagem.setPayloadRespostaProgramada(respostaBotao.getString("id"));
                                                }
                                            }
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
