package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces;

import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.EncaminhamentoMatrixParaWtzp;

/**
 *
 * @author salvio
 */
public interface ItfProcessadorEventoWhatsapp extends ItfProcessadorPacoteMatrixWhatsap {

    public EncaminhamentoMatrixParaWtzp getMensagemRelacionada();
}
