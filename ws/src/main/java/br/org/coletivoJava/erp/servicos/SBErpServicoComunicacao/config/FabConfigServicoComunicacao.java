/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config;

import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import com.super_bits.modulosSB.SBCore.ConfigGeral.arquivosConfiguracao.ItfFabConfigModulo;

/**
 *
 * @author salvio
 */
public enum FabConfigServicoComunicacao implements ItfFabConfigModulo {

    USUARIO_ATENDIMENTO_PADRAO,
    URL_CRM_SERVICE,;

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

            default:
                throw new AssertionError();
        }

    }

}
