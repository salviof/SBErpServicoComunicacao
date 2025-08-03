/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.TipoRota;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Pessoa;

/**
 *
 * @author salvio
 */
public interface ItfRotaMensagemDoContato {

    public Long getId();

    public TipoRota getTipoRota();

    public void setId(Long id);

    public void setTipoRota(TipoRota tipoRota);

    public RotaEncaminhamentoSala getComoRotaEncaminhamentoMatrix();

    public RotaMenuOpcoes getComoRotaMenuOpcoes();

    public RotaLinkAcesso getComoRotaLinkAcesso();

    public RotaWebservice getComoRotaWebService();

    public Pessoa getContatoPrincipal();

}
