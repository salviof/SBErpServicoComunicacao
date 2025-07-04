/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.super_bits.casanovadigital.servicos.messagens.model.mensagem;

import com.super_bits.modulosSB.Persistencia.registro.persistidos.ListenerEntidadePadrao;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoCampo;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoObjetoSB;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.campo.FabTipoAtributoObjeto;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author salvio
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipoMensagem")
@EntityListeners(ListenerEntidadePadrao.class)
@InfoObjetoSB(tags = "Mensagem em Transito", plural = "Mensagens em transito")
public class MensagemTransito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Column(nullable = false, updatable = false, insertable = false)
    private String tipoMensagem;

    private String entradaIdentificadorWhatsapp;
    private String salaCodigoMatrix;

    @Enumerated(EnumType.STRING)
    private FabTipoOrigem tipoOrigem;

    @InfoCampo(tipo = FabTipoAtributoObjeto.VERDADEIRO_FALSO)
    private boolean registrado = true;
    @InfoCampo(tipo = FabTipoAtributoObjeto.VERDADEIRO_FALSO)
    private boolean encaminhado = false;
    @InfoCampo(tipo = FabTipoAtributoObjeto.VERDADEIRO_FALSO)
    private boolean lido = false;

    @Temporal(TemporalType.TIMESTAMP)
    @InfoCampo(tipo = FabTipoAtributoObjeto.REG_DATAINSERCAO)
    private Date dataHoraCriacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date daHoraExpirar;

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

    public String getTipoMensagem() {
        return tipoMensagem;
    }

    public void setTipoMensagem(String tipoMensagem) {
        this.tipoMensagem = tipoMensagem;
    }

    public FabTipoOrigem getTipoOrigem() {
        return tipoOrigem;
    }

    public void setTipoOrigem(FabTipoOrigem tipoOrigem) {
        this.tipoOrigem = tipoOrigem;
    }

    public boolean isRegistrado() {
        return registrado;
    }

    public void setRegistrado(boolean registrado) {
        this.registrado = registrado;
    }

    public boolean isEncaminhado() {
        return encaminhado;
    }

    public void setEncaminhado(boolean encaminhado) {
        this.encaminhado = encaminhado;
    }

    public boolean isLido() {
        return lido;
    }

    public void setLido(boolean lido) {
        this.lido = lido;
    }

    public Date getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public void setDataHoraCriacao(Date dataHoraCriacao) {
        this.dataHoraCriacao = dataHoraCriacao;
    }

    public Date getDaHoraExpirar() {
        return daHoraExpirar;
    }

    public void setDaHoraExpirar(Date daHoraExpirar) {
        this.daHoraExpirar = daHoraExpirar;
    }

    public String getEntradaIdentificadorWhatsapp() {
        return entradaIdentificadorWhatsapp;
    }

    public void setEntradaIdentificadorWhatsapp(String entradaIdentificadorWhatsapp) {
        this.entradaIdentificadorWhatsapp = entradaIdentificadorWhatsapp;
    }

    public String getSalaCodigoMatrix() {
        return salaCodigoMatrix;
    }

    public void setSalaCodigoMatrix(String salaCodigoMatrix) {
        this.salaCodigoMatrix = salaCodigoMatrix;
    }

}
