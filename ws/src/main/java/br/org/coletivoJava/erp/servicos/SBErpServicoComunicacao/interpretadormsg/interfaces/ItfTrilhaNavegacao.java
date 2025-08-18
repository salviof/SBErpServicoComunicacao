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

    public Class<? extends ItfTrilhaNavegacao> getClasseDesvioDeTrilha(MensagemWhatsapp p) throws ErroComDevolucaoMensagemUsuario;

    public void AcaoTimeoutResposta(Contato pContato);

    public Class<? extends ItfTrilhaNavegacao> getClasseDesvioDeTrilha(EventoSalaMatrix p) throws ErroComDevolucaoMensagemUsuario;

    public RotaMensagemContato getRotaAtual();

    public void finalizarSesaso();

    public ContextoContato getContextoDeSessao();
}
