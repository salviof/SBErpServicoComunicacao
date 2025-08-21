/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp;

/**
 *
 * @author salvio
 */
public enum FabTipoMensagemWhatsapp {
    TEXTO_SIMPLES,
    REACAO,
    IMAGEM,
    AUDIO,
    VIDEO,
    DOCUMENTO,
    DESCONHECIDO,
    EVENTO_ENTREGA,
    EVENTO_LIDO,
    INTERATIVA;

    ;

    public static FabTipoMensagemWhatsapp getTipoMensagemByType(String pType) {

        switch (pType) {

            case "audio":
                return AUDIO;
            case "text":
                return TEXTO_SIMPLES;
            case "video":
                return VIDEO;
            case "image":
                return IMAGEM;
            case "reaction":
                return REACAO;
            case "document":
                return DOCUMENTO;
            case "interactive":
                return INTERATIVA;
            default:
                return DESCONHECIDO;
        }
    }

    public boolean isTipoMensagem() {
        switch (this) {
            case TEXTO_SIMPLES:
            case REACAO:
            case IMAGEM:
            case AUDIO:
            case VIDEO:
            case DOCUMENTO:
            case DESCONHECIDO:
            case INTERATIVA:
                return true;
            case EVENTO_ENTREGA:
            case EVENTO_LIDO:
                return false;

            default:
                throw new AssertionError();
        }
    }

    public boolean isTipoEvento() {
        switch (this) {
            case TEXTO_SIMPLES:
            case REACAO:
            case IMAGEM:
            case AUDIO:
            case VIDEO:
            case DOCUMENTO:
            case DESCONHECIDO:
            case INTERATIVA:
                return false;
            case EVENTO_ENTREGA:

            case EVENTO_LIDO:
                return true;

            default:
                throw new AssertionError();
        }
    }

    public boolean isTipoMedia() {
        switch (this) {
            case DOCUMENTO:
            case IMAGEM:
            case AUDIO:
            case VIDEO:
                return true;

            default:
            case INTERATIVA:
            case TEXTO_SIMPLES:
                return false;
        }
    }
}
