package org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtrorigemwhatsapp;

import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoReferenciaEntidade;

@InfoReferenciaEntidade(tipoObjeto = MensagemTrOrigemWhatsapp.class)
public enum CPMensagemTrOrigemWhatsapp {
	_CODIGOREGISTROMENSAGEMWHATSAPP, _CODIGOENCAMINHAMENTOMATRIX, _CORPOJSONRECEBIDO, _DATAHORARESPOSTADOATENDIMENTO;

	public static final String codigoregistromensagemwhatsapp = "codigoRegistroMensagemWhatsapp";
	public static final String codigoencaminhamentomatrix = "codigoEncaminhamentoMatrix";
	public static final String corpojsonrecebido = "corpoJsonRecebido";
	public static final String datahorarespostadoatendimento = "dataHoraRespostaDoAtendimento";
}