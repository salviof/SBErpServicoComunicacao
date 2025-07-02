/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.listenerMatrix;

import br.com.casanovadigital.servicos.notificacao.chat.ServicoNotificacaoChat;
import br.com.casanovadigital.servicos.notificacao.contextoChat.matrix.ContextoMatrix;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfNotificacaoUsuarioChat;
import br.org.coletivoJava.fw.api.erp.chat.notificacoes.ItfRetornoDeChamadaDeNotificacao;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import org.coletivojava.fw.api.tratamentoErros.FabErro;

/**
 *
 * @author salvio
 */
public class ListenerNotificacaoMatrixAuxiliadora implements ItfRetornoDeChamadaDeNotificacao {

    @Override
    public void onEventReceived(ItfNotificacaoUsuarioChat pNotificacao) {

        ItfChatSalaBean sala;
        try {

            System.out.println("Processando Listener notificação Auxiliadora, ouvindo " + pNotificacao.getCodigoSalaOrigem());
            sala = ContextoMatrix.getChatMatrixService().getSalaByCodigo(pNotificacao.getCodigoSalaOrigem());
            if (sala == null) {
                System.out.println("Sala" + pNotificacao.getCodigoSalaOrigem() + " não foi encontrado");
            } else {

                if (sala.getApelido() != null) {
                    FabTipoSalaMatrix tipo = FabTipoSalaMatrix.getTipoByAlias(sala.getApelido());
                    if (tipo != null) {
                        System.out.println("Tipo Sala  encontrada do tipo " + tipo + " abrind sessão:" + pNotificacao.getCodigoSalaOrigem());
                        ContextoMatrix.getChatMatrixService().salaAbrirSessao(sala);
                    }
                }
            }
            System.out.println("....");
        } catch (Throwable ex) {
            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "falha abrindo sessão sala" + pNotificacao.getCodigoSalaOrigem(), ex);
        }

    }

}
