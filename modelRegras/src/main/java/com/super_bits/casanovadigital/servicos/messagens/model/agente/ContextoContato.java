/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.super_bits.casanovadigital.servicos.messagens.model.agente;

import com.super_bits.casanovadigital.servicos.messagens.model.agente.canais.tipos.CanalOminieChannel;
import com.super_bits.modulosSB.Persistencia.registro.persistidos.EntidadeSimples;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoCampo;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoObjetoSB;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.campo.FabTipoAtributoObjeto;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author salvio
 */
@Entity
@InfoObjetoSB(tags = "Contexto de sessão do contato", plural = "Contextos de sessão")
public class ContextoContato extends EntidadeSimples {

    @Id
    @InfoCampo(tipo = FabTipoAtributoObjeto.ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = Contato.class)
    private Contato contato;

    @InfoCampo(tipo = FabTipoAtributoObjeto.NOME)
    private String nomeContexto;

    @ManyToOne(targetEntity = CanalOminieChannel.class)
    private CanalOminieChannel canal;

    private String codigoEntrada;

    @InfoCampo(tipo = FabTipoAtributoObjeto.TEXTO_SIMPLES)
    private String trilhaAtual;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHoraInteracaoContato;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHoraInteracaoAtendimento;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHoraInicioSessao;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHoraFinalSessao;

    @Column(name = "jsonDadosDoContexto",
            columnDefinition = "VARCHAR(16000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String jsonDadosDoContexto;

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    public String getCodigoEntrada() {
        return codigoEntrada;
    }

    public void setCodigoEntrada(String codigoEntrada) {
        this.codigoEntrada = codigoEntrada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CanalOminieChannel getCanal() {
        return canal;
    }

    public void setCanal(CanalOminieChannel canal) {
        this.canal = canal;
    }

    public String getTrilhaAtual() {
        return trilhaAtual;
    }

    public void setTrilhaAtual(String trilhaAtual) {
        this.trilhaAtual = trilhaAtual;
    }

    public Date getDataHoraInteracaoContato() {
        return dataHoraInteracaoContato;
    }

    public void setDataHoraInteracaoContato(Date dataHoraInteracaoContato) {
        this.dataHoraInteracaoContato = dataHoraInteracaoContato;
    }

    public Date getDataHoraInteracaoAtendimento() {
        return dataHoraInteracaoAtendimento;
    }

    public void setDataHoraInteracaoAtendimento(Date dataHoraInteracaoAtendimento) {
        this.dataHoraInteracaoAtendimento = dataHoraInteracaoAtendimento;
    }

    public Date getDataHoraInicioSessao() {
        return dataHoraInicioSessao;
    }

    public void setDataHoraInicioSessao(Date dataHoraInicioSessao) {
        this.dataHoraInicioSessao = dataHoraInicioSessao;
    }

    public Date getDataHoraFinalSessao() {
        return dataHoraFinalSessao;
    }

    public void setDataHoraFinalSessao(Date dataHoraFinalSessao) {
        this.dataHoraFinalSessao = dataHoraFinalSessao;
    }

    public String getNomeContexto() {
        return nomeContexto;
    }

    public void setNomeContexto(String nomeContexto) {
        this.nomeContexto = nomeContexto;
    }

    public String getJsonDadosDoContexto() {
        return jsonDadosDoContexto;
    }

    public void setJsonDadosDoContexto(String jsonDadosDoContexto) {
        this.jsonDadosDoContexto = jsonDadosDoContexto;
    }

}
