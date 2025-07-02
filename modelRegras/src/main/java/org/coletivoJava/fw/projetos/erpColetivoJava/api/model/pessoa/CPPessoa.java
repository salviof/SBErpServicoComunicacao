package org.coletivoJava.fw.projetos.erpColetivoJava.api.model.pessoa;

import com.super_bits.casanovadigital.servicos.messagens.model.agente.Pessoa;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoReferenciaEntidade;

@InfoReferenciaEntidade(tipoObjeto = Pessoa.class)
public enum CPPessoa {
	_ID, _NOME, _AVATARWATSAP, _WTZPID, _MATRIXID, _TIPOPESSOA, _DATAHORACRIACAO, _EMAIL, _TELEFONE;

	public static final String id = "id";
	public static final String nome = "nome";
	public static final String avatarwatsap = "avatarWatsap";
	public static final String wtzpid = "wtzpID";
	public static final String matrixid = "matrixID";
	public static final String tipopessoa = "tipoPessoa";
	public static final String datahoracriacao = "dataHoraCriacao";
	public static final String email = "email";
	public static final String telefone = "telefone";
}