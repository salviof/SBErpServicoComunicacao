/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCStringBuscaTrecho;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCStringFiltros;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author salvio
 */
public class UtilAplicacaoWsChat {

    /**
     * Verifica se o texto inteiro (ignorando caixa e espaços extras) é uma das
     * palavras de logoff do serviço de navegação da entrada.
     *
     * @param pEntrada entrada de whatsapp que define o serviço de navegação
     * @param pTexto texto digitado
     * @return true se o texto for um comando de logoff
     */
    public static boolean isPalavraLogoff(EntradaNumeroWhatsapp pEntrada, String pTexto) {
        if (pTexto == null) {
            return false;
        }
        final String textoNormalizado = pTexto.trim().replaceAll("\\s+", " ").toLowerCase();
        if (textoNormalizado.isEmpty()) {
            return false;
        }
        try {
            List<String> palavrasLogoff = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(pEntrada).getPalavrasParaLogoff();
            if (palavrasLogoff == null) {
                return false;
            }
            return palavrasLogoff.stream()
                    .anyMatch(palavra -> palavra != null && palavra.trim().toLowerCase().equals(textoNormalizado));
        } catch (ErroComDevolucaoMensagemUsuario ex) {
            return false;
        }
    }

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
            return getTextoRotaExplicitaTextoUsuario(pEntrada, pEvento.getContent().getString("body"));
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
        String possivelPalavraChave = UtilCRCStringFiltros.filtrarApenasLetra(pConteudo.toLowerCase());
        if (pConteudo != null) {
            for (String palavra : AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(pEntrada).getPalavrasParaCaminhoTrilhaRaiz()) {
                if (palavra.equals(possivelPalavraChave)) {
                    novaRotaAuto = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(pEntrada).getCaminhoTrilhaRaiz();
                    break;
                }
            }

        }

        List<String> partes = UtilCRCStringBuscaTrecho.getPartesEntreColchete(pConteudo);
        Optional<String> novaRotareferenciaTexto = partes.stream().filter(prota -> prota.contains("rota.") || prota.contains("consultoria.")).findFirst();

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
