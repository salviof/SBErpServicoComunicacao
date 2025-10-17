package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.UtilAplicacaoWsChatMatrixSalas;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaMensagemContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem.ENCAMINHAMENTO;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem.MENU_OPCOES;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem.RESPOSTA_WEBSERVICE;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem.RETORNO_LINK;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos.ListenerSalaMatrix;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.sessao.SessaoDeContato;
import br.org.coletivoJava.fw.api.erp.chat.model.ComandoDeAtendimento;

import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.UtilMatrixERP;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import com.google.common.collect.Lists;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreDataHora;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.modulos.objetos.registro.Interfaces.basico.ItfBeanSimples;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.coletivojava.fw.api.tratamentoErros.ErroPreparandoObjeto;

/**
 *
 * @author salvio
 */
public abstract class TrilhaNavegacaoAbs implements ItfTrilhaNavegacao {

    private Long id;

    private String trilhaOrigem;
    private String nome;
    private List<ItfTrilhaNavegacao> trilhasDisponiveis;
    private EntradaNumeroWhatsapp entrada;
    private String caminhoTrilha;
    protected RotaMensagemContato rotaAtual;
    private SessaoDeContato sessaoDoContato;
    private Date ultimaInteracaoContato;
    private Date ultimaInteracaoAtendimento;
    private long segundosTimeoutAguardandoContato = 90000;
    private long segundosTimeoutAguardandoAtendimento = 900;
    //segundosTimeoutAguardandoAtendimento:600000
    private boolean agenteUltimaInteracaoContato;
    private Monitor monitor;
    private boolean umaTrilhaRaiz;

    public void setSegundosTimeoutAguardandoContato(long segundosTimeoutAguardandoContato) {
        this.segundosTimeoutAguardandoContato = segundosTimeoutAguardandoContato;
    }

    public void setSegundosTimeoutAguardandoAtendimento(long segundosTimeoutAguardandoAtendimento) {
        this.segundosTimeoutAguardandoAtendimento = segundosTimeoutAguardandoAtendimento;
    }

    public void setUltimaInteracaoContato(Date ultimaInteracaoContato) {
        this.ultimaInteracaoContato = ultimaInteracaoContato;
    }

    @Override
    public void atualizarUltimaSalaConversaDeSessao(String pSala) {
        if (pSala == null) {
            return;
        }
        if (sessaoDoContato.getContexto().getSalaUltimaConversa() == null) {
            sessaoDoContato.getContexto().setSalaUltimaConversa(pSala);
            AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.contextoAtualizar(sessaoDoContato.getContexto());
        } else {
            if (!sessaoDoContato.getContexto().getSalaUltimaConversa().equals(pSala)) {
                sessaoDoContato.getContexto().setSalaUltimaConversa(pSala);
                AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.contextoAtualizar(sessaoDoContato.getContexto());
            }
        }

    }

