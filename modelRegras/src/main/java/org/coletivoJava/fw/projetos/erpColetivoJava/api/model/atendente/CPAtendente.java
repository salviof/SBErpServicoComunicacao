package org.coletivoJava.fw.projetos.erpColetivoJava.api.model.atendente;

import com.super_bits.casanovadigital.servicos.messagens.model.agente.Atendente;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoReferenciaEntidade;

@InfoReferenciaEntidade(tipoObjeto = Atendente.class)
public enum CPAtendente {
	_EMAIL;

	public static final String email = "email";
}