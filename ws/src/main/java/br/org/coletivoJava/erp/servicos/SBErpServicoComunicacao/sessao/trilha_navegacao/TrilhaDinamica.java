package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaEncaminhamentoSala;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComEncaminhamentoRotaRaiz;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.FabTipoGatilho;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.sessao.SessaoDeContato;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_ATENDIMENTO_CHAMADO;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_DEBATE_INTERNO_LEAD_CLIENTE;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.WTZAP_ATENDIMENTO_GRUPO_CLIENTE;
import com.google.common.collect.Lists;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;

/**
 *
 * @author salvio
 */
public class TrilhaDinamica extends TrilhaNavegacaoAbs {

    private DadosTrilhaDinamica dadosTrilhaDinamica;
    private ItfTrilhaNavegacao trilhaOrig;
    private ComoUsuarioChat usuarioAtendimento;

    public TrilhaDinamica(SessaoDeContato pSessaoDeContato, ItfTrilhaNavegacao pTrilhaOrigem, EntradaNumeroWhatsapp pEntrada, String pCaminhoTrilha) {
        super(pSessaoDeContato, pTrilhaOrigem, pEntrada, pCaminhoTrilha);
        trilhaOrig = pTrilhaOrigem;
    }

    public RotaEncaminhamentoSala gerarRotaEncamimnhamentoMensagem() throws ErroConexaoServicoChat, ErroComDevolucaoMensagemUsuario, ErroComEncaminhamentoRotaRaiz {
        ComoUsuarioChat usuarioAtendimento = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByEmail(dadosTrilhaDinamica.getAtendenteSelecionada());
        if (usuarioAtendimento == null) {
            throw new ErroComEncaminhamentoRotaRaiz("Falha localizando dados do atendimento");
        }
        ComoChatSalaBean sala = null;
        try {
            switch (dadosTrilhaDinamica.getTipoSala()) {
                case WTZAP_ATENDIMENTO:
                case WTZAP_VENDAS:

                case MATRIX_CHAT_VENDAS:
                case MATRIX_CHAT_ATENDIMENTO:
                    sala = gerarSala(getEntrada(), dadosTrilhaDinamica.getTipoSala(),
                            getContextoDeSessao().getContato(), usuarioAtendimento, true);
                    break;
                case WTZAP_ATENDIMENTO_GRUPO_CLIENTE:
                // ATENÇÂO WTZAP_ATENDIMENTO_GRUPO_CLIENTE não foi implementado
                case MATRIX_CHAT_DEBATE_INTERNO_LEAD_CLIENTE:
                case MATRIX_CHAT_ATENDIMENTO_CHAMADO:
                case CHAT_DINAMICO_DE_ENTIDADE:
                    if (dadosTrilhaDinamica.getEntidadeTrilhaDinamica() == null) {
                        throw new ErroComDevolucaoMensagemUsuario("O tipo de sela " + dadosTrilhaDinamica.getTipoSala() + " precisa de entidade vinculada", "Falha definindo rota");
                    }
                    sala = gerarSalaVinculadaEntidade(getEntrada(), dadosTrilhaDinamica.getTipoSala(), dadosTrilhaDinamica.getEntidadeTrilhaDinamica(), getContextoDeSessao().getContato(), usuarioAtendimento);
                    break;
                default:
                    throw new AssertionError();
            }
            if (sala == null) {
                throw new ErroComDevolucaoMensagemUsuario("impossível deteminar a rota", "Falha encontrando rota, escreva menu, para voltar para o início");
            }
        } catch (Throwable t) {
            throw new ErroComDevolucaoMensagemUsuario("impossível deteminar a rota" + t.getMessage(), "Falha encontrando rota, escreva menu, para voltar para o início");
        }
        RotaEncaminhamentoSala rota = new RotaEncaminhamentoSala(getContextoDeSessao().getContato(), sala,
                Lists.newArrayList(),
                AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getAtendente(usuarioAtendimento),
                Lists.newArrayList()
        );
        return rota;

    }

    @Override
    public void iniciarTrilha() throws ErroConexaoServicoChat, ErroComDevolucaoMensagemUsuario, ErroComEncaminhamentoRotaRaiz {
        dadosTrilhaDinamica = getSessao().getServicoNavegacao().getDadosTrilhaDinamica(getSessao(), trilhaOrig, getEntrada(), getCaminhoTrilha());
        if (dadosTrilhaDinamica == null) {
            throw new ErroComDevolucaoMensagemUsuario("Dados da trilha dinânmica não encontrada", "Falha pesquisando rota");
        }
        if (dadosTrilhaDinamica.getModeloMensagemBoasVindas() != null) {

        }
        usuarioAtendimento = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByEmail(dadosTrilhaDinamica.getAtendenteSelecionada());
        rotaAtual = gerarRotaEncamimnhamentoMensagem();
    }

    @Override
    public AcaoGatilhoTrilha getAcaoDeGatilhoInicioTrilha(ContextoContato pContexto) {
        if (dadosTrilhaDinamica.getModeloMensagemBoasVindas() != null) {
            return new AcaoGatilhoTrilha(FabAcaoGatilhosTrilha.MENSAGEM_CONTATO_WHATSAPP, this, pContexto)
                    .setMensagemParaContato(dadosTrilhaDinamica.getModeloMensagemBoasVindas());
        } else {
            return null;
        }

    }

    @Override
    protected AcaoGatilhoTrilha getAcaoTrilhaPorMensgemContato(String pMensagem, String pComando) {
        return super.acaoTimeoutAguardandoRespostaAtendimento(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public void acaoTimeoutAguardandoInteracaoContato() {
        super.acaoTimeoutAguardandoInteracaoContato(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public AcaoGatilhoTrilha acaoTimeoutAguardandoRespostaAtendimento() {
        return super.acaoTimeoutAguardandoRespostaAtendimento(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    protected AcaoGatilhoTrilha getAcaoTrilhaPorMensgemAtendimento(String pMensagem, String pComando) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
