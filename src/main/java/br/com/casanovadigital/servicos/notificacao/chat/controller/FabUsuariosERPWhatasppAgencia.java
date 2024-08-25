/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller;

import com.super_bits.modulos.SBAcessosModel.model.GrupoUsuarioSB;
import com.super_bits.modulos.SBAcessosModel.model.UsuarioSB;
import com.super_bits.modulosSB.SBCore.modulos.fabrica.ItfFabrica;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoObjetoDaFabrica;
import com.super_bits.modulosSB.SBCore.modulos.objetos.registro.Interfaces.basico.ItfBeanSimples;

/**
 *
 * @author salvio
 */
public enum FabUsuariosERPWhatasppAgencia implements ItfFabrica {

    @InfoObjetoDaFabrica(classeObjeto = UsuarioSB.class, id = 1, nomeObjeto = "Financeiro")
    USUARIO_INTEGRACAO;

    @Override
    public Object getRegistro() {
        ItfBeanSimples item = (ItfBeanSimples) ItfFabrica.super.getRegistro();
        switch (this) {

            case USUARIO_INTEGRACAO:
                GrupoUsuarioSB grpUsuariosHomologados = new GrupoUsuarioSB();
                grpUsuariosHomologados.setNome("Usuarios Homologados Agencia");
                UsuarioSB pUsuario = (UsuarioSB) item;
                pUsuario.setEmail("financeiro@casanovadigital.com.br");
                pUsuario.setGrupo(grpUsuariosHomologados);
                return pUsuario;

            default:
                throw new AssertionError();
        }

    }
}
