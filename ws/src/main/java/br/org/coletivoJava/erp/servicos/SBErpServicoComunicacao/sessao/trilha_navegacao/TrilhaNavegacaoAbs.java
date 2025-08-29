package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaMensagemContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos.ListenerSalaMatrix;

import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.UtilMatrixERP;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.CHAT_DINAMICO_DE_ENTIDADE;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_ATENDIMENTO;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_ATENDIMENTO_CHAMADO;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_DEBATE_INTERNO_LEAD_CLIENTE;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_VENDAS;
import com.google.common.collect.Lists;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreDataHora;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringFiltros;
import com.super_bits.modulosSB.SBCore.modulos.objetos.registro.Interfaces.basico.ItfBeanSimples;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivojava.fw.api.tratamentoErros.ErroPreparandoObjeto;

/**
 *
 * @author salvio
 */
public abstract class TrilhaNavegacaoAbs implements ItfTrilhaNavegacao {

    private Long id;

    private ItfTrilhaNavegacao trilhaOrigem;
    private String nome;
    private List<ItfTrilhaNavegacao> trilhasDisponiveis;
    private EntradaNumeroWhatsapp entrada;
    private String caminhoTrilha;
    protected RotaMensagemContato rotaAtual;
    private ContextoContato contextoDeSessao;
    private Date ultimaInteracaoContato;
    private Date ultimaInteracaoAtendimento;
    private long segundosTimeoutAguardandoContato = 79200;
    private long segundosTimeoutAguardandoAtendimento = 900;
    //segundosTimeoutAguardandoAtendimento:600000
    private boolean agenteUltimaInteracaoContato;
    private final Monitor monitor;

    public void setSegundosTimeoutAguardandoContato(long segundosTimeoutAguardandoContato) {
        this.segundosTimeoutAguardandoContato = segundosTimeoutAguardandoContato;
    }

    public void setSegundosTimeoutAguardandoAtendimento(long segundosTimeoutAguardandoAtendimento) {
        this.segundosTimeoutAguardandoAtendimento = segundosTimeoutAguardandoAtendimento;
    }

    public TrilhaNavegacaoAbs(ContextoContato pContato, ItfTrilhaNavegacao pTrilhaOrigem, EntradaNumeroWhatsapp pEntrada, String pCaminhoTrilha) {
        entrada = pEntrada;
        trilhaOrigem = pTrilhaOrigem;
        caminhoTrilha = pCaminhoTrilha;
        contextoDeSessao = pContato;
        if (pCaminhoTrilha == null) {
            this.getClass().getSimpleName();
        }
        ultimaInteracaoContato = new Date();
        monitor = new Monitor();
        monitor.start();
    }

    public class Monitor extends Thread {

        private final long segundosMonitorTimeoutAguardandoAtendimento;
        private final long segundosMonitorTimeoutAguardandoContato;

        public Monitor() {
            this(segundosTimeoutAguardandoAtendimento, segundosTimeoutAguardandoContato);
        }

        public Monitor(long pSegundosTimeoutAguardandoAtendimento, long pSegundosTimeoutAguardandoContato) {
            segundosMonitorTimeoutAguardandoAtendimento = pSegundosTimeoutAguardandoAtendimento;
            segundosMonitorTimeoutAguardandoContato = pSegundosTimeoutAguardandoContato;
        }

