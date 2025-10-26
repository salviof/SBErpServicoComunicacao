package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao;

import br.org.coletivoJava.fw.api.erp.chat.model.ComandoDeAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.UtilAplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComEncaminhamentoRotaRaiz;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroIniciandoTrilha;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.FabTipoGatilho.GATILHO_COMANDO_ATENDIMENTO;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.FabTipoGatilho.GATILHO_EVENTO_MATRIX;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.FabTipoGatilho.GATILHO_MENSAGEM_WHATSAPP;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.sessao.SessaoDeContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.AcaoGatilhoTrilha;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.FabAcaoGatilhosTrilha;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.FabAcaoGatilhosTrilha.ENCERRAR_SESSAO;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.FabAcaoGatilhosTrilha.MENSAGEM_ATENDIMENTO;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.FabAcaoGatilhosTrilha.MENSAGEM_CONTATO_WHATSAPP;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.TrilhaNavegacaoAbs;

import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.coletivojava.fw.api.tratamentoErros.FabErro;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.FabAcaoGatilhosTrilha.NOVA_TRILHA;

/**
 *
 * @author salvio
 */
public class GestaoDeServicosNavegacao {

    private static Map<EntradaNumeroWhatsapp, Map<Contato, ItfTrilhaNavegacao>> ULTIMAS_TRILHAS = Collections.synchronizedMap(new HashMap<>());
    private static Map<EntradaNumeroWhatsapp, ItfServicoNavegacao> MAPA_SERVICOS_NAVEGACAO = Collections.synchronizedMap(new HashMap<>());
    private static Map<Long, SessaoDeContato> MAPA_SESSOES = new HashMap<>();

    public SessaoDeContato getSessaoDoContato(ContextoContato pContexto) throws ErroConexaoServicoChat, ErroRegraDeNegocio, ErroComDevolucaoMensagemUsuario {
        if (pContexto == null) {
            throw new ErroRegraDeNegocio("Contexto de sessao nulo enviado");
        }
        if (!MAPA_SESSOES.containsKey(pContexto.getId())) {
            EntradaNumeroWhatsapp entrada = AplicacaoWsChat.getEntradaByCodigoEntrada(pContexto.getCodigoEntrada());
            MAPA_SESSOES.put(pContexto.getId(), new SessaoDeContato(pContexto, entrada));
        }
        MAPA_SESSOES.get(pContexto.getId()).setContexto(pContexto);

        return MAPA_SESSOES.get(pContexto.getId());

    }

    public ItfServicoNavegacao getServicoNavegacao(EntradaNumeroWhatsapp pEntrada) throws ErroComDevolucaoMensagemUsuario {
        if (MAPA_SERVICOS_NAVEGACAO.containsKey(pEntrada)) {
            MAPA_SERVICOS_NAVEGACAO.get(pEntrada);
        }
        ItfServicoNavegacao servico;
        try {
            Class servicoNavegacao = AplicacaoWsChat.getCentralLogicaProcesasmento().getClasseServicoNavegacao(pEntrada);
            Constructor construtor = servicoNavegacao.getConstructor(EntradaNumeroWhatsapp.class);
            ItfServicoNavegacao navegacao = (ItfServicoNavegacao) construtor.newInstance(pEntrada);
            navegacao.validarServicoNavegacao();
            MAPA_SERVICOS_NAVEGACAO.put(pEntrada, navegacao);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ErroComDevolucaoMensagemUsuario("Falha criando serviço de navegacao" + ex.getMessage(), "Erro procurando serviço de navegação, entre em contato com o suporte");
        } catch (ErroConexaoServicoChat ex) {
            throw new ErroComDevolucaoMensagemUsuario("Falha criando serviço de navegacao" + ex.getMessage(), "Erro procurando serviço de navegação, entre em contato com o suporte");
        } catch (ErroFalhaEncaminhando erroCriandoSErvicoDeNAvegacao) {

        }

        try {
            return MAPA_SERVICOS_NAVEGACAO.get(pEntrada);
        } catch (Throwable t) {
            throw new ErroComDevolucaoMensagemUsuario("Nenhuma rota de navegação foi encontrada para " + pEntrada.getCodigo(), "Erro procurando serviço de navegação, entre em contato com o suporte "
            );
        }
    }

