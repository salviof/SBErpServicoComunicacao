/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaMensagemContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.eventos.EventoSalaMatrix;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
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
     */
    public void iniciarTrilha() throws ErroConexaoServicoChat, ErroComDevolucaoMensagemUsuario;

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

    public void AcaoTimeoutResposta(Contato pContato);

    public String getDesvioTrilhaPorEventoMatrix(EventoSalaMatrix p) throws ErroComDevolucaoMensagemUsuario;

    public RotaMensagemContato getRotaAtual();

    public String getCaminhoTrilha();

    public void finalizarSesaso();

    public ContextoContato getContextoDeSessao();
}