    @Override
    public void atualizarContextoSessao() {
        ContextoContato contextoAtualizado = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContextoContato(entrada, sessaoDoContato.getContexto().getContato());
        if (contextoAtualizado != null) {
            sessaoDoContato.setContexto(contextoAtualizado);
            ItfServicoNavegacao servico;
            try {
                servico = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada);

                JsonObject dadosDeSesaoAtualizados = servico.gerarJsonDadosDeSessao(sessaoDoContato.getContexto().getContato());
                if (dadosDeSesaoAtualizados != null) {
                    sessaoDoContato.getContexto().setJsonDadosDoContexto(UtilSBCoreJson.getTextoByJsonObjeect(dadosDeSesaoAtualizados));
                }
                sessaoDoContato.getContexto().setTrilhaAtual(caminhoTrilha);
                AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.contextoAtualizar(sessaoDoContato.getContexto());
            } catch (ErroComDevolucaoMensagemUsuario ex) {
                return;
            }

        }
    }

    public TrilhaNavegacaoAbs(SessaoDeContato pSessaoDeContato, ItfTrilhaNavegacao pTrilhaOrigem, EntradaNumeroWhatsapp pEntrada, String pCaminhoTrilha) {
        entrada = pEntrada;
        if (pTrilhaOrigem != null) {
            trilhaOrigem = pTrilhaOrigem.getCaminhoTrilha();
        } else {
            trilhaOrigem = null;
        }
        caminhoTrilha = pCaminhoTrilha;
        sessaoDoContato = pSessaoDeContato;
        if (caminhoTrilha == null) {
            umaTrilhaRaiz = true;
        } else {
            try {
                umaTrilhaRaiz = caminhoTrilha.equals(AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada).getCaminhoTrilhaRaiz());
            } catch (ErroComDevolucaoMensagemUsuario ex) {
                umaTrilhaRaiz = false;
            }
        }
        if (pCaminhoTrilha == null) {
            this.getClass().getSimpleName();
        }

        if (pCaminhoTrilha == null) {
            try {
                caminhoTrilha = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada).getCaminhoTrilhaRaiz();
            } catch (ErroComDevolucaoMensagemUsuario ex) {

            }
        }
    }

    public class Monitor extends Thread {

        private final long segundosMonitorTimeoutAguardandoAtendimento;
        private final long segundosMonitorTimeoutAguardandoContato;
        private boolean monitorAtivo = true;
        private int acoesAguardandoAtendimento;
        private int acoesAguardandoContato;
        private final long vinteQuatroHorasEmSegudos = 86400l;

        public Monitor() {
            this(segundosTimeoutAguardandoAtendimento, segundosTimeoutAguardandoContato);
        }

        public Monitor(long pSegundosTimeoutAguardandoAtendimento, long pSegundosTimeoutAguardandoContato) {
            segundosMonitorTimeoutAguardandoAtendimento = pSegundosTimeoutAguardandoAtendimento;
            segundosMonitorTimeoutAguardandoContato = pSegundosTimeoutAguardandoContato;
        }

        @Override
        public void run() {

            while (monitorAtivo) {
                try {
                    sleep(120000);
                } catch (InterruptedException ex) {
                    monitorAtivo = false;
                }
                monitorAtivo = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.isTrilhaExiste(entrada, getContextoDeSessao().getContato(), caminhoTrilha);
                try {
                    if (agenteUltimaInteracaoContato) {//aguardando Atendimento
                        //Aguardando interacao do Atendimento
                        long tempoPassouInteracaoContato = UtilSBCoreDataHora.intervaloTempoSegundos(ultimaInteracaoContato, new Date());
                        long tempoLimite = segundosMonitorTimeoutAguardandoAtendimento;
                        if (acoesAguardandoAtendimento > 0) {
                            tempoLimite = tempoLimite + segundosMonitorTimeoutAguardandoAtendimento * acoesAguardandoAtendimento;
                        }

                        if (tempoPassouInteracaoContato >= tempoLimite) {
                            acoesAguardandoAtendimento++;
                            acaoTimeoutAguardandoRespostaAtendimento();
                            // registrarInteracao(TIPO_INTERACAO.ATENDIMENTO);
                        }
                    } else {
                        //Aguardando interação do Contato

                        long tempoLimite = segundosMonitorTimeoutAguardandoContato;
                        if (acoesAguardandoContato > 0) {
                            tempoLimite = tempoLimite + segundosMonitorTimeoutAguardandoContato * acoesAguardandoAtendimento;
                        }

                        long tempoPassouInteracaoAtendimento = UtilSBCoreDataHora.intervaloTempoSegundos(ultimaInteracaoAtendimento, new Date());
                        if (tempoPassouInteracaoAtendimento > vinteQuatroHorasEmSegudos) {
                            finalizarSesaso();
                        }
                        if (tempoPassouInteracaoAtendimento >= segundosMonitorTimeoutAguardandoContato) {
                            acaoTimeoutAguardandoInteracaoContato();
                        }
                        acoesAguardandoContato++;
                    }
                } catch (Throwable t) {
                    monitorAtivo = false;
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

    public String getTrilhaOrigem() {
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

        return UtilAplicacaoWsChatMatrixSalas.gerarSala(pEntrada, pTipoSala, pContato, pUsuarioAtendimento, false);

    }

    protected ItfChatSalaBean gerarSala(EntradaNumeroWhatsapp pEntrada, FabTipoSalaMatrix pTipoSala, Contato pContato, ItfUsuarioChat pUsuarioAtendimento, boolean pRemoverOutrosUsuarios) throws ErroConexaoServicoChat {

        return UtilAplicacaoWsChatMatrixSalas.gerarSala(pEntrada, pTipoSala, pContato, pUsuarioAtendimento, pRemoverOutrosUsuarios);

    }

    protected abstract AcaoGatilhoTrilha getAcaoTrilhaPorMensgemContato(String pMensagem, String pComando);

    protected abstract AcaoGatilhoTrilha getAcaoTrilhaPorMensgemAtendimento(String pMensagem, String pComando);

    @Override
    public final AcaoGatilhoTrilha getAcaoDeGatilhoPorMensagemWtzp(MensagemWhatsapp p) throws ErroComDevolucaoMensagemUsuario {

        if (p.getMensagem() != null) {
            final String palavra = p.getMensagem().toLowerCase();
            if (AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada)
                    .getPalavrasParaCaminhoTrilhaRaiz().stream()
                    .filter(comandoRaiz -> comandoRaiz.equals(palavra)).findFirst().isPresent()) {
                AcaoGatilhoTrilha acao = new AcaoGatilhoTrilha(FabAcaoGatilhosTrilha.NOVA_ROTA, this, sessaoDoContato.getContexto());
                acao.setNovaRota(AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada).getCaminhoTrilhaRaiz());
                return acao;
            }
        }

        return getAcaoTrilhaPorMensgemContato(p.getMensagem(), p.getPayloadRespostaProgramada());

    }

    @Override
    public final AcaoGatilhoTrilha getAcaoDeGatilhoPorComandoAtendimento(ComandoDeAtendimento p) throws ErroComDevolucaoMensagemUsuario {

        return getAcaoTrilhaPorMensgemAtendimento(p.getTextoCompleto(), p.getNovaRota());
    }

    @Override
    public final AcaoGatilhoTrilha getAcaoDeGatilhoPorEventoMatrix(ItfEventoMatix pEvento) throws ErroComDevolucaoMensagemUsuario {
        registrarInteracao(TIPO_INTERACAO.ATENDIMENTO);
        return getAcaoTrilhaPorMensgemAtendimento(pEvento.getContent().getString("body"), null);
    }

    public RotaMensagemContato getRotaAtual() {
        return rotaAtual;
    }

    public ContextoContato getContextoDeSessao() {
        return sessaoDoContato.getContexto();
    }

    public SessaoDeContato getSessao() {
        return sessaoDoContato;
    }

    @Override
    public void finalizarSesaso() {

        if (rotaAtual != null) {
            switch (rotaAtual.getTipoRota().getTipoRotaMensagem()) {

                case MENU_OPCOES:
                case RESPOSTA_WEBSERVICE:
                case RETORNO_LINK:
                    break;
                case ENCAMINHAMENTO: {
                    try {
                        AplicacaoWsChat.SERVICO_WHATSAPP.enviarMensagem(getEntrada(), getContextoDeSessao().getContato().getWaid(), "Sessão foi encerrada");
                        if (rotaAtual.getComoRotaEncaminhamentoMatrix().getSala() != null) {
                            AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(rotaAtual.getComoRotaEncaminhamentoMatrix().getSala(), "Sessão Encerrada");
                        }
                    } catch (ErroConexaoServicoChat ex) {

                    }
                }
                break;

                default:
                    throw new AssertionError();
            }

        }

        AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.contextoAtualizar(sessaoDoContato.getContexto());
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
                        if (rotaAtual.getComoRotaEncaminhamentoMatrix().getSala() != null) {
                            AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(rotaAtual.getComoRotaEncaminhamentoMatrix().getSala(), getContextoDeSessao().getContato().getNome() + " aguarda sua resposta em " + rotaAtual.getComoRotaEncaminhamentoMatrix().getSala().getNome());
                        } else {
                            AplicacaoWsChat.SERVICO_MATRIX.enviarDirect(rotaAtual.getComoRotaEncaminhamentoMatrix().getAtendentePrincipal().getMatrixID(),
                                    getContextoDeSessao().getContato().getNome() + " aguarda sua resposta em " + rotaAtual.getComoRotaEncaminhamentoMatrix().getSala().getNome()
                            );
                        }
                    } catch (ErroConexaoServicoChat ex) {

                    }
                }
                break;

                default:
                    throw new AssertionError();
            }

        }
    }

    public enum TIPO_INTERACAO {

        CONTATO, ATENDIMENTO;
    }

    public void registrarInteracao(TIPO_INTERACAO tipoInteracao) {

        if (monitor == null) {
            monitor = new Monitor();
            monitor.start();
        }
        switch (tipoInteracao) {

            case CONTATO:
                ultimaInteracaoContato = new Date();
                agenteUltimaInteracaoContato = true;
                break;
            case ATENDIMENTO:
                ultimaInteracaoAtendimento = new Date();
                agenteUltimaInteracaoContato = false;
                break;
            default:
                throw new AssertionError();
        }
    }

    public boolean isUmaTrilhaRaiz() {
        return umaTrilhaRaiz;
    }
}
