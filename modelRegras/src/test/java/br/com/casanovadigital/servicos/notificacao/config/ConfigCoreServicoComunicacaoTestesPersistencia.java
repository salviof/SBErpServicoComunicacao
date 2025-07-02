/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.config;

import com.super_bits.modulosSB.Persistencia.ConfigGeral.ConfiguradorCoreDeProjetoJarPersistenciaAbstrato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.ItfConfiguracaoCoreCustomizavel;

/**
 *
 * @author salvio
 */
public class ConfigCoreServicoComunicacaoTestesPersistencia extends ConfiguradorCoreDeProjetoJarPersistenciaAbstrato {

    public ConfigCoreServicoComunicacaoTestesPersistencia() {
        super();
    }

    @Override
    public void defineFabricasDeACao(ItfConfiguracaoCoreCustomizavel pConfig) {
        setIgnorarConfiguracaoAcoesDoSistema(true);
        setIgnorarConfiguracaoPermissoes(true);
        pConfig.setFabricaDeAcoes(new Class[]{});
    }

}
