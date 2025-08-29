package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.implmentacaopadrao.servicoNavegacao.trilhas.vindasDoWhatsapp.vendas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaEncaminhamentoSala;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.fw.api.erp.chat.model.ComandoDeAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.TrilhaNavegacaoAbs;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.eventos.EventoSalaMatrix;
import com.google.common.collect.Lists;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author salvio
 */
public class TrilhaVendasPadrao extends TrilhaNavegacaoAbs {

    public TrilhaVendasPadrao(ContextoContato pSessaoAtendimento, ItfTrilhaNavegacao pTrilhaOrigem, EntradaNumeroWhatsapp pEntrada, String pCaminhoTrilha) {
        super(pSessaoAtendimento, pTrilhaOrigem, pEntrada, pCaminhoTrilha);
    }

    @Override
    public String iniciarTrilha() throws ErroConexaoServicoChat {

        ItfUsuarioChat usuarioAtendimento = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(getEntrada(), getContextoDeSessao().getContato());
        try {

            AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(getEntrada(), getContextoDeSessao().getContato());
            ItfChatSalaBean sala = gerarSala(getEntrada(), FabTipoSalaMatrix.WTZAP_VENDAS,
                    getContextoDeSessao().getContato(), usuarioAtendimento);
            RotaEncaminhamentoSala rota = new RotaEncaminhamentoSala(getContextoDeSessao().getContato(), sala,
                    Lists.newArrayList(),
                    AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getAtendente(usuarioAtendimento),
                    Lists.newArrayList());
            rotaAtual = rota;
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(TrilhaVendasPadrao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getDesvioTrilhaPorMensgemWhatsapp(MensagemWhatsapp p) {
        return null;
    }

    @Override
    public void acaoTimeoutAguardandoRespostaAtendimento() {

    }

    @Override
    public void acaoTimeoutAguardandoInteracaoContato() {

    }

    @Override
    public String getDesvioTrilhaPorEventoMatrix(ComandoDeAtendimento p) throws ErroComDevolucaoMensagemUsuario {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