        @Override
        public void run() {

            while (true && AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.isTrilhaExiste(entrada, getContextoDeSessao().getContato())) {

                if (agenteUltimaInteracaoContato) {
                    //Aguardando interacao do Atendimento
                    long tempoPassouInteracaoContato = UtilSBCoreDataHora.intervaloTempoSegundos(ultimaInteracaoContato, new Date());
                    if (tempoPassouInteracaoContato >= segundosTimeoutAguardandoAtendimento) {
                        acaoTimeoutAguardandoRespostaAtendimento();
                    }
                } else {
                    //Aguardando interação do Contato
                    long tempoPassouInteracaoAtendimento = UtilSBCoreDataHora.intervaloTempoSegundos(ultimaInteracaoAtendimento, new Date());
                    if (tempoPassouInteracaoAtendimento >= segundosTimeoutAguardandoContato) {
                        acaoTimeoutAguardandoInteracaoContato();
                    }
                }

            }
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ItfTrilhaNavegacao getTrilhaOrigem() {
        return trilhaOrigem;
    }

    public List<ItfTrilhaNavegacao> getTrilhasDisponiveis() {
        return trilhasDisponiveis;
    }

    public EntradaNumeroWhatsapp getEntrada() {
        return entrada;
    }

    @Override
    public String getCaminhoTrilha() {
        return caminhoTrilha;
    }

    protected ItfChatSalaBean gerarSalaVinculadaEntidade(EntradaNumeroWhatsapp pEntrada, FabTipoSalaMatrix pTipoSala, ItfBeanSimples pEntidade, Contato pContato, ItfUsuarioChat pUsuarioAtendimento) throws ErroConexaoServicoChat {

        ItfUsuarioChat usuarioContatoMatrix;
        try {
            usuarioContatoMatrix = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(pContato.getMatrixID());
            ItfChatSalaBean salaIdeal = pTipoSala
                    .getSalaMatrix(pEntidade, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(), Lists.newArrayList(pUsuarioAtendimento), Lists.newArrayList(usuarioContatoMatrix));

            String apelido = UtilMatrixERP.gerarAliasSalaIDCanonicoUsuarioWhatsapp(usuarioContatoMatrix, pTipoSala.getSlug());
            ItfChatSalaBean salaRelacionada = AplicacaoWsChat.SERVICO_MATRIX.getSalaCriandoSeNaoExistir(salaIdeal, apelido);

            if (!AplicacaoWsChat.SERVICO_MATRIX.isSalaEscutaDefinida()) {
                AplicacaoWsChat.SERVICO_MATRIX.registrarClasseDeEscutaSalas(ListenerSalaMatrix.class
                );
            }
            AplicacaoWsChat.SERVICO_MATRIX.salaAbrirSessao(salaRelacionada);
            return (ItfChatSalaBean) salaRelacionada;

        } catch (ErroPreparandoObjeto ex) {
            throw new ErroConexaoServicoChat("Falha defininido sala de atendimento matrix" + ex.getMessage());
        }

    }

    protected ItfChatSalaBean gerarSala(EntradaNumeroWhatsapp pEntrada, FabTipoSalaMatrix pTipoSala, Contato pContato, ItfUsuarioChat pUsuarioAtendimento) throws ErroConexaoServicoChat {

        switch (pTipoSala) {
            case CHAT_DINAMICO_DE_ENTIDADE:
            case MATRIX_CHAT_ATENDIMENTO_CHAMADO:
            case MATRIX_CHAT_VENDAS:
            case MATRIX_CHAT_ATENDIMENTO:
            case MATRIX_CHAT_DEBATE_INTERNO_LEAD_CLIENTE:

                throw new ErroConexaoServicoChat("tipo de sala não é compatível com estes parametros, envie a entidade relacionada ao " + this.toString());
        }

        ItfUsuarioChat UsuarioContato;
        try {
            UsuarioContato = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(pContato.getMatrixID());
            ItfChatSalaBean salaIdeal = pTipoSala
                    .getSalaMatrixPadrao(pUsuarioAtendimento,
                            UsuarioContato);

            String apelido = UtilMatrixERP.gerarAliasSalaIDCanonicoUsuarioWhatsapp(UsuarioContato, pTipoSala.getSlug());
            ItfChatSalaBean salaRelacionada = AplicacaoWsChat.SERVICO_MATRIX.getSalaCriandoSeNaoExistir(salaIdeal, apelido);

            if (!AplicacaoWsChat.SERVICO_MATRIX.isSalaEscutaDefinida()) {
                AplicacaoWsChat.SERVICO_MATRIX.registrarClasseDeEscutaSalas(ListenerSalaMatrix.class
                );
            }
            AplicacaoWsChat.SERVICO_MATRIX.salaAbrirSessao(salaRelacionada);
            return (ItfChatSalaBean) salaRelacionada;

        } catch (ErroPreparandoObjeto ex) {
            throw new ErroConexaoServicoChat("Falha defininido sala de atendimento matrix" + ex.getMessage());
        }

    }

    @Override
    public String getDesvioTrilhaPorMensgemWhatsapp(MensagemWhatsapp p) throws ErroComDevolucaoMensagemUsuario {
        if (p.getPayloadRespostaProgramada() != null && !p.getPayloadRespostaProgramada().isEmpty()) {

            //ItfServicoNavegacao servicoNavegacao = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada);
            //Class classe = servicoNavegacao.getClasseTrilhaDeNavegacao(getContextoDeSessao().getContato(), p.getPayloadRespostaProgramada());
            return p.getPayloadRespostaProgramada();

        }
        ultimaInteracaoContato = new Date();
        String possivelPalavraChave = UtilSBCoreStringFiltros.filtrarApenasLetra(p.getMensagem().toLowerCase());
        if (p.getMensagem() != null) {
            for (String palavra : AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada).getPalavrasParaCaminhoTrilhaRaiz()) {
                if (palavra.equals(possivelPalavraChave)) {
                    return AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada).getCaminhoTrilhaRaiz();
                }
            }
        }
        return null;
    }

    public RotaMensagemContato getRotaAtual() {
        return rotaAtual;
    }

    public ContextoContato getContextoDeSessao() {
        return contextoDeSessao;
    }

    @Override
    public void finalizarSesaso() {
        contextoDeSessao.setDataHoraFinalSessao(new Date());
        AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.contextoAtualizar(contextoDeSessao);
        AplicacaoWsChat.encerrrarSessao(entrada, getContextoDeSessao().getContato().getWaid());

    }

    @Override
    public void acaoTimeoutAguardandoInteracaoContato() {
        finalizarSesaso();
    }

    @Override
    public void acaoTimeoutAguardandoRespostaAtendimento() {
        if (rotaAtual != null) {
            switch (rotaAtual.getTipoRota().getTipoRotaMensagem()) {

                case MENU_OPCOES:
                case RESPOSTA_WEBSERVICE:
                case RETORNO_LINK:
                    break;
                case ENCAMINHAMENTO: {
                    try {
                        AplicacaoWsChat.SERVICO_MATRIX.enviarDirect(rotaAtual.getComoRotaEncaminhamentoMatrix().getAtendentePrincipal().getMatrixID(),
                                getContextoDeSessao().getContato().getNome() + " aguarda sua resposta em " + rotaAtual.getComoRotaEncaminhamentoMatrix().getSala().getNome()
                        );
                    } catch (ErroConexaoServicoChat ex) {

                    }
                }
                break;

                default:
                    throw new AssertionError();
            }

        }
    }

}
