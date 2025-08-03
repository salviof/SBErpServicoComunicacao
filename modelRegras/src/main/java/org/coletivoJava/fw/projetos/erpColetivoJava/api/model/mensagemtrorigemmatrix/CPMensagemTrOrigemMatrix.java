package org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtrorigemmatrix;

import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoReferenciaEntidade;

@InfoReferenciaEntidade(tipoObjeto = MensagemTrOrigemMatrix.class)
public enum CPMensagemTrOrigemMatrix {
	_JSONMENSAGEMORIGINAL, _ENCAMINHAMENTOS, _CODIGORECIBOMENSAGEMMATRIX;

	public static final String jsonmensagemoriginal = "jsonMensagemOriginal";
	public static final String encaminhamentos = "encaminhamentos";
	public static final String codigorecibomensagemmatrix = "codigoReciboMensagemMatrix";
}