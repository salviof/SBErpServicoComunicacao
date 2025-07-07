/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.super_bits.casanovadigital.servicos.messagens.model.mensagem;

import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoCampo;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoObjetoSB;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.campo.FabTipoAtributoObjeto;
import java.util.Date;
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

    @InfoCampo(tipo = FabTipoAtributoObjeto.TEXTO_SIMPLES)
    private String corpoJsonRecebido;

    @InfoCampo(tipo = FabTipoAtributoObjeto.DATAHORA)
    private Date dataHoraRespostaDoAtendimento;

    public MensagemTrOrigemWhatsapp() {
        setTipoOrigem(FabTipoOrigem.WHATSAPP);
    }

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

    public String getCorpoJsonRecebido() {
        return corpoJsonRecebido;
    }

    public void setCorpoJsonRecebido(String corpoJsonRecebido) {
        this.corpoJsonRecebido = corpoJsonRecebido;
    }

    public Date getDataHoraRespostaDoAtendimento() {
        return dataHoraRespostaDoAtendimento;
    }

    public void setDataHoraRespostaDoAtendimento(Date dataHoraRespostaDoAtendimento) {
        this.dataHoraRespostaDoAtendimento = dataHoraRespostaDoAtendimento;
    }

}
