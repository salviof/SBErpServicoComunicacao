/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens;

import java.util.List;

/**
 *
 * @author salvio
 */
public class TipoContexto {

    private final List<String> codigoWtzapRegistrado;
    private final List<String> servidoresMatrix;
    private final List<String> telefones;
    private final Class classeProcessamento;
    private final Class classeContextoLoad;
    private final String codigoWhatsappServidor;
    private final String servicoMatrix;

    public TipoContexto(List<String> codigoWtzapRegistrado, List<String> servidoresMatrix,
            List<String> telefones, String pCodigoWtsapp, String pServidorMatrix, Class pClasseProcessamento,
            Class pClasseLoad) {
        this.codigoWtzapRegistrado = codigoWtzapRegistrado;
        this.servidoresMatrix = servidoresMatrix;
        this.telefones = telefones;
        classeProcessamento = pClasseProcessamento;
        classeContextoLoad = pClasseLoad;
        codigoWhatsappServidor = pCodigoWtsapp;
        servicoMatrix = pServidorMatrix;
    }

    public List<String> getCodigoWtzapRegistrado() {
        return codigoWtzapRegistrado;
    }

    public List<String> getServidoresMatrix() {
        return servidoresMatrix;
    }

    public List<String> getTelefones() {
        return telefones;
    }

    public Class getClasseProcessamento() {
        return classeProcessamento;
    }

    public Class getClasseContextoLoad() {
        return classeContextoLoad;
    }

    public String getCodigoWhatsappServidor() {
        return codigoWhatsappServidor;
    }

    public String getServicoMatrix() {
        return servicoMatrix;
    }

}
