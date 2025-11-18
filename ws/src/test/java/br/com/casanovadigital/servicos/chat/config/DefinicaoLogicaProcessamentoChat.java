/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.config;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoUsuarioChat;
import com.super_bits.modulosSB.SBCore.modulos.erp.ItfSistemaERP;
import java.util.ArrayList;
import java.util.List;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfCentralLogicasProcessamentoMsg;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.implmentacaopadrao.servicoNavegacao.trilhas.vindasDoWhatsapp.vendas.ServicoNavegacaoPadraoVendas;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.WTZAP_ATENDIMENTO;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.WTZAP_ATENDIMENTO_GRUPO_CLIENTE;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.WTZAP_VENDAS;
import br.org.coletivoJava.integracoes.whatsapp.config.FabConfigApiWhatsapp;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreNumeros;
import com.super_bits.modulosSB.SBCore.UtilGeral.json.ErroProcessandoJson;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.net.URL;
import java.util.Random;

/**
 *
 * @author salvio
 */
public class DefinicaoLogicaProcessamentoChat implements ItfCentralLogicasProcessamentoMsg {

    public static final String CODIGO_ENTRADA_EXEMPLO_VENDAS = FabConfigApiWhatsapp.CODIGO_USUARIO.getValorParametroSistema();
    public static final String CODIGO_ENTRADA_EXEMPLO_ATENDIMENTO = "SEMREGISTRO";

    @Override
    public ComoUsuarioChat getUsuarioAtendimentoPadrao(EntradaNumeroWhatsapp pEntrada, Contato pContato) {
        String caminhoArquivo = SBCore.getConfigModulo(FabConfigServicoComunicacao.class).getPropriedade(FabConfigServicoComunicacao.USUARIO_ATENDIMENTO_PADRAO);
        String email = FabConfigServicoComunicacao.USUARIO_ATENDIMENTO_PADRAO.getValorParametroSistema();
        if (!new Random().nextBoolean()) {
            email = "camila@casanovadigital.com.br";
        }
        try {
            return AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByEmail(email);
        } catch (ErroConexaoServicoChat ex) {
            throw new UnsupportedOperationException("Impossível obter os dados do usuário matrix verifique a variavel de ambiente " + FabConfigServicoComunicacao.USUARIO_ATENDIMENTO_PADRAO.toString() + "" + caminhoArquivo + " ");

        }
    }

    public DefinicaoLogicaProcessamentoChat() {
        System.out.println("up");

    }

    @Override
    public List<ItfSistemaERP> getSistemas() {
        List<ItfSistemaERP> sistemas = new ArrayList<>();
        //for (FabSistemasErp sistema : FabSistemasErp.values()) {
        //    sistemas.add(sistema.getRegistro());
        //}
        return sistemas;
    }

    @Override
    public Class<? extends ItfServicoNavegacao> getClasseServicoNavegacao(EntradaNumeroWhatsapp pEntrada) {
        if (pEntrada.getCodigo().equals("103007756220088")) {
            return ServicoNavegacaoPadraoVendas.class;
        } else {
            return ServicoNavegacaoPadraoVendas.class;
        }
    }

    @Override
    public EntradaNumeroWhatsapp getEntradaBySala(String pApelidoSala) throws ErroRegraDeNegocio {

        FabTipoSalaMatrix tipoSala = FabTipoSalaMatrix.getTipoByAlias(pApelidoSala);
        switch (tipoSala) {

            case WTZAP_ATENDIMENTO:
                return AplicacaoWsChat.getEntradaByCodigoEntrada(CODIGO_ENTRADA_EXEMPLO_ATENDIMENTO);
            case WTZAP_VENDAS:
                return AplicacaoWsChat.getEntradaByCodigoEntrada(CODIGO_ENTRADA_EXEMPLO_VENDAS);

            case WTZAP_ATENDIMENTO_GRUPO_CLIENTE:
                return AplicacaoWsChat.getEntradaByCodigoEntrada(CODIGO_ENTRADA_EXEMPLO_VENDAS);

            default:
                System.out.println("SLUG NÃO ENCONTRADO PARA NOME DA SALA " + pApelidoSala);
                return null;
        }

        //return AplicacaoWsChat.getEntradaByCodigoEntrada("114354588403482");
    }

    @Override
    public boolean isSalaAutomonitoravel(String pNomeSala) {
        FabTipoSalaMatrix tipoSala = FabTipoSalaMatrix.getTipoByAlias(pNomeSala);
        if (tipoSala == null) {
            return false;
        }
        switch (tipoSala) {
            case WTZAP_ATENDIMENTO:

            case WTZAP_VENDAS:
                return true;

            case WTZAP_ATENDIMENTO_GRUPO_CLIENTE:
                return false;

            default:
                System.out.println("SLUG NÃO ENCONTRADO PARA NOME DA SALA " + pNomeSala);
                return false;
        }
    }

    @Override
    public URL gerarRedirecionamentoServicoWtzp(EntradaNumeroWhatsapp pEntrada, MensagemWhatsapp pMensagem) {
        return null;
    }

    @Override
    public List<EntradaNumeroWhatsapp> gerarEntradas() {
        List<EntradaNumeroWhatsapp> entradas = new ArrayList<>();
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
        return entradas;
    }

    @Override
    public void inicializacaoServicosTerceiros() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
