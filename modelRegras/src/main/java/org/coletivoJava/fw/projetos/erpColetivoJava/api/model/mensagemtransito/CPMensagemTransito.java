package org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtransito;

import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTransito;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoReferenciaEntidade;

@InfoReferenciaEntidade(tipoObjeto = MensagemTransito.class)
public enum CPMensagemTransito {
	_ID, _NOME, _TIPOMENSAGEM, _TIPOORIGEM, _REGISTRADO, _ENCAMINHADO, _LIDO, _DATAHORACRIACAO, _DAHORAEXPIRAR;

	public static final String id = "id";
	public static final String nome = "nome";
	public static final String tipomensagem = "tipoMensagem";
	public static final String tipoorigem = "tipoOrigem";
	public static final String registrado = "registrado";
	public static final String encaminhado = "encaminhado";
	public static final String lido = "lido";
	public static final String datahoracriacao = "dataHoraCriacao";
	public static final String dahoraexpirar = "daHoraExpirar";
}