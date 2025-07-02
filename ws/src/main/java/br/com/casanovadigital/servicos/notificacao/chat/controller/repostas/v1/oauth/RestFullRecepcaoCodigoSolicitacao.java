/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.repostas.v1.oauth;

import br.com.casanovadigital.servicos.notificacao.chat.controller.FabUsuariosERPWhatasppAgencia;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import com.google.common.collect.Lists;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJsonRest;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.implementacao.UtilSBApiRestClient;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.implementacao.erro.ErroRecebendoCodigoDeAcesso;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.ItfResposta;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import com.super_bits.modulosSB.SBCore.modulos.objetos.registro.Interfaces.basico.ItfUsuario;
import jakarta.json.JsonObjectBuilder;

/**
 *
 * @author salvio
 */
public class RestFullRecepcaoCodigoSolicitacao extends RotaSparkPadrao {

    private static final ItfUsuario usuarioPadraoAgencia = (ItfUsuario) FabUsuariosERPWhatasppAgencia.USUARIO_INTEGRACAO.getRegistro();

    public RestFullRecepcaoCodigoSolicitacao() {
        super();
    }

    @Override
    public void validarParamentros() throws ErroParamentosInvalidos {
        System.out.println("Recebendo token");
    }

    @Override
    public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
        System.out.println("Conexão endpoint teste contato " + requisicao.ip());
        System.out.println(requisicao.queryString());
        System.out.println(requisicao.pathInfo());
        System.out.println(requisicao.requestMethod());
        SBCore.getServicoSessao().getSessaoAtual().setUsuario(usuarioPadraoAgencia);
        try {
            UtilSBApiRestClient.servletReceberCodigoConcessao(requisicao.raw(), resposta.raw(), SBCore.getServicoSessao().getSessaoAtual());
            JsonObjectBuilder respostaJson = UtilSBCoreJsonRest.getRespostaJsonBuilderBase(true, ItfResposta.Resultado.SUCESSO, Lists.newArrayList(FabMensagens.AVISO.getMsgUsuario("Chave de Aceso armazenada com sucesso, você está conectado com a aplicação.")));
            return UtilSBCoreJson.getTextoByJsonObjeect(respostaJson.build());
        } catch (ErroRecebendoCodigoDeAcesso ex) {
            return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha(ex.getMessage()).build());
        }
    }

}