    public boolean removerRota(ContextoContato pContexto) throws ErroComDevolucaoMensagemUsuario, ErroRegraDeNegocio {
        EntradaNumeroWhatsapp entrada = AplicacaoWsChat.getEntradaByCodigoEntrada(pContexto.getCodigoEntrada());
        if (!ULTIMAS_TRILHAS.containsKey(entrada)) {
            return false;
        }
        if (!ULTIMAS_TRILHAS.get(entrada).containsKey(pContexto.getContato())) {
            return false;
        }
        ULTIMAS_TRILHAS.get(entrada).remove(pContexto.getContato());
        MAPA_SESSOES.remove(pContexto.getId());
        return true;
    }

    private ItfTrilhaNavegacao instanciarTrilha(Class classeRegraDeNegocioTrilha, SessaoDeContato pContexto, ItfTrilhaNavegacao trilhaPai, EntradaNumeroWhatsapp pEntrada, String pCaminhoTrilha) throws ErroComDevolucaoMensagemUsuario {
        ItfTrilhaNavegacao novaTrilha = null;

        try {
            Constructor construtor = (Constructor) classeRegraDeNegocioTrilha.getConstructor(SessaoDeContato.class, ItfTrilhaNavegacao.class, EntradaNumeroWhatsapp.class, String.class);
            novaTrilha = (ItfTrilhaNavegacao) construtor.newInstance(pContexto, trilhaPai, pEntrada, pCaminhoTrilha);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new ErroComDevolucaoMensagemUsuario("Verifique o constructor da  trilha," + classeRegraDeNegocioTrilha.getCanonicalName() + " "
                    + "que deve conter: ContextoContato, TrilhaPai, EntradaNumeroWhatsapp, e string com caminho da trilha de navegação"
                    + "Erro: " + ex.getMessage(),
                    "Falha encontrando rota de mensagem, entre em contato com o suporte.");
        }
        if (novaTrilha == null) {
            throw new ErroComDevolucaoMensagemUsuario("A trilha não pode ser iniciada" + classeRegraDeNegocioTrilha.getCanonicalName() + " ",
                    "Falha encontrando rota de mensagem, entre em contato com o suporte.");
        }
        return novaTrilha;
    }

    public boolean isTrilhaExiste(EntradaNumeroWhatsapp pEntrada, Contato pContato, String pCaminhoTrilha) {
        if (!ULTIMAS_TRILHAS.get(pEntrada).containsKey(pContato)) {
            return false;
        }
        if (ULTIMAS_TRILHAS.get(pEntrada).containsKey(pContato)) {
            String rota = ULTIMAS_TRILHAS.get(pEntrada).get(pContato).getCaminhoTrilha();
            if (rota != null && pCaminhoTrilha != null) {
                if (rota.equals(pCaminhoTrilha)) {
                    return true;
                }
            }
        }
        return false;

    }

    private ItfTrilhaNavegacao getTrilha(ContextoContato pContexto, ItfTrilhaNavegacao trilhaAtual, String pRota) throws ErroRegraDeNegocio, ErroComDevolucaoMensagemUsuario, ErroConexaoServicoChat {

        EntradaNumeroWhatsapp entrada = AplicacaoWsChat.getEntradaByCodigoEntrada(pContexto.getCodigoEntrada());
        ItfServicoNavegacao servicoNavegacao = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada);
        boolean novaTrilha = (trilhaAtual == null);
        if (!novaTrilha) {
            if (!trilhaAtual.getCaminhoTrilha().equals(pRota)) {
                novaTrilha = true;
            }
        }

