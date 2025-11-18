package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.implmentacaopadrao.servicoNavegacao.trilhas.vindasDoWhatsapp.vendas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaEncaminhamentoSala;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.sessao.SessaoDeContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.AcaoGatilhoTrilha;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.TrilhaNavegacaoAbs;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import com.google.common.collect.Lists;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author salvio
 */
public class TrilhaVendasPadrao extends TrilhaNavegacaoAbs {

    public TrilhaVendasPadrao(SessaoDeContato pSessaoAtendimento, ItfTrilhaNavegacao pTrilhaOrigem, EntradaNumeroWhatsapp pEntrada, String pCaminhoTrilha) {
        super(pSessaoAtendimento, pTrilhaOrigem, pEntrada, pCaminhoTrilha);
    }

    @Override
    public void iniciarTrilha() throws ErroConexaoServicoChat {

        ComoUsuarioChat usuarioAtendimento = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(getEntrada(), getContextoDeSessao().getContato());
        try {

            AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(getEntrada(), getContextoDeSessao().getContato());
            ComoChatSalaBean sala = gerarSala(FabTipoSalaMatrix.WTZAP_VENDAS,
                    usuarioAtendimento);
            RotaEncaminhamentoSala rota = new RotaEncaminhamentoSala(getContextoDeSessao().getContato(), sala,
                    Lists.newArrayList(),
                    AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getAtendente(usuarioAtendimento),
                    Lists.newArrayList());
            rotaAtual = rota;
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(TrilhaVendasPadrao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public AcaoGatilhoTrilha getAcaoDeGatilhoInicioTrilha(ContextoContato pContexto) {
        return null;
    }

    @Override
    public AcaoGatilhoTrilha getAcaoTrilhaPorMensgemContato(String pMensagem, String pComando) {
        return null;
    }

    @Override
    public AcaoGatilhoTrilha getAcaoTrilhaPorMensgemAtendimento(String pMensagem, String pComando) {
        return null;
    }

}
