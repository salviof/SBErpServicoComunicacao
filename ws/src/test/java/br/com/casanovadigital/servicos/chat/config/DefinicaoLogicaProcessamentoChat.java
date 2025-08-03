/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.config;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.modulosSB.SBCore.modulos.erp.ItfSistemaERP;
import java.util.ArrayList;
import java.util.List;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfCentralLogicasProcessamentoMsg;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.implmentacaopadrao.servicoNavegacao.trilhas.vindasDoWhatsapp.vendas.ServicoNavegacaoPadraoVendas;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.WTZAP_ATENDIMENTO;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.WTZAP_ATENDIMENTO_GRUPO_CLIENTE;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.WTZAP_VENDAS;
import br.org.coletivoJava.integracoes.whatsapp.config.FabConfigApiWhatsapp;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;

/**
 *
 * @author salvio
 */
public class DefinicaoLogicaProcessamentoChat implements ItfCentralLogicasProcessamentoMsg {

    public static final String CODIGO_ENTRADA_EXEMPLO_VENDAS = FabConfigApiWhatsapp.CODIGO_USUARIO.getValorParametroSistema();
    public static final String CODIGO_ENTRADA_EXEMPLO_ATENDIMENTO = "SEMREGISTRO";

    @Override
    public ItfUsuarioChat getUsuarioAtendimentoPadrao(EntradaNumeroWhatsapp pEntrada, Contato pContato) {
        String caminhoArquivo = SBCore.getConfigModulo(FabConfigServicoComunicacao.class).getPropriedade(FabConfigServicoComunicacao.USUARIO_ATENDIMENTO_PADRAO);

        String email = FabConfigServicoComunicacao.USUARIO_ATENDIMENTO_PADRAO.getValorParametroSistema();
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
    public EntradaNumeroWhatsapp getEntradaBySala(ItfChatSalaBean pSala) throws ErroRegraDeNegocio {

        FabTipoSalaMatrix tipoSala = FabTipoSalaMatrix.getTipoByAlias(pSala.getApelido());
        switch (tipoSala) {

            case WTZAP_ATENDIMENTO:
                return AplicacaoWsChat.getEntradaByCodigoEntrada(CODIGO_ENTRADA_EXEMPLO_ATENDIMENTO);
            case WTZAP_VENDAS:
                return AplicacaoWsChat.getEntradaByCodigoEntrada(CODIGO_ENTRADA_EXEMPLO_VENDAS);

            case WTZAP_ATENDIMENTO_GRUPO_CLIENTE:
                return AplicacaoWsChat.getEntradaByCodigoEntrada(CODIGO_ENTRADA_EXEMPLO_VENDAS);

            default:
                System.out.println("SLUG NÃO ENCONTRADO PARA NOME DA SALA " + pSala.getApelido());
                return null;
        }

        //return AplicacaoWsChat.getEntradaByCodigoEntrada("114354588403482");
    }

}