        if (novaTrilha) {
            System.out.println("PEsquisando trilha para " + pRota);

            Class classe = servicoNavegacao.getClasseTrilhaDeNavegacao(pContexto.getContato(), pRota);
            trilhaAtual = instanciarTrilha(classe, AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getSessaoDoContato(pContexto), null, entrada, pRota);

            trilhaAtual.atualizarContextoSessao();

            try {
                trilhaAtual.iniciarTrilha();
            } catch (ErroComEncaminhamentoRotaRaiz ex) {
                String trilharaiz = trilhaAtual.getSessao().getServicoNavegacao().getCaminhoTrilhaRaiz();
                if (trilhaAtual.getCaminhoTrilha() != null && !trilhaAtual.getCaminhoTrilha().equals(trilharaiz)) {
                    return getTrilha(pContexto, trilhaAtual, trilharaiz);
                }
            }
            if (trilhaAtual.getRotaAtual() != null) {
                if (trilhaAtual.getRotaAtual().getTipoRota().getTipoRotaMensagem().equals(FabTipoRotaMensagem.ENCAMINHAMENTO)) {
                    String codigoUltimaSala = trilhaAtual.getRotaAtual().getComoRotaEncaminhamentoMatrix().getSala().getCodigoChat();
                    trilhaAtual.atualizarUltimaSalaConversaDeSessao(codigoUltimaSala);
                }
            }
            try {

                AcaoGatilhoTrilha acaoGatilho = trilhaAtual.getAcaoDeGatilhoInicioTrilha(pContexto);
                if (acaoGatilho != null && acaoGatilho.getTipoAcao().equals(FabAcaoGatilhosTrilha.NOVA_TRILHA)) {
                    if (!acaoGatilho.getNovaTrilha().equals(pRota)) {
                        if (servicoNavegacao.isRotaExiste(pRota)) {
                            return getTrilha(pContexto, trilhaAtual, acaoGatilho.getNovaTrilha());
                        }
                    }
                } else {
                    if (acaoGatilho != null) {
                        processarAcaoGatilho(acaoGatilho);
                    }
                }

            } catch (Throwable t) {
                SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha obtendo ação após iniciar a trilha", t);
            }

        }

