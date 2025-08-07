/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark;

import br.org.coletivoJava.fw.ws.restFull.ErroAcessoNegado;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import spark.Request;
import spark.Route;

/**
 *
 * @author salvio
 */
public interface ItfRecepcaoPacoteServidor extends Route {

    public String executarRegraDeNegocio(String pCorpo) throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro;

    public RespostaHttpResumo getRepostaHttpResumo();

    public void validarParamentros(Request pRequisicao) throws ErroParamentosInvalidos;

    public void validarPermissao() throws ErroAcessoNegado;

}
