/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.repostas.v1.teste.contato;

import br.com.casanovadigital.servicos.notificacao.chat.controller.UtilAgenciaContatos;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.json.ErroProcessandoJson;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author salvio
 */
public class AtendimentoByNumeroLead extends RotaSparkPadrao {

    private String telefoneCentralAtendimento;
    private String telefoneLead;

    @Override
    public void validarParamentros() throws ErroParamentosInvalidos {
        telefoneLead = requisicao.raw().getParameter("telefoneLead");
        telefoneCentralAtendimento = requisicao.raw().getParameter("telefoneCentralAtendimento");
    }

    @Override
    public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
        ItfUsuarioChat usuario = UtilAgenciaContatos.getContatoAtendimentoByTelefone(telefoneCentralAtendimento, telefoneLead);
        try {
            JsonObject json = UtilSBCoreJson.getJsonObjectBySequenciaChaveValor("email", usuario.getEmail());
            return UtilSBCoreJson.getTextoByJsonObjeect(json);
        } catch (ErroProcessandoJson ex) {
            Logger.getLogger(AtendimentoByNumeroLead.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ainda não implemtado";
    }

}
