/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens;

import br.com.casanovadigital.servicos.notificacao.chat.controller.notificacaoLeitura.EventoEncMatrixParaWtsap;
import br.com.casanovadigital.servicos.notificacao.chat.controller.notificacaoLeitura.EventoEncWhatsapParaMatrix;
import br.com.casanovadigital.servicos.notificacao.chat.controller.notificacaoLeitura.FabTipoOperacaoNotificacaoLeitura;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author salvio
 */
public class ControleDeReciboDeLeitura {

    private final Map<String, List<EventoEncMatrixParaWtsap>> ecaminhamentoMatrixParaWhatsap = new HashMap<>();
    private final Map<String, List<EventoEncWhatsapParaMatrix>> ecaminhamentoWhatsappParaMatrix = new HashMap<>();

    public void registrarEventoEncaminhamentoMatrixParaWhatsapp(String pCodigoContextoWtzp, RoomEvent pEventoMatrix, String pCodigEventoWhatsapp) {

        EventoEncMatrixParaWtsap evento = new EventoEncMatrixParaWtsap(pEventoMatrix, pCodigEventoWhatsapp);
        controleDeEventos(FabTipoOperacaoNotificacaoLeitura.REGISTRAR_ENCAMINHAMENTO_MATRIX_PARA_WHATSAPP, pCodigoContextoWtzp,
                evento, null);
    }

    public void registrarEventoEncaminhamentoWhatsappParaMatrix(String pCodigoContextoWtzp, MensagemWhatsapp pMensagemWhatsapp, String pCodEventoMatrix) {

        EventoEncWhatsapParaMatrix evento = new EventoEncWhatsapParaMatrix(pMensagemWhatsapp, pCodEventoMatrix);
        controleDeEventos(FabTipoOperacaoNotificacaoLeitura.REGISTRAR_ENCAMINHAMENTO_WHATSAPP_PARA_MATRIX, pCodigoContextoWtzp,
                null, evento);
    }

    public String getCodigoEventoWhatsappByCodigoMatrix(String pCodigoEventoMatrix) {

        for (String entrada : ecaminhamentoWhatsappParaMatrix.keySet()) {
            Optional<EventoEncWhatsapParaMatrix> pesquisa = ecaminhamentoWhatsappParaMatrix.get(entrada).stream().filter(evento -> evento.getCodigoEventoMatrix().equals(pCodigoEventoMatrix)).findFirst();
            if (pesquisa.isPresent()) {
                return pesquisa.get().getCodigoEventoMatrix();

            }
        }
        for (String entrada : ecaminhamentoMatrixParaWhatsap.keySet()) {
            Optional<EventoEncWhatsapParaMatrix> pesquisa = ecaminhamentoWhatsappParaMatrix.get(entrada).stream().filter(evento -> evento.getCodigoEventoMatrix().equals(pCodigoEventoMatrix)).findFirst();
            if (pesquisa.isPresent()) {
                return pesquisa.get().getCodigoEventoWhatsapp();

            }
        }
        return null;
    }

    public String getCodigoEventoMatrixByCodigoWhatsapp(String pCodigoWhatsapp) {
        for (String entrada : ecaminhamentoMatrixParaWhatsap.keySet()) {
            Optional<EventoEncMatrixParaWtsap> pesquisa = ecaminhamentoMatrixParaWhatsap.get(entrada).stream().filter(evento -> evento.getCodigoEventoWhatsapp().equals(pCodigoWhatsapp)).findFirst();
            if (pesquisa.isPresent()) {
                return pesquisa.get().getCodigoEventoMatrix();

            }
        }
        for (String entrada : ecaminhamentoWhatsappParaMatrix.keySet()) {
            Optional<EventoEncWhatsapParaMatrix> pesquisa = ecaminhamentoWhatsappParaMatrix.get(entrada).stream().filter(evento -> evento.getCodigoEventoWhatsapp().equals(pCodigoWhatsapp)).findFirst();
            if (pesquisa.isPresent()) {
                return pesquisa.get().getCodigoEventoMatrix();

            }
        }

        return null;
    }

    private synchronized void controleDeEventos(FabTipoOperacaoNotificacaoLeitura pTipoOperacao, String pCodContextoWtsap, EventoEncMatrixParaWtsap pEventoMatrixPWtsp, EventoEncWhatsapParaMatrix pEventoWhatsapparaMatrix) {

        switch (pTipoOperacao) {

            case REGISTRAR_ENCAMINHAMENTO_MATRIX_PARA_WHATSAPP:
                if (!ecaminhamentoMatrixParaWhatsap.containsKey(pCodContextoWtsap)) {
                    ecaminhamentoMatrixParaWhatsap.put(pCodContextoWtsap, new ArrayList<>());
                }
                if (ecaminhamentoMatrixParaWhatsap.get(pCodContextoWtsap).size() > 10) {
                    ecaminhamentoMatrixParaWhatsap.get(pCodContextoWtsap).remove(0);
                }

                ecaminhamentoMatrixParaWhatsap.get(pCodContextoWtsap).add(pEventoMatrixPWtsp);

                break;
            case REGISTRAR_ENCAMINHAMENTO_WHATSAPP_PARA_MATRIX:
                if (!ecaminhamentoWhatsappParaMatrix.containsKey(pCodContextoWtsap)) {
                    ecaminhamentoWhatsappParaMatrix.put(pCodContextoWtsap, new ArrayList<>());
                }
                if (ecaminhamentoWhatsappParaMatrix.get(pCodContextoWtsap).size() > 10) {
                    ecaminhamentoWhatsappParaMatrix.get(pCodContextoWtsap).remove(0);
                }
                ecaminhamentoWhatsappParaMatrix.get(pCodContextoWtsap).add(pEventoWhatsapparaMatrix);
                break;
            case BAIXA_LEITURA_MATRIX:
                System.out.println("Baixa leitura Matrix");
                break;
            case BAIXA_LEITURA_WHATSAPP:
                System.out.println("Baixa leitura Whatsapp");
                break;
            default:
                throw new AssertionError();
        }

    }

    public String getCodigoContextoWtspByEventoMatrix(String pCodigo) {
        for (String entrada : ecaminhamentoMatrixParaWhatsap.keySet()) {
            Optional<EventoEncMatrixParaWtsap> pesquisa = ecaminhamentoMatrixParaWhatsap.get(entrada).stream().filter(evento -> evento.getCodigoEventoMatrix().equals(pCodigo)).findFirst();
            if (pesquisa.isPresent()) {
                return entrada;
            }
        }
        return null;

    }

    public String getCodigoContextoWtspByEventoWhatsapp(String pCodigoWhatsapp) {
        for (String contexto : ecaminhamentoMatrixParaWhatsap.keySet()) {
            Optional<EventoEncMatrixParaWtsap> pesquisa = ecaminhamentoMatrixParaWhatsap.get(contexto).stream().filter(evento -> evento.getCodigoEventoWhatsapp().equals(pCodigoWhatsapp)).findFirst();
            if (pesquisa.isPresent()) {
                return contexto;

            }
        }
        for (String contexto : ecaminhamentoWhatsappParaMatrix.keySet()) {
            Optional<EventoEncWhatsapParaMatrix> pesquisa = ecaminhamentoWhatsappParaMatrix.get(contexto).stream().filter(evento -> evento.getCodigoEventoWhatsapp().equals(pCodigoWhatsapp)).findFirst();
            if (pesquisa.isPresent()) {
                return contexto;

            }
        }
        return null;
    }

}
