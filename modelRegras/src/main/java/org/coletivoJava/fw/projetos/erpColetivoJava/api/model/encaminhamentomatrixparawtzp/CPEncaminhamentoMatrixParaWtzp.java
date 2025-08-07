package org.coletivoJava.fw.projetos.erpColetivoJava.api.model.encaminhamentomatrixparawtzp;

import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.EncaminhamentoMatrixParaWtzp;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoReferenciaEntidade;

@InfoReferenciaEntidade(tipoObjeto = EncaminhamentoMatrixParaWtzp.class)
public enum CPEncaminhamentoMatrixParaWtzp {
	_ID, _NOME, _RECIBOREGISTROOWTZP, _FOIENVIADOPELOWHATSAPP, _FOIENTREGUENOCELULARDOCONTATO, _FOILIDOPELOCONATOWTZP, _CONTATO, _MENSAGEM;

	public static final String id = "id";
	public static final String nome = "nome";
	public static final String reciboregistroowtzp = "reciboRegistrooWtzp";
	public static final String foienviadopelowhatsapp = "foiEnviadoPeloWhatsapp";
	public static final String foientreguenocelulardocontato = "foiEntregueNoCelularDoContato";
	public static final String foilidopeloconatowtzp = "foiLidoPeloConatoWtzp";
	public static final String contato = "contato";
	public static final String mensagem = "mensagem";
}