/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos;

import com.super_bits.modulosSB.SBCore.modulos.objetos.registro.EntidadeSimples;

/**
 *
 * @author salvio
 */
public class TipoRota extends EntidadeSimples {

    private Long id;
    private FabTipoRotaMensagem tipoRotaMensagem;

    private String nome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FabTipoRotaMensagem getTipoRotaMensagem() {
        return tipoRotaMensagem;
    }

    public void setTipoRotaMensagem(FabTipoRotaMensagem tipoRotaMensagem) {
        this.tipoRotaMensagem = tipoRotaMensagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
