/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.config;

import com.super_bits.modulosSB.SBCore.ConfigGeral.arquivosConfiguracao.ItfConfigModulo;
import com.super_bits.modulosSB.SBCore.ConfigGeral.arquivosConfiguracao.ItfFabConfigModulo;

/**
 *
 * @author salvio
 */
public enum FabConfigModuloSNSNotificacaoNovoContato implements ItfFabConfigModulo {

    TOKEN_ACESSO,
    CHAVE_PUBLICA_SNS,
    CHAVE_PRIVADA_SNS;

    @Override
    public String getValorPadrao() {
        return "NÃODEFINIDO";
    }

}
