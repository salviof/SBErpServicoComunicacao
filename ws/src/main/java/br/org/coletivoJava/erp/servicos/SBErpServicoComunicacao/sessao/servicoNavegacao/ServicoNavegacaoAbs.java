package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;

/**
 *
 * @author salvio
 */
public abstract class ServicoNavegacaoAbs implements ItfServicoNavegacao {

    private EntradaNumeroWhatsapp entradaWhatsapp;

    public ServicoNavegacaoAbs(EntradaNumeroWhatsapp pEntrada) {
        entradaWhatsapp = pEntrada;
    }

    public EntradaNumeroWhatsapp getEntradaWhatsapp() {
        return entradaWhatsapp;
    }

}
