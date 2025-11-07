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
    SEGUNDOS_PADRAO_AGUARDANDO_CONTATO;

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

            default:
                throw new AssertionError();
        }

    }

}
