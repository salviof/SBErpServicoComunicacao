/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import java.util.Date;

/**
 *
 * @author salvio
 */
public class MensagemWhatsapp {

    private String id;
    private String codigoContaConectada;
    private String nome;
    private String telefone;
    private String mensagem;
    private String mediaNome;
    private String mediaMimeType;
    private Date dataHora;
    private String codigoMedia;
    private String urlAvatar;
    private FabTipoMensagemWhatsapp tipoMensagem;
    private EntradaNumeroWhatsapp entrada;

    public MensagemWhatsapp(FabTipoMensagemWhatsapp pTipo) {
        tipoMensagem = pTipo;
    }

    private ContatoWhatsapp contatoOrigem;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMediaNome() {
        return mediaNome;
    }

    public void setMediaNome(String mediaNome) {
        this.mediaNome = mediaNome;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public String getCodigoMedia() {
        return codigoMedia;
    }

    public void setCodigoMedia(String codigoMedia) {
        this.codigoMedia = codigoMedia;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public String getMediaMimeType() {
        return mediaMimeType;
    }

    public void setMediaMimeType(String MediaMimeType) {
        this.mediaMimeType = MediaMimeType;
    }

    public String getCodigoContaConectada() {
        return codigoContaConectada;
    }

    public void setCodigoContaConectada(String codigoContaConectada) {
        this.codigoContaConectada = codigoContaConectada;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContatoWhatsapp getContatoOrigem() {
        return contatoOrigem;
    }

    public void setContatoOrigem(ContatoWhatsapp contatoOrigem) {
        this.contatoOrigem = contatoOrigem;
    }

    public FabTipoMensagemWhatsapp getTipoMensagem() {
        return tipoMensagem;
    }

    public void setTipoMensagem(FabTipoMensagemWhatsapp tipoMensagem) {
        this.tipoMensagem = tipoMensagem;
    }

    public EntradaNumeroWhatsapp getEntrada() {
        return entrada;
    }

    public void setEntrada(EntradaNumeroWhatsapp entrada) {
        this.entrada = entrada;
    }

}
