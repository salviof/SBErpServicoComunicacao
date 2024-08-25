/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.config;

import com.super_bits.modulosSB.SBCore.ConfigGeral.ConfiguradorCoreModelSemPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.ItfConfiguracaoCoreCustomizavel;
import com.super_bits.modulosSB.SBCore.modulos.comunicacao.CentralComunicacaoApenasLogs;

/**
 *
 * @author salvio
 */
public class ConfigCoreCNDNotificacaoContato extends ConfiguradorCoreModelSemPersistencia {

    public ConfigCoreCNDNotificacaoContato() {
        setIgnorarConfiguracaoAcoesDoSistema(true);

    }

    @Override
    public void defineFabricasDeACao(ItfConfiguracaoCoreCustomizavel pConfig) {
        super.defineClassesBasicas(pConfig); //To change body of generated methods, choose Tools | Templates.
        setIgnorarConfiguracaoAcoesDoSistema(true);
        setIgnorarConfiguracaoPermissoes(true);
    }

    @Override
    public void defineClassesBasicas(ItfConfiguracaoCoreCustomizavel pConfiguracao) {
        setIgnorarConfiguracaoAcoesDoSistema(true);
        pConfiguracao.setCentralComunicacao(CentralComunicacaoApenasLogs.class);
    }

}
