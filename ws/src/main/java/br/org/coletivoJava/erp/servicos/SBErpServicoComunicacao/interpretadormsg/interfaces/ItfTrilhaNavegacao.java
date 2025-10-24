package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaMensagemContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComEncaminhamentoRotaRaiz;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.sessao.SessaoDeContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.AcaoGatilhoTrilha;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.TrilhaNavegacaoAbs;
import br.org.coletivoJava.fw.api.erp.chat.model.ComandoDeAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.eventos.EventoSalaMatrix;
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
    public void iniciarTrilha() throws ErroConexaoServicoChat, ErroComDevolucaoMensagemUsuario, ErroComEncaminhamentoRotaRaiz;

    public AcaoGatilhoTrilha getAcaoDeGatilhoPorMensagemWtzp(MensagemWhatsapp p) throws ErroComDevolucaoMensagemUsuario;

    public AcaoGatilhoTrilha getAcaoDeGatilhoPorComandoAtendimento(ComandoDeAtendimento p) throws ErroComDevolucaoMensagemUsuario;

    public AcaoGatilhoTrilha getAcaoDeGatilhoPorEventoMatrix(ItfEventoMatix pEvento) throws ErroComDevolucaoMensagemUsuario;

    public AcaoGatilhoTrilha getAcaoDeGatilhoInicioTrilha(ContextoContato pContexto);

    public void registrarInteracao(TrilhaNavegacaoAbs.TIPO_INTERACAO tipoInteracao);

    public AcaoGatilhoTrilha acaoTimeoutAguardandoRespostaAtendimento();

    public void atualizarContextoSessao();

    public void atualizarUltimaSalaConversaDeSessao(String pSala);

    public void acaoTimeoutAguardandoInteracaoContato();

    public RotaMensagemContato getRotaAtual();

    public String getCaminhoTrilha();

    public void finalizarSesaso();

    public ContextoContato getContextoDeSessao();

    public SessaoDeContato getSessao();

}
