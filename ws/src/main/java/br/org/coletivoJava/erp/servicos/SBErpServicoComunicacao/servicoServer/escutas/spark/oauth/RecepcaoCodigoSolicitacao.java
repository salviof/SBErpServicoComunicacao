/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.oauth;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config.FabConfigServicoComunicacao;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.RotaSparkPadrao;
import com.google.common.collect.Lists;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJsonRest;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.implementacao.UtilSBApiRestClient;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.implementacao.erro.ErroRecebendoCodigoDeAcesso;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.ItfResposta;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import com.super_bits.modulosSB.SBCore.modulos.objetos.registro.Interfaces.basico.ComoUsuario;
import jakarta.json.JsonObjectBuilder;
import javax.ws.rs.Path;
import spark.Request;
import spark.Response;

/**
 *
 * @author salvio
 */
@Path("/solicitacaoAuth2Recept/code/SISTEMA")
public class RecepcaoCodigoSolicitacao extends RotaSparkPadrao {

    private static ComoUsuario usuarioPadraoAgencia = null;

    public RecepcaoCodigoSolicitacao() {
        super();
    }

    @Override
    public void validarParamentros(Request pREquisicao) throws ErroParamentosInvalidos {
        System.out.println("Recebendo token");
        if (usuarioPadraoAgencia == null) {
            try {
                usuarioPadraoAgencia = (ComoUsuario) AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByEmail(FabConfigServicoComunicacao.USUARIO_ATENDIMENTO_PADRAO.getValorParametroSistema());
            } catch (ErroConexaoServicoChat ex) {
                throw new ErroParamentosInvalidos("Usuário de atendimento padrão: " + FabConfigServicoComunicacao.USUARIO_ATENDIMENTO_PADRAO.getValorParametroSistema() + " ainda não foi encontrado");
            }
        }
    }

    @Override
    public String executarRegraDeNegocio(Request pRequest, Response pResposta) throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
        System.out.println("Conexão endpoint teste contato " + pRequest.ip());
        System.out.println(pRequest.queryString());
        System.out.println(pRequest.pathInfo());
        System.out.println(pRequest.requestMethod());
        SBCore.getServicoSessao().getSessaoAtual().setUsuario(usuarioPadraoAgencia);
        try {
            UtilSBApiRestClient.servletReceberCodigoConcessao(pRequest.raw(), pResposta.raw(), SBCore.getServicoSessao().getSessaoAtual());
            JsonObjectBuilder respostaJson = UtilSBCoreJsonRest.getRespostaJsonBuilderBase(true, ItfResposta.Resultado.SUCESSO, Lists.newArrayList(FabMensagens.AVISO.getMsgUsuario("Chave de Aceso armazenada com sucesso, você está conectado com a aplicação.")));
            return UtilSBCoreJson.getTextoByJsonObjeect(respostaJson.build());
        } catch (ErroRecebendoCodigoDeAcesso ex) {
            return UtilSBCoreJson.getTextoByJsonObjeect(UtilSBCoreJsonRest.getRespostaJsonBuilderBaseFalha(ex.getMessage()).build());
        }
    }

    @Override
    public String executarRegraDeNegocio(String pCorpo) throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
        throw new ErroRegraDeNegocio("A tratativa de execucao da regra de negocio por conteúdo do body não faz sentido nesta situação");
    }

}
