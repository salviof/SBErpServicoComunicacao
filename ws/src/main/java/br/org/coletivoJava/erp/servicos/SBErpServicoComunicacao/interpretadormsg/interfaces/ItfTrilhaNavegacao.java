package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaMensagemContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.fw.api.erp.chat.model.ComandoDeAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;

/**
 *
 * @author salvio
 */
public interface ItfTrilhaNavegacao {

    /**
     *
     * este método precisa definir a rota atual da tralha
     *
     * @param pContato
     * @return Nova Trilha
     */
    public String iniciarTrilha() throws ErroConexaoServicoChat, ErroComDevolucaoMensagemUsuario;

    /**
     * TODO RETORNAR A CLASSE E NO NOVO CAMINHO VINCULADO A ESSA NOVA TRILHA OU
     * RETORNA SÓ O NOVO CAMINHO E A RESPONSABILIDADE DE DEFINIR A CLASSE, FICA
     * EXCLUSIVA DO SERVIÇO DE NAVEGAÇÃO
     *
     * @param p
     * @return
     * @throws ErroComDevolucaoMensagemUsuario
     */
    public String getDesvioTrilhaPorMensgemWhatsapp(MensagemWhatsapp p) throws ErroComDevolucaoMensagemUsuario;

    public void acaoTimeoutAguardandoRespostaAtendimento();

    public void acaoTimeoutAguardandoInteracaoContato();

    public String getDesvioTrilhaPorEventoMatrix(ComandoDeAtendimento p) throws ErroComDevolucaoMensagemUsuario;

    public RotaMensagemContato getRotaAtual();

    public String getCaminhoTrilha();

    public void finalizarSesaso();

    public ContextoContato getContextoDeSessao();
}
