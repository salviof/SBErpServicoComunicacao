/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.salaChat;

import br.com.casanovadigital.servicos.notificacao.chat.ServicoNotificacaoChat;
import br.com.casanovadigital.servicos.notificacao.contextoChat.matrix.ContextoMatrix;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreGravatar;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJsonRest;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringValidador;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;

/**
 *
 * @author salvio
 */
public class ApiSalaChatMembros extends RotaSparkPadrao {

    private String codigoSala;

    @Override
    public void validarParamentros() throws ErroParamentosInvalidos {
        if (requisicao.splat().length != 1) {
            throw new ErroParamentosInvalidos("Sala não encontrada");

        }
        codigoSala = requisicao.splat()[0];
    }

    @Override
    public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
        ItfChatSalaBean sala;
        try {
            sala = ContextoMatrix.getChatMatrixService().getSalaByCodigo(codigoSala);
        } catch (ErroConexaoServicoChat ex) {
            throw new ErroConexaoSistemaTerceiro("Erro de conexão obtendo dados da sala " + codigoSala + " " + ex.getMessage());
        }

        JsonArrayBuilder contatosSala = Json.createArrayBuilder();
        JsonObjectBuilder retorno = Json.createObjectBuilder();
        JsonObjectBuilder usuarioLider = Json.createObjectBuilder();
        JsonObjectBuilder usuarioLogin = Json.createObjectBuilder();
        for (ItfUsuarioChat usuario : sala.getUsuarios()) {

            if (!UtilSBCoreStringValidador.isNuloOuEmbranco(usuario.getEmail())) {

                JsonObjectBuilder contato = Json.createObjectBuilder();
                contato.add("codigoUsuario", usuario.getCodigoUsuario());
                contato.add("email", usuario.getEmail());
                contato.add("nome", usuario.getNome());
                contato.add("avatar", UtilSBCoreGravatar.getGravatarUrl(usuario.getEmail(), 80));
                contatosSala.add(contato);

                if (usuario.getEmail().contains("@casanovadigital.com.br")) {

                    usuarioLider.add("codigoUsuario", usuario.getCodigoUsuario());
                    usuarioLider.add("email", usuario.getEmail());
                    usuarioLider.add("nome", usuario.getNome());
                    usuarioLider.add("avatar", UtilSBCoreGravatar.getGravatarUrl(usuario.getEmail(), 80));

                } else {

                    usuarioLogin.add("codigoUsuario", usuario.getCodigoUsuario());
                    usuarioLogin.add("email", usuario.getEmail());
                    usuarioLogin.add("nome", usuario.getNome());
                    usuarioLogin.add("avatar", UtilSBCoreGravatar.getGravatarUrl(usuario.getEmail(), 80));
                }
            }

            //System.out.println(pUsuario.getEmail());
            // System.out.println(pUsuario.getEmailPrincipal());
        }
        retorno.add("membros", contatosSala);
        retorno.add("membroLider", usuarioLider);
        retorno.add("membroLogin", usuarioLogin);
        return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseSucesso(retorno.build()).build());
    }

    public String getCodigoSala() {
        return codigoSala;
    }

    public void setCodigoSala(String codigoSala) {
        this.codigoSala = codigoSala;
    }

}
