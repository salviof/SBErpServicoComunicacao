/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config;

import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.ConfigGeral.arquivosConfiguracao.ItfFabConfigModulo;

/**
 *
 * @author salvio
 */
public enum FabConfigServicoComunicacao implements ItfFabConfigModulo {

    USUARIO_ATENDIMENTO_PADRAO,
    URL_CRM_SERVICE,
    SEGUNDOS_PADRAO_AGUARDANDO_ATENDIMENTO,
    SEGUNDOS_PADRAO_AGUARDANDO_CONTATO,
    /**
     * Quanto a requisição do webhook espera pela criação da sala antes de
     * avisar o contato e passar a entrega para a fila.
     */
    SEGUNDOS_TIMEOUT_SALA_SOB_DEMANDA,
    /**
     * Prazo para entregar uma mensagem que ficou na fila. Vencido o prazo, o
     * contato é avisado de que a mensagem não foi entregue. Prazo curto é
     * deliberado: mantém a conversa dentro da janela de 24h do WhatsApp.
     */
    MINUTOS_PRAZO_ENTREGA_PENDENTE,
    /**
     * Intervalo entre os ciclos do dreno da fila de entregas pendentes.
     */
    SEGUNDOS_INTERVALO_DRENO_ENTREGA,
    /**
     * Liga o dreno neste processo. Deve ficar ligado em apenas uma instância
     * por banco: duas instâncias apontando para o mesmo MySQL disputariam a
     * mesma fila e poderiam repetir os avisos ao contato.
     */
    DRENO_ENTREGA_PENDENTE_ATIVO;

    @Override
    public String getValorPadrao() {
        String dominio = FabConfigApiMatrixChat.DOMINIO_FEDERADO.getValorParametroSistema();
        switch (this) {

            case USUARIO_ATENDIMENTO_PADRAO:

                String atendimentoPadrao = "atendimento@" + dominio;
                return atendimentoPadrao;

            case URL_CRM_SERVICE:

                String crmlHostPadrao = "https://crm." + dominio;
                return crmlHostPadrao;
            case SEGUNDOS_PADRAO_AGUARDANDO_ATENDIMENTO:
                if (SBCore.isEmModoDesenvolvimento()) {
                    return "30";
                } else {
                    return "900";
                }

            case SEGUNDOS_PADRAO_AGUARDANDO_CONTATO:
                if (SBCore.isEmModoDesenvolvimento()) {
                    return "120";
                } else {
                    return "90000";
                }

            case SEGUNDOS_TIMEOUT_SALA_SOB_DEMANDA:
                return "3";

            case MINUTOS_PRAZO_ENTREGA_PENDENTE:
                return "10";

            case SEGUNDOS_INTERVALO_DRENO_ENTREGA:
                return "15";

            case DRENO_ENTREGA_PENDENTE_ATIVO:
                return "true";

            default:
                throw new AssertionError();
        }

    }

}
