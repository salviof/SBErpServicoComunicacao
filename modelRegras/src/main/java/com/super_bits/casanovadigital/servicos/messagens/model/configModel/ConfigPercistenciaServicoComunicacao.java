/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.super_bits.casanovadigital.servicos.messagens.model.configModel;

import com.super_bits.modulosSB.Persistencia.ConfigGeral.ItfConfigSBPersistencia;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreDataHora;
import com.super_bits.modulosSB.SBCore.modulos.fabrica.ItfFabrica;

/**
 *
 * @author Salvio
 */
public class ConfigPercistenciaServicoComunicacao implements ItfConfigSBPersistencia {

    @Override
    public String bancoPrincipal() {
        return "SBErpServicoComunicacaoModel";
    }

    @Override
    public String[] bancosExtra() {
        return new String[0];
    }

    @Override
    public String formatoDataBanco() {
        return UtilSBCoreDataHora.datahoraSistemaFr.toString();
    }

    @Override
    public String formatoDataUsuario() {
        return UtilSBCoreDataHora.horaUsuarioFr.toString();
    }

    @Override
    public String pastaImagensJPA() {
        return "/img";
    }

    @Override
    public void criarBancoInicial() {

    }

    @Override
    public Class<? extends ItfFabrica>[] fabricasRegistrosIniciais() {
        return new Class[]{ //    FabAtividadeCRMAutoexecucao.class
        };
    }

}
