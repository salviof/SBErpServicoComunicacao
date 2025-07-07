package org.coletivoJava.fw.projetos.erpColetivoJava.api.cucumber.fluxomensagemorigemwhatsapp;
public enum EtapasFluxoMensagemOrigemWhatsapp {
	_DADO_QUE_O_USUARIO_CONTATO_ESTA_CONECTADO_NO_WHATSAPP, _E_O_USUARIO_ATENDIMENTO_ESTA_CONECTADO_NO_MATRIX, _QUANDO_O_USUARIO_CONTATO_ENVIA_A_MENSAGEM_OLA_TUDO_BEM_PELO_WHATSAPP_PARA_ATENDIMENTO, _ENTAO_A_MENSAGEM_OLA_TUDO_BEM_E_ENCAMINHADA_PARA_O_USUARIO_ATENDIMENTO_NO_MATRIX, _QUANDO_O_USUARIO_ATENDIMENTO_LE_A_MENSAGEM_OLA_TUDO_BEM_NO_MATRIX, _ENTAO_O_USUARIO_CONTATO_E_NOTIFICADO_DA_LEITURA_DA_MENSAGEM_NO_WHATSAPP;

	public static final String DADO_QUE_O_USUARIO_CONTATO_ESTA_CONECTADO_NO_WHATSAPP = "que o usuário Contato está conectado no WhatsApp";
	public static final String E_O_USUARIO_ATENDIMENTO_ESTA_CONECTADO_NO_MATRIX = "o usuário Atendimento está conectado no Matrix";
	public static final String QUANDO_O_USUARIO_CONTATO_ENVIA_A_MENSAGEM_OLA_TUDO_BEM_PELO_WHATSAPP_PARA_ATENDIMENTO = "o usuário Contato envia a mensagem Olá, tudo bem pelo WhatsApp para Atendimento";
	public static final String ENTAO_A_MENSAGEM_OLA_TUDO_BEM_E_ENCAMINHADA_PARA_O_USUARIO_ATENDIMENTO_NO_MATRIX = "a mensagem Olá, tudo bem é encaminhada para o usuário Atendimento no Matrix";
	public static final String QUANDO_O_USUARIO_ATENDIMENTO_LE_A_MENSAGEM_OLA_TUDO_BEM_NO_MATRIX = "o usuário Atendimento lê a mensagem Olá, tudo bem no Matrix";
	public static final String ENTAO_O_USUARIO_CONTATO_E_NOTIFICADO_DA_LEITURA_DA_MENSAGEM_NO_WHATSAPP = "o usuário Contato é notificado da leitura da mensagem no WhatsApp";
}