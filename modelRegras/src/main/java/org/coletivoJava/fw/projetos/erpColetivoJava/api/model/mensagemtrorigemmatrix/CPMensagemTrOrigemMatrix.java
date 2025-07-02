package org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtrorigemmatrix;

import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoReferenciaEntidade;

@InfoReferenciaEntidade(tipoObjeto = MensagemTrOrigemMatrix.class)
public enum CPMensagemTrOrigemMatrix {
	_CODIGORECIBOENTREGAMATRIX, _CODIGOENCAMINHAMENTOWHATSAPP;

	public static final String codigoreciboentregamatrix = "codigoReciboEntregaMatrix";
	public static final String codigoencaminhamentowhatsapp = "codigoEncaminhamentoWhatsapp";
}