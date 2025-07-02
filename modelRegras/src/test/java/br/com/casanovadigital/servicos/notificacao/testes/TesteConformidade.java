/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.testes;

import br.com.casanovadigital.servicos.notificacao.config.ConfigCoreServicoComunicacaoTestesPersistencia;
import com.super_bits.casanovadigital.servicos.messagens.model.configModel.ConfigPercistenciaServicoComunicacao;
import com.super_bits.modulosSB.Persistencia.ConfigGeral.SBPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import org.junit.Test;
import testesFW.TesteJunitSBPersistencia;

/**
 *
 * @author salvio
 */
public class TesteConformidade extends TesteJunitSBPersistencia {

    @Override
    protected void configAmbienteDesevolvimento() {
        SBCore.configurar(new ConfigCoreServicoComunicacaoTestesPersistencia(), SBCore.ESTADO_APP.DESENVOLVIMENTO);
        SBPersistencia.configuraJPA(new ConfigPercistenciaServicoComunicacao());

    }

    @Test
    public void teste() {
        gerarCodigoModelProjeto();
    }

}
