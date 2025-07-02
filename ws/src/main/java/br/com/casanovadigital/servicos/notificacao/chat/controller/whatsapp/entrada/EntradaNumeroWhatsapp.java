/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada;

/**
 *
 * @author salvio
 */
public class EntradaNumeroWhatsapp {

    private String codigo;
    private String nome;
    private String telefonewa_id;
    private String telefoneDivulgacao;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefonewa_id() {
        return telefonewa_id;
    }

    public void setTelefonewa_id(String telefonewa_id) {
        this.telefonewa_id = telefonewa_id;
    }

    public String getTelefoneDivulgacao() {
        return telefoneDivulgacao;
    }

    public void setTelefoneDivulgacao(String telefoneDivulgacao) {
        this.telefoneDivulgacao = telefoneDivulgacao;
    }

}
