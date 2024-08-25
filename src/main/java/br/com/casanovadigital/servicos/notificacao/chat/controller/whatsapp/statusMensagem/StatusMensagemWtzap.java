/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.statusMensagem;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import java.util.Date;

/**
 *
 * @author salvio
 */
public class StatusMensagemWtzap {

    public StatusMensagemWtzap(FabTipoStatusMensagemWhtzap pTipo) {
        tipoStatus = pTipo;
    }

    private FabTipoStatusMensagemWhtzap tipoStatus;
    private String codigoMensagem;
    private String descricaoErro;
    private Date dataHora;
    private int codigoErro;
    private EntradaNumeroWhatsapp entrada;
    private String waIdContatoOrigem;

    public FabTipoStatusMensagemWhtzap getTipoStatus() {
        return tipoStatus;
    }

    public void setTipoStatus(FabTipoStatusMensagemWhtzap tipoStatus) {
        this.tipoStatus = tipoStatus;
    }

    public String getCodigoMensagem() {
        return codigoMensagem;
    }

    public void setCodigoMensagem(String codigoMensagem) {
        this.codigoMensagem = codigoMensagem;
    }
    private boolean respostaAjustada = false;

    private void buildDescricaoErro() {
        if (tipoStatus == null || codigoErro == 0 || respostaAjustada) {
            return;
        }
        respostaAjustada = true;
        switch (tipoStatus) {

            case FALHA_ENTREGA:
                if (codigoErro == 131047) {
                    descricaoErro = "A mensagem não foi enviada porque já se passaram mais de 24 horas desde a última resposta do cliente a este número.";
                }
                break;
            default:

        }
    }

    public String getDescricaoErro() {
        buildDescricaoErro();
        return descricaoErro;
    }

    public void setDescricaoErro(String descricaoErro) {
        this.descricaoErro = descricaoErro;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public int getCodigoErro() {
        return codigoErro;
    }

    public void setCodigoErro(int codigoErro) {
        this.codigoErro = codigoErro;
    }

    public EntradaNumeroWhatsapp getEntrada() {
        return entrada;
    }

    public void setEntrada(EntradaNumeroWhatsapp entrada) {
        this.entrada = entrada;
    }

    public String getWaIdContatoOrigem() {
        return waIdContatoOrigem;
    }

    public void setWaIdContatoOrigem(String waIdContatoOrigem) {
        this.waIdContatoOrigem = waIdContatoOrigem;
    }

}
