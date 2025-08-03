package org.coletivoJava.fw.projetos.erpColetivoJava.api.model.canalominiechannel;

import com.super_bits.casanovadigital.servicos.messagens.model.agente.canais.CanalOminieChannel;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoReferenciaEntidade;

@InfoReferenciaEntidade(tipoObjeto = CanalOminieChannel.class)
public enum CPCanalOminieChannel {
	_ID, _DESCRICAO;

	public static final String id = "id";
	public static final String descricao = "descricao";
}