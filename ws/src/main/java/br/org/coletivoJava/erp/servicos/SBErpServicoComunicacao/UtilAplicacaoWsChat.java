/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringBuscaTrecho;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringFiltros;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author salvio
 */
public class UtilAplicacaoWsChat {

    public static String getTextoRotaExplicitaPorMensagemWtzp(EntradaNumeroWhatsapp pEntrada, MensagemWhatsapp pMensagem) {
        String novaRotaAuto = null;
        try {
            if (pMensagem.getPayloadRespostaProgramada() != null && !pMensagem.getPayloadRespostaProgramada().isEmpty()) {

                //ItfServicoNavegacao servicoNavegacao = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada);
                //Class classe = servicoNavegacao.getClasseTrilhaDeNavegacao(getContextoDeSessao().getContato(), p.getPayloadRespostaProgramada());
                if (AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(pEntrada).isRotaExiste(pMensagem.getPayloadRespostaProgramada())) {
                    novaRotaAuto = pMensagem.getPayloadRespostaProgramada();
                }

            } else {
                return getTextoRotaExplicitaTextoUsuario(pEntrada, pMensagem.getMensagem());
            }
        } catch (ErroComDevolucaoMensagemUsuario ex) {

            return null;
        }
        return novaRotaAuto;
    }

    public static String getTextoRotaExplicitaPorMensagemMatrix(EntradaNumeroWhatsapp pEntrada, ItfEventoMatix pEvento) {
        try {
            return getTextoRotaExplicitaTextoUsuario(pEntrada, pEvento.getContent().getString(pEvento.getContent().getString("body")));
        } catch (ErroComDevolucaoMensagemUsuario ex) {
            return null;
        }
    }

    private static String getTextoRotaExplicitaTextoUsuario(EntradaNumeroWhatsapp pEntrada, String pConteudo) throws ErroComDevolucaoMensagemUsuario {
        String novaRotaAuto = null;
        if (pConteudo == null) {
            return pConteudo;
        }
        //VERIFICA SE TEM PALAVRAS CHAVES QUE RETONAM AO MENO INICIAL.
        String possivelPalavraChave = UtilSBCoreStringFiltros.filtrarApenasLetra(pConteudo.toLowerCase());
        if (pConteudo != null) {
            for (String palavra : AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(pEntrada).getPalavrasParaCaminhoTrilhaRaiz()) {
                if (palavra.equals(possivelPalavraChave)) {
                    novaRotaAuto = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(pEntrada).getCaminhoTrilhaRaiz();
                    break;
                }
            }
        }

        List<String> partes = UtilSBCoreStringBuscaTrecho.getPartesEntreColchete(pConteudo);
        Optional<String> novaRotareferenciaTexto = partes.stream().filter(prota -> prota.contains("rota.")).findFirst();

        if (novaRotareferenciaTexto.isPresent()) {
            String caminho = novaRotareferenciaTexto.get();
            if (caminho.contains("rota.")) {
                novaRotaAuto = caminho.replace("rota.", "");
            } else {
                novaRotaAuto = caminho;
            }
        }
        return novaRotaAuto;
    }
}
