/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.super_bits.casanovadigital.servicos.messagens.model.agente;

import com.super_bits.modulosSB.Persistencia.registro.persistidos.ListenerEntidadePadrao;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoObjetoSB;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

/**
 *
 * @author salvio
 */
@Entity
@InfoObjetoSB(tags = {"Contato"}, plural = "Contatos", icone = "fa fa-user")
@EntityListeners(ListenerEntidadePadrao.class)
public class Contato extends Pessoa {

    private String waid;

    public String getWaid() {
        return waid;
    }

    public void setWaid(String waid) {
        this.waid = waid;
    }

}
