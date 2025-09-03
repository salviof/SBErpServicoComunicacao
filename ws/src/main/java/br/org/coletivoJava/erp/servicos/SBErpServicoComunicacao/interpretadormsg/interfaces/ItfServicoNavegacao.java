/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import jakarta.json.JsonObject;
import java.util.List;

/**
 *
 * @author salvio
 */
public interface ItfServicoNavegacao {

    /**
     *
     * @param pContato
     * @param pCaminho
     * @return
     */
    public Class<? extends ItfTrilhaNavegacao> getClasseTrilhaDeNavegacao(Contato pContato, String pCaminho);

    public JsonObject gerarJsonDadosDeSessao(Contato pContato);

    public String getCaminhoTrilhaRaiz();

    public List<String> getPalavrasParaCaminhoTrilhaRaiz();

    public boolean isRotaExiste(String pRota);

    public void validarServicoNavegacao() throws ErroFalhaEncaminhando;

}
