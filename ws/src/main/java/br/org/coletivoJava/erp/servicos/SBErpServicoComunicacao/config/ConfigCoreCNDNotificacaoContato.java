/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config;

import com.super_bits.modulosSB.Persistencia.ConfigGeral.ConfiguradorCoreDeProjetoJarPersistenciaAbstrato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.ConfiguradorCoreModelSemPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.ItfConfiguracaoCoreCustomizavel;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.modulos.comunicacao.CentralComunicacaoApenasLogs;
import com.super_bits.modulosSB.SBCore.modulos.comunicacao.CentralComunicacaoDesktop;

/**
 *
 * @author salvio
 */
public class ConfigCoreCNDNotificacaoContato extends ConfiguradorCoreDeProjetoJarPersistenciaAbstrato {

    public ConfigCoreCNDNotificacaoContato() {
        setIgnorarConfiguracaoAcoesDoSistema(true);

    }

    @Override
    public void defineFabricasDeACao(ItfConfiguracaoCoreCustomizavel pConfig) {

        setIgnorarConfiguracaoAcoesDoSistema(true);
        setIgnorarConfiguracaoPermissoes(false);
    }

    @Override
    public void defineClassesBasicas(ItfConfiguracaoCoreCustomizavel pConfiguracao) {
        super.defineClassesBasicas(pConfiguracao);
        setIgnorarConfiguracaoAcoesDoSistema(true);
        if (SBCore.isEmModoProducao()) {
            pConfiguracao.setCentralComunicacao(CentralComunicacaoApenasLogs.class);
        } else {
            pConfiguracao.setCentralComunicacao(CentralComunicacaoDesktop.class);
        }

    }

}
