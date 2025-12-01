package org.coletivoJava.fw.projetos.erpColetivoJava.api.model.contextocontato;

import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoReferenciaEntidade;

@InfoReferenciaEntidade(tipoObjeto = ContextoContato.class)
public enum CPContextoContato {
	_ID, _CONTATO, _NOMECONTEXTO, _CANAL, _CODIGOENTRADA, _TRILHAATUAL, _DATAHORAINTERACAOCONTATO, _DATAHORAINTERACAOATENDIMENTO, _DATAHORAINICIOSESSAO, _DATAHORAFINALSESSAO, _JSONDADOSDOCONTEXTO, _SALAULTIMACONVERSA;

	public static final String id = "id";
	public static final String contato = "contato";
	public static final String nomecontexto = "nomeContexto";
	public static final String canal = "canal";
	public static final String codigoentrada = "codigoEntrada";
	public static final String trilhaatual = "trilhaAtual";
	public static final String datahorainteracaocontato = "dataHoraInteracaoContato";
	public static final String datahorainteracaoatendimento = "dataHoraInteracaoAtendimento";
	public static final String datahorainiciosessao = "dataHoraInicioSessao";
	public static final String datahorafinalsessao = "dataHoraFinalSessao";
	public static final String jsondadosdocontexto = "jsonDadosDoContexto";
	public static final String salaultimaconversa = "salaUltimaConversa";
}