/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.super_bits.casanovadigital.servicos.messagens.model.mensagem;

import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.Persistencia.registro.persistidos.EntidadeSimplesORM;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoCampo;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoObjetoSB;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.campo.FabTipoAtributoObjeto;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author salvio
 */
@Entity
@InfoObjetoSB(tags = "Encaminhamento matrix", plural = "Encaminhamentos do matrix para Whatsapp")
public class EncaminhamentoMatrixParaWtzp extends EntidadeSimplesORM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @InfoCampo(tipo = FabTipoAtributoObjeto.NOME)
    private String nome;

    @InfoCampo(tipo = FabTipoAtributoObjeto.TEXTO_SIMPLES)
    private String reciboRegistrooWtzp;

    @InfoCampo(tipo = FabTipoAtributoObjeto.VERDADEIRO_FALSO)
    private boolean foiEnviadoPeloWhatsapp;

    @InfoCampo(tipo = FabTipoAtributoObjeto.VERDADEIRO_FALSO)
    private boolean foiEntregueNoCelularDoContato;

    @InfoCampo(tipo = FabTipoAtributoObjeto.VERDADEIRO_FALSO)
    private boolean foiLidoPeloConatoWtzp;

    @ManyToOne(targetEntity = Contato.class)
    private Contato contato;

    @ManyToOne(targetEntity = MensagemTrOrigemMatrix.class)
    private MensagemTrOrigemMatrix mensagem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getReciboRegistrooWtzp() {
        return reciboRegistrooWtzp;
    }

    public void setReciboRegistrooWtzp(String reciboRegistrooWtzp) {
        this.reciboRegistrooWtzp = reciboRegistrooWtzp;
    }

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    public boolean isFoiEnviadoPeloWhatsapp() {
        return foiEnviadoPeloWhatsapp;
    }

    public void setFoiEnviadoPeloWhatsapp(boolean foiEnviadoPeloWhatsapp) {
        this.foiEnviadoPeloWhatsapp = foiEnviadoPeloWhatsapp;
    }

    public boolean isFoiEntregueNoCelularDoContato() {
        return foiEntregueNoCelularDoContato;
    }

    public void setFoiEntregueNoCelularDoContato(boolean foiEntregueNoCelularDoContato) {
        this.foiEntregueNoCelularDoContato = foiEntregueNoCelularDoContato;
    }

    public boolean isFoiLidoPeloConatoWtzp() {
        return foiLidoPeloConatoWtzp;
    }

    public void setFoiLidoPeloConatoWtzp(boolean foiLidoPeloConatoWtzp) {
        this.foiLidoPeloConatoWtzp = foiLidoPeloConatoWtzp;
    }

    public MensagemTrOrigemMatrix getMensagem() {
        return mensagem;
    }

    public void setMensagem(MensagemTrOrigemMatrix mensagem) {
        this.mensagem = mensagem;
    }

}
