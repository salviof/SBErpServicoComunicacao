/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos;

import com.super_bits.modulosSB.SBCore.modulos.fabrica.ComoFabrica;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoObjetoDaFabrica;

/**
 *
 * @author salvio
 */
public enum FabTipoRotaMensagem implements ComoFabrica {

    @InfoObjetoDaFabrica(classeObjeto = TipoRota.class, id = 1, nomeObjeto = "Menu de Opções")
    MENU_OPCOES,
    @InfoObjetoDaFabrica(classeObjeto = TipoRota.class, id = 2, nomeObjeto = "Resposta webservice")
    RESPOSTA_WEBSERVICE,
    @InfoObjetoDaFabrica(classeObjeto = TipoRota.class, id = 3, nomeObjeto = "Retorno de link")
    RETORNO_LINK,
    @InfoObjetoDaFabrica(classeObjeto = TipoRota.class, id = 4, nomeObjeto = "Encaminhamento")
    ENCAMINHAMENTO;

    @Override
    public TipoRota getRegistro() {
        TipoRota tipo = (TipoRota) ComoFabrica.super.getRegistro();
        tipo.setTipoRotaMensagem(this);
        return tipo;
    }

}
