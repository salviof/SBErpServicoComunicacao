/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfCentralLogicasProcessamentoMsg;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.logdeMensagens.RepositorioComunicacaoChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoClient.ServicoWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos.ListenerSalaMatrix;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.GestaoDeServicosNavegacao;
import br.org.coletivoJava.fw.api.erp.chat.ERPChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.ChatMatrixOrgimpl;
import br.org.coletivoJava.integracoes.whatsapp.config.FabConfigApiWhatsapp;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.json.ErroProcessandoJson;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 *
 * @author salvio
 */
public class AplicacaoWsChat {

    private static List<EntradaNumeroWhatsapp> entradas = new ArrayList<>();
    public static final GestaoDeServicosNavegacao GESTAO_SERVICO_NAVEGACAO = new GestaoDeServicosNavegacao();
    private static ItfCentralLogicasProcessamentoMsg centralLogicasDeProcessamento;
    public static final ChatMatrixOrgimpl SERVICO_MATRIX = (ChatMatrixOrgimpl) ERPChat.MATRIX_ORG.getImplementacaoDoContexto();
    public static final ServicoWhatsapp SERVICO_WHATSAPP = new ServicoWhatsapp();
    public static final RepositorioComunicacaoChat REPOSITORIO_COMUNICACAO_CHAT = new RepositorioComunicacaoChat();
    private static boolean aplicacaoIniciad = false;

    public static final ItfCentralLogicasProcessamentoMsg getCentralLogicaProcesasmento() throws ErroConexaoServicoChat {
        if (centralLogicasDeProcessamento == null) {
            ServiceLoader<ItfCentralLogicasProcessamentoMsg> services
                    = ServiceLoader.load(ItfCentralLogicasProcessamentoMsg.class);
            centralLogicasDeProcessamento = services.iterator().next();

        }
        return centralLogicasDeProcessamento;
    }

    public static synchronized void iniciarAplicacao() {
        try {
            if (aplicacaoIniciad) {
                return;
            }
            getCentralLogicaProcesasmento();
            loadEntradas();

            //TODO VALIDAR AUTENTICAÇÃO DE SISTEMAS, ANTES DE INICIAR
            centralLogicasDeProcessamento.getSistemas();

            //TODO VALIDAR CODIGOS DE ENTRADA EXECUTANDO CHAMADA DE WHATSAPP ANTES DE INICIAR O SISTEMA
            for (EntradaNumeroWhatsapp entrada : entradas) {

            }
            //TODO VALIDAR AUTENTICAÇÃO DO SERVIÇO MATRIX ANTES DE INICIAR
            AplicacaoWsChat.SERVICO_MATRIX.registrarClasseDeEscutaSalas(ListenerSalaMatrix.class);
            //      AplicacaoWsChat.SERVICO_MATRIX.registrarClasseEscutaNotificacoes(ListenerNotificacaoMatrixAuxiliadora.class);

            aplicacaoIniciad = true;
        } catch (Throwable t) {
            System.out.println("FALHA CONECTANDO COM SERVIÇO DE CHAT, TENTANDO NOVAMENTE EM 10 SEGUNDOS");
            try {
                Thread.sleep(10000);
                iniciarAplicacao();
            } catch (InterruptedException ex) {

            }
        }
    }

    public static EntradaNumeroWhatsapp getEntradaByCodigoEntrada(String pCodigo) throws ErroRegraDeNegocio {
        if (entradas.isEmpty()) {
            loadEntradas();
        }

        Optional<EntradaNumeroWhatsapp> pesquisaEntrada = entradas.stream().filter(et -> et.getCodigo().equals(pCodigo)).findFirst();
        if (pesquisaEntrada.isPresent()) {
            return pesquisaEntrada.get();
        }
        throw new ErroRegraDeNegocio("Falha encontrando telefone relacionado a integração, código" + pCodigo);
    }

    private synchronized static void loadEntradas() {
        System.out.println(SBCore.getConfigModulo(FabConfigApiWhatsapp.class).getRepositorioDeArquivosExternos().getCaminhoArquivosRepositorio());
        if (SBCore.getConfigModulo(FabConfigApiWhatsapp.class).getRepositorioDeArquivosExternos().getJsonObjeto("entradas.json").isEmpty()) {
            try {
                JsonObjectBuilder exemplo = UtilSBCoreJson.getJsonBuilderBySequenciaChaveValor("nomeAplicacao", SBCore.getNomeProjeto());
                JsonArrayBuilder entradasJson = Json.createArrayBuilder();
                entradasJson.add(UtilSBCoreJson.getJsonObjectBySequenciaChaveValor("codigo", FabConfigApiWhatsapp.CODIGO_USUARIO.getValorParametroSistema(), "nome", "Vendas Casanova", "telefonewa_id", "553121159755", "telefoneDivulgacao", "(31) 2115-9755"));
                exemplo.add("entradas", entradasJson.build());
                SBCore.getConfigModulo(FabConfigApiWhatsapp.class).getRepositorioDeArquivosExternos().putConteudoRecursoExterno("entradas.json", UtilSBCoreJson.getTextoByJsonObjeect(exemplo.build()));
            } catch (ErroProcessandoJson ex) {
                throw new UnsupportedOperationException("Falha criando entradas");
            }

        } else {
            JsonObject entradasJson = SBCore.getConfigModulo(FabConfigApiWhatsapp.class).getRepositorioDeArquivosExternos().getJsonObjeto("entradas.json");
            JsonArray entradasJsons = entradasJson.getJsonArray("entradas");
            for (JsonValue valor : entradasJsons) {
                JsonObject entradaJson = valor.asJsonObject();
                EntradaNumeroWhatsapp entrada = new EntradaNumeroWhatsapp();
                entrada.setCodigo(entradaJson.getString("codigo"));
                entrada.setNome(entradaJson.getString("nome"));
                entrada.setTelefonewa_id(entradaJson.getString("telefonewa_id"));
                entrada.setTelefoneDivulgacao(entradaJson.getString("telefoneDivulgacao"));
                entradas.add(entrada);
            }
        }
    }

}
