/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;

/**
 *
 * @author salvio
 */
public class RotaLinkAcesso extends RotaMensagemContato {

    public RotaLinkAcesso(Contato p) {
        super(FabTipoRotaMensagem.RETORNO_LINK, p);
    }

}
