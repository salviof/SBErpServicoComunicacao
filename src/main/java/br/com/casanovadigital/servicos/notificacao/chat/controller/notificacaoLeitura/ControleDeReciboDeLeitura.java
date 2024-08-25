/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.notificacaoLeitura;

import br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author salvio
 */
public class ControleDeReciboDeLeitura {

    private static final Map<String, String> codCtxtoByrecibosEntregaChat = new ConcurrentHashMap<>();

    private static final Map<String, String> codCtxtoByrecibosEntregaWtsap = new ConcurrentHashMap<>();
    private static final Map<String, String> codCtxtoByrecibosEntregaMatrix = new ConcurrentHashMap<>();

    private static final Map<String, String> codEventoMatryxByCodWhatsapp = new ConcurrentHashMap<>();
    private static final Map<String, String> codCodWhatsappByCodMatrix = new ConcurrentHashMap<>();

    private static final Map<String, String> dataEventosWhatsapp = new ConcurrentHashMap<>();

    private synchronized void atualizarRegistro(FabTipoOperacaoNotificacaoLeitura ptIPOaCAO, String pCodigoContextoWtzp, String pCodigoEventoWhatsapp, String pCodigoEventoMatrix) {
        switch (ptIPOaCAO) {
            case REGISTRAR_ENCAMINHAMENTO_MATRIX_PARA_WHATSAPP:

                break;
            case REGISTRAR_ENCAMINHAMENTO_WHATSAPP_PARA_MATRIX:
                break;
            case BAIXA_LEITURA_MATRIX:
                break;
            case BAIXA_LEITURA_WHATSAPP:
                break;

            default:
                throw new AssertionError();
        }
    }

    public void registroEncaminhamentoMatrixParaWhatsapp() {

    }

    public void registrarEncaminhamentoWhatsapParaMatrix() {

    }

    public void darBaixaLeituraWhatsapp(String pCodEventoWhatsapp) {

    }

    public void darBaixaLeituraMatrix(String pCodigoEventoMAtrix) {

    }

}
