/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
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

        ComoChatSalaBean sala;
        try {

            System.out.println("Processando Listener notificação Auxiliadora, ouvindo " + pNotificacao.getCodigoSalaOrigem());
            sala = AplicacaoWsChat.SERVICO_MATRIX.getSalaByCodigo(pNotificacao.getCodigoSalaOrigem());
            if (sala == null) {
                System.out.println("Sala" + pNotificacao.getCodigoSalaOrigem() + " não foi encontrado");
            } else {

                if (sala.getApelido() != null) {
                    FabTipoSalaMatrix tipo = FabTipoSalaMatrix.getTipoByAlias(sala.getApelido());
                    if (tipo != null) {
                        System.out.println("Tipo Sala  encontrada do tipo " + tipo + " abrind sessão:" + pNotificacao.getCodigoSalaOrigem());
                        AplicacaoWsChat.SERVICO_MATRIX.salaAbrirSessao(sala);
                    }
                }
            }
            System.out.println("....");
        } catch (Throwable ex) {
            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "falha abrindo sessão sala" + pNotificacao.getCodigoSalaOrigem(), ex);
        }

    }

}
