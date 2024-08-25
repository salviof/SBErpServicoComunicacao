/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.statusMensagem;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.util.Date;
import org.coletivojava.fw.api.tratamentoErros.FabErro;

/**
 *
 * @author salvio
 */
public enum FabTipoStatusMensagemWhtzap {

    FALHA_ENTREGA,
    MENSAGEM_ENTREGUE,
    MENSAGEM_ENVIADA,
    MENSAGEM_LIDA,
    MENSAGEM_EXCLUIDA,
    DESCONHECIDO;

    public static StatusMensagemWtzap gerarStatusMensgem(EntradaNumeroWhatsapp pEntrada, JsonObject pMetadata, JsonObject pJson) {
        try {
            long dataHoraTs = Long.valueOf(pJson.getString("timestamp")) * 1000;
            Date dataHora = new Date(dataHoraTs);
            FabTipoStatusMensagemWhtzap tipo = DESCONHECIDO;
            if (pJson.containsKey("status")) {
                String status = pJson.getString("status");
                tipo = getTIpoByStatus(status);
            }
            if (tipo == null) {
                //pode ser status excluido que o whatzap não possui um status ainda conforme documentação em
                //https://developers.facebook.com/docs/whatsapp/cloud-api/webhooks/payload-examples#status--mensagem-exclu-da
            }

            StatusMensagemWtzap novoStatus = new StatusMensagemWtzap(tipo);
            novoStatus.setDataHora(dataHora);
            novoStatus.setWaIdContatoOrigem(pMetadata.getString("display_phone_number"));

            novoStatus.setCodigoMensagem(pJson.getString("id"));
            novoStatus.setEntrada(pEntrada);
            if (pJson.containsKey("errors")) {
                JsonArray array = pJson.getJsonArray("errors");
                JsonObject erroJson = (JsonObject) array.get(0);

                StringBuilder errostr = new StringBuilder();
                errostr.append("Erro: ");
                errostr.append(erroJson.getInt("code"));
                novoStatus.setCodigoErro(erroJson.getInt("code"));

                if (novoStatus.getCodigoErro() == 131051) {
                    novoStatus.setTipoStatus(FabTipoStatusMensagemWhtzap.MENSAGEM_EXCLUIDA);
                }
                errostr.append("|");
                errostr.append(erroJson.getString("title"));
                if (erroJson.containsKey("error_data")) {
                    JsonObject dataErroJson = erroJson.getJsonObject("error_data");
                    errostr.append("|");

                    errostr.append("|");
                    errostr.append(dataErroJson.getString("details"));
                }
                novoStatus.setDescricaoErro(errostr.toString());

            }
            return novoStatus;
        } catch (Throwable t) {
            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha criando status de mensagem", t);
            return null;
        }
    }

    public static FabTipoStatusMensagemWhtzap getTIpoByStatus(String status) {
        if (status.equals("failed")) {
            return FALHA_ENTREGA;
        }
        if (status.equals("sent")) {
            return FabTipoStatusMensagemWhtzap.MENSAGEM_ENVIADA;
        }
        if (status.equals("delivered")) {
            return FabTipoStatusMensagemWhtzap.MENSAGEM_ENTREGUE;
        }

        if (status.equals("read")) {
            return FabTipoStatusMensagemWhtzap.MENSAGEM_LIDA;
        }

        return null;
    }

}
