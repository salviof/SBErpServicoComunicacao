/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.super_bits.casanovadigital.servicos.messagens.model.mensagem;

import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoCampo;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoObjetoSB;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.campo.FabTipoAtributoObjeto;
import javax.persistence.Entity;

/**
 *
 * @author salvio
 */
@Entity
@InfoObjetoSB(tags = "Mensagem pelo Wtzp", plural = "Mensagens disparadas pelo whatsapp")
public class MensagemTrOrigemWhatsapp extends MensagemTransito {

    @InfoCampo(tipo = FabTipoAtributoObjeto.TEXTO_SIMPLES)
    private String codigoRegistroMensagemWhatsapp;

    @InfoCampo(tipo = FabTipoAtributoObjeto.TEXTO_SIMPLES)
    private String codigoEncaminhamentoMatrix;

    private String corpoJsonRecebido;

    public String getCodigoRegistroMensagemWhatsapp() {
        return codigoRegistroMensagemWhatsapp;
    }

    public void setCodigoRegistroMensagemWhatsapp(String codigoRegistroMensagemWhatsapp) {
        this.codigoRegistroMensagemWhatsapp = codigoRegistroMensagemWhatsapp;
    }

    public String getCodigoEncaminhamentoMatrix() {
        return codigoEncaminhamentoMatrix;
    }

    public void setCodigoEncaminhamentoMatrix(String codigoEncaminhamentoMatrix) {
        this.codigoEncaminhamentoMatrix = codigoEncaminhamentoMatrix;
    }

}