        return trilhaAtual;

    }

    public ItfTrilhaNavegacao getTrilhaByEventoExistente(EntradaNumeroWhatsapp pEntrada, Contato pContato, ItfEventoMatix pEvento) throws ErroComDevolucaoMensagemUsuario, ErroIniciandoTrilha {
        ItfTrilhaNavegacao trilha = executarGatilhosDefinirTrilha(pEntrada, pContato, new TipoGatilho(pEvento));
        trilha.registrarInteracao(TrilhaNavegacaoAbs.TIPO_INTERACAO.ATENDIMENTO);
        return trilha;
    }

    public ItfTrilhaNavegacao getTrilhaByComandoMatrix(EntradaNumeroWhatsapp pEntrada, Contato pContato, ComandoDeAtendimento pComandoAtendimento) throws ErroComDevolucaoMensagemUsuario, ErroIniciandoTrilha {
        ItfTrilhaNavegacao trilha = executarGatilhosDefinirTrilha(pEntrada, pContato, new TipoGatilho(pComandoAtendimento));
        trilha.registrarInteracao(TrilhaNavegacaoAbs.TIPO_INTERACAO.ATENDIMENTO);
        return trilha;
    }

    public ItfTrilhaNavegacao getTrilhaByMensagemWhatasapp(EntradaNumeroWhatsapp pEntrada, Contato pContato, MensagemWhatsapp pMensagem) throws ErroComDevolucaoMensagemUsuario, ErroIniciandoTrilha {
        ItfTrilhaNavegacao trilha = executarGatilhosDefinirTrilha(pEntrada, pContato, new TipoGatilho(pMensagem));
        trilha.registrarInteracao(TrilhaNavegacaoAbs.TIPO_INTERACAO.CONTATO);
        return trilha;
    }

    private ItfTrilhaNavegacao executarGatilhosDefinirTrilha(EntradaNumeroWhatsapp pEntrada, Contato pContato, TipoGatilho pTipoGatilho) throws ErroComDevolucaoMensagemUsuario, ErroIniciandoTrilha {
        ItfServicoNavegacao servicoNavegacao = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(pEntrada);
        ItfTrilhaNavegacao trilhaAtual = null;

        if (!ULTIMAS_TRILHAS.containsKey(pEntrada)) {
            ULTIMAS_TRILHAS.put(pEntrada, Collections.synchronizedMap(new HashMap<>()));
        }
        if (ULTIMAS_TRILHAS.get(pEntrada).containsKey(pContato)) {
            trilhaAtual = ULTIMAS_TRILHAS.get(pEntrada).get(pContato);
        }
        ContextoContato contextoDoUsuario = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContextoContato(pEntrada, pContato);
        final String nomeTrilhaInicialDoContexto = contextoDoUsuario.getTrilhaAtual();
        String caminhoTrilha = nomeTrilhaInicialDoContexto;
        String rotaExplicita = null;
        switch (pTipoGatilho.getTipoGatilho()) {

            case GATILHO_MENSAGEM_WHATSAPP:
                rotaExplicita = UtilAplicacaoWsChat.getTextoRotaExplicitaPorMensagemWtzp(pEntrada, pTipoGatilho.getMensagemWtzp());
                break;
            case GATILHO_COMANDO_ATENDIMENTO:
                rotaExplicita = pTipoGatilho.getComando().getNovaRota();
                break;
            case GATILHO_EVENTO_MATRIX:
                rotaExplicita = UtilAplicacaoWsChat.getTextoRotaExplicitaPorMensagemMatrix(pEntrada, pTipoGatilho.getEvento());
                break;
            default:
                throw new AssertionError();
        }

        if (rotaExplicita != null) {
            if (servicoNavegacao.isRotaExiste(rotaExplicita)) {
                caminhoTrilha = rotaExplicita;
            }
        }

        try {
            try {
                trilhaAtual = getTrilha(contextoDoUsuario, trilhaAtual, caminhoTrilha);
            } catch (ErroRegraDeNegocio | ErroConexaoServicoChat ex) {
                throw new ErroComDevolucaoMensagemUsuario("A rota não foi definida na trilha" + trilhaAtual.getClass().getSimpleName() + " para " + pContato + " ->" + ex.getMessage(), "A Mensagem não foi entregue. Falha definindo a trilha da sua comunicação, tente digitar menu para retomar ao início, ou entre em contato ligando neste número ");
            }

            if (trilhaAtual == null) {
                throw new ErroComDevolucaoMensagemUsuario("A rota não foi definida na trilha" + trilhaAtual.getClass().getSimpleName() + " para " + pContato, "A Mensagem não foi entregue. Falha definindo a trilha da sua comunicação, tente digitar menu para retomar ao início, ou entre em contato ligando neste número ");
            }
        } catch (ErroComDevolucaoMensagemUsuario pErro) {
            throw new ErroIniciandoTrilha(pErro);
        }
        AcaoGatilhoTrilha acaoGatilho = null;
        try {
            switch (pTipoGatilho.getTipoGatilho()) {
                case GATILHO_MENSAGEM_WHATSAPP:
                    acaoGatilho = trilhaAtual.getAcaoDeGatilhoPorMensagemWtzp(pTipoGatilho.getMensagemWtzp());
                    break;
                case GATILHO_COMANDO_ATENDIMENTO:
                    acaoGatilho = trilhaAtual.getAcaoDeGatilhoPorComandoAtendimento(pTipoGatilho.getComando());
                    break;
                case GATILHO_EVENTO_MATRIX:
                    acaoGatilho = trilhaAtual.getAcaoDeGatilhoPorEventoMatrix(pTipoGatilho.getEvento());
                    break;

                default:
                    throw new AssertionError();
            }

            ItfTrilhaNavegacao novaTrilha = processarAcaoGatilho(acaoGatilho);
            while (novaTrilha != null) {
                trilhaAtual = novaTrilha;

                switch (pTipoGatilho.getTipoGatilho()) {
                    case GATILHO_MENSAGEM_WHATSAPP:
                        acaoGatilho = trilhaAtual.getAcaoDeGatilhoPorMensagemWtzp(pTipoGatilho.getMensagemWtzp());
                        break;
                    case GATILHO_COMANDO_ATENDIMENTO:
                        acaoGatilho = trilhaAtual.getAcaoDeGatilhoPorComandoAtendimento(pTipoGatilho.getComando());
                        break;
                    case GATILHO_EVENTO_MATRIX:
                        acaoGatilho = trilhaAtual.getAcaoDeGatilhoPorEventoMatrix(pTipoGatilho.getEvento());
                        break;

                    default:
                        throw new AssertionError();
                }
                if (acaoGatilho != null) {
                    novaTrilha = processarAcaoGatilho(acaoGatilho);
                }

            }
        } catch (ErroComDevolucaoMensagemUsuario e) {
            throw e;
        } catch (Throwable t) {
            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Houve um erro não esperado processando o gatilho na trilha" + trilhaAtual.getClass().getSimpleName(), t);
        }

        ULTIMAS_TRILHAS.get(pEntrada).put(pContato, trilhaAtual);

        return trilhaAtual;
    }

    public ItfTrilhaNavegacao processarAcaoGatilho(AcaoGatilhoTrilha pAcaoGatilho) throws ErroComDevolucaoMensagemUsuario {
        if (pAcaoGatilho == null) {
            return null;
        }
        Contato contato = pAcaoGatilho.getContexto().getContato();
        EntradaNumeroWhatsapp entrada;
        try {
            entrada = AplicacaoWsChat.getEntradaByCodigoEntrada(pAcaoGatilho.getContexto().getCodigoEntrada());
        } catch (ErroRegraDeNegocio ex) {
            return null;
        }
        ItfServicoNavegacao servicoNavegacao = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada);
        ItfTrilhaNavegacao novaTrilha = null;
        if (pAcaoGatilho == null) {
            return novaTrilha;
        } else {
            switch (pAcaoGatilho.getTipoAcao()) {
                case ENCERRAR_SESSAO:
                    return novaTrilha;

                case NOVA_TRILHA:

                    if (pAcaoGatilho.getNovaTrilha() == null
                            || pAcaoGatilho.getNovaTrilha().equals(pAcaoGatilho.getTrilha().getCaminhoTrilha())) {
                        ULTIMAS_TRILHAS.get(entrada).put(pAcaoGatilho.getContexto().getContato(), novaTrilha);
                        return novaTrilha;
                    } else {
                        if (servicoNavegacao.isRotaExiste(pAcaoGatilho.getNovaTrilha())) {
                            try {
                                novaTrilha = getTrilha(pAcaoGatilho.getContexto(), pAcaoGatilho.getTrilha(), pAcaoGatilho.getNovaTrilha());
                                ULTIMAS_TRILHAS.get(entrada).put(pAcaoGatilho.getContexto().getContato(), novaTrilha);
                                return novaTrilha;
                            } catch (ErroRegraDeNegocio | ErroConexaoServicoChat ex) {
                                throw new ErroComDevolucaoMensagemUsuario("A rota não foi definida na trilha" + pAcaoGatilho.getTrilha().getClass().getSimpleName() + " para " + pAcaoGatilho.getContexto().getContato(),
                                        "A Mensagem não foi entregue. Falha definindo a trilha da sua comunicação, tente digitar menu para retomar ao início, ou entre em contato ligando neste número ");
                            }
                        } else {
                            return novaTrilha;
                        }
                    }

                case MENSAGEM_CONTATO_WHATSAPP:
                    if (pAcaoGatilho.getMensagemParaContato() != null && pAcaoGatilho.getMensagemParaContato().getCorpo() != null && !pAcaoGatilho.getMensagemParaContato().getCorpo().isEmpty()) {
                        try {
                            AplicacaoWsChat.SERVICO_WHATSAPP.enviarMensagemTexto(entrada, contato.getWaid(), pAcaoGatilho.getMensagemParaContato());
                        } catch (ErroConexaoServicoChat ex) {
                            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Erro encaminhando mensagem automatica para whatsapp", ex);
                        }
                    }

                    return novaTrilha;

                case MENSAGEM_ATENDIMENTO:
                    if (pAcaoGatilho.getMensagemParaAtendimento() != null && !pAcaoGatilho.getMensagemParaAtendimento().isEmpty()) {
                        try {
                            AplicacaoWsChat.SERVICO_WHATSAPP.enviarMensagemTexto(entrada, contato.getWaid(), pAcaoGatilho.getMensagemParaAtendimento());
                        } catch (ErroConexaoServicoChat ex) {
                            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Erro encaminhando mensagem automatica para whatsapp", ex);
                        }
                    }
                    return novaTrilha;

                default:
                    return novaTrilha;
            }
        }

    }

}
