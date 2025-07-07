package org.coletivoJava.fw.projetos.erpColetivoJava.api.model.contato;

import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoReferenciaEntidade;

@InfoReferenciaEntidade(tipoObjeto = Contato.class)
public enum CPContato {
	_WAID;

	public static final String waid = "waid";
}