package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.implmentacaopadrao.servicoNavegacao.trilhas.vindasDoWhatsapp.vendas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.ServicoNavegacaoAbs;
import com.google.common.collect.Lists;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import java.util.List;

/**
 *
 * @author salvio
 */
public class ServicoNavegacaoPadraoVendas extends ServicoNavegacaoAbs implements ItfServicoNavegacao {

    public ServicoNavegacaoPadraoVendas(EntradaNumeroWhatsapp pEntrada) {
        super(pEntrada);
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

}
