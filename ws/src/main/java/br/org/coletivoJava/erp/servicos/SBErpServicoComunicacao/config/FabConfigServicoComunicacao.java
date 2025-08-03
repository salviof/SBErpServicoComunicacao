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

    USUARIO_ATENDIMENTO_PADRAO;

    @Override
    public String getValorPadrao() {
        String dominio = FabConfigApiMatrixChat.DOMINIO_FEDERADO.getValorParametroSistema();
        String valorPadrao = "atendimento@" + dominio;
        return valorPadrao;
    }

}
