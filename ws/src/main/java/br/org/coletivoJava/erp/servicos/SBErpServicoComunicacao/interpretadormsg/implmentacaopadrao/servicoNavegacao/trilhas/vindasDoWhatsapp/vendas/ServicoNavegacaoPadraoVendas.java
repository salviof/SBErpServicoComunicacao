package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.implmentacaopadrao.servicoNavegacao.trilhas.vindasDoWhatsapp.vendas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.UtilAplicacaoWsChatMatrixSalas;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.ServicoNavegacaoAbs;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import com.google.common.collect.Lists;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import jakarta.json.JsonObject;
import java.util.List;

/**
 *
 * @author salvio
 */
public class ServicoNavegacaoPadraoVendas extends ServicoNavegacaoAbs implements ItfServicoNavegacao {

    public ServicoNavegacaoPadraoVendas(EntradaNumeroWhatsapp pEntrada) {
        super(pEntrada, TrilhaVendasPadrao.class, new Class[]{TrilhaVendasPadrao.class});
    }

    public Class<? extends ItfTrilhaNavegacao> getClasseTrilhaDeNavegacao(Contato pContato, String pCaminho) {
        if (pCaminho == null) {
            return TrilhaVendasPadrao.class;
        }
        switch (pCaminho) {
            case "REUNIAO_AGENDADA":

                break;
            default:
                throw new AssertionError();
        }
        return TrilhaVendasPadrao.class;
    }

    @Override
    public String getCaminhoTrilhaRaiz() {
        return "menu";
    }

    @Override
    public List<String> getPalavrasParaCaminhoTrilhaRaiz() {
        return Lists.newArrayList("menu");
    }

    @Override
    public JsonObject gerarJsonDadosDeSessao(Contato pContato) {
        return null;
    }

    @Override
    public ItfChatSalaBean gerarSalaAtendimentoPadrao(EntradaNumeroWhatsapp pEntrada, Contato pContato) throws ErroConexaoServicoChat {

        return UtilAplicacaoWsChatMatrixSalas.gerarSala(pEntrada, FabTipoSalaMatrix.MATRIX_CHAT_ATENDIMENTO, pContato, AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(pEntrada, pContato));

    }

}
