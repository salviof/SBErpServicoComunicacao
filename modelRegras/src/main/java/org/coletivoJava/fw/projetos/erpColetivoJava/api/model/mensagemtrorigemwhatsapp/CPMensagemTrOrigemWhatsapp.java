package org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtrorigemwhatsapp;

import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoReferenciaEntidade;

@InfoReferenciaEntidade(tipoObjeto = MensagemTrOrigemWhatsapp.class)
public enum CPMensagemTrOrigemWhatsapp {
	_CODIGORECIBOENTREGAWHATSAPP, _CODIGOENCAMINHAMENTOMATRIX;

	public static final String codigoreciboentregawhatsapp = "codigoReciboEntregaWhatsapp";
	public static final String codigoencaminhamentomatrix = "codigoEncaminhamentoMatrix";
}