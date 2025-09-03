package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao;

import br.org.coletivoJava.fw.api.erp.chat.model.ComandoDeAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.UtilAplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.FabTipoGatilho.GATILHO_COMANDO_ATENDIMENTO;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.FabTipoGatilho.GATILHO_EVENTO_MATRIX;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.FabTipoGatilho.GATILHO_MENSAGEM_WHATSAPP;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.AcaoGatilhoTrilha;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.FabAcaoGatilhosTrilha;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.FabAcaoGatilhosTrilha.ENCERRAR_SESSAO;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.FabAcaoGatilhosTrilha.MENSAGEM_ATENDIMENTO;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.FabAcaoGatilhosTrilha.MENSAGEM_CONTATO_WHATSAPP;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.FabAcaoGatilhosTrilha.NOVA_ROTA;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.TrilhaNavegacaoAbs;

import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivojava.fw.api.tratamentoErros.FabErro;

/**
 *
 * @author salvio
 */
public class GestaoDeServicosNavegacao {

    private static Map<EntradaNumeroWhatsapp, Map<Contato, ItfTrilhaNavegacao>> ULTIMAS_TRILHAS = Collections.synchronizedMap(new HashMap<>());
    private static Map<EntradaNumeroWhatsapp, ItfServicoNavegacao> MAPA_SERVICOS_NAVEGACAO = Collections.synchronizedMap(new HashMap<>());

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

    public boolean removerRota(EntradaNumeroWhatsapp pEntrada, Contato pContato) throws ErroComDevolucaoMensagemUsuario {
        if (!ULTIMAS_TRILHAS.containsKey(pEntrada)) {
            return false;
        }
        if (!ULTIMAS_TRILHAS.get(pEntrada).containsKey(pContato)) {
            return false;
        }
        ULTIMAS_TRILHAS.get(pEntrada).remove(pContato);
        return true;
    }

    private ItfTrilhaNavegacao instanciarTrilha(Class classeRegraDeNegocioTrilha, ContextoContato pContexto, ItfTrilhaNavegacao trilhaPai, EntradaNumeroWhatsapp pEntrada, String pCaminhoTrilha) throws ErroComDevolucaoMensagemUsuario {
        ItfTrilhaNavegacao novaTrilha = null;

        try {
            Constructor construtor = (Constructor) classeRegraDeNegocioTrilha.getConstructor(ContextoContato.class, ItfTrilhaNavegacao.class, EntradaNumeroWhatsapp.class, String.class);
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
            trilhaAtual = instanciarTrilha(classe, pContexto, null, entrada, pRota);
            trilhaAtual.iniciarTrilha();
            try {
                JsonObject dadosContexto = servicoNavegacao.gerarJsonDadosDeSessao(pContexto.getContato());
                String textoDadosContexto = UtilSBCoreJson.getTextoByJsonObjeect(dadosContexto);
                if (textoDadosContexto != null) {
                    pContexto.setJsonDadosDoContexto(textoDadosContexto);
                }

            } catch (Throwable t) {
                SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Erro atualizando dados de contexto" + pContexto, t);
            }

            AcaoGatilhoTrilha acaoGatilho = trilhaAtual.getAcaoDeGatilhoLoadDadosSessao(pContexto);
            if (acaoGatilho.getTipoAcao().equals(FabAcaoGatilhosTrilha.NOVA_ROTA)) {
                if (!acaoGatilho.getNovaRota().equals(pRota)) {
                    if (servicoNavegacao.isRotaExiste(pRota)) {
                        return getTrilha(pContexto, trilhaAtual, acaoGatilho.getNovaRota());
                    }
                }
            }
            pContexto.setTrilhaAtual(trilhaAtual.getCaminhoTrilha());
            AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.contextoAtualizar(pContexto);
        }
        return trilhaAtual;

    }

    public ItfTrilhaNavegacao getTrilhaByEventoExistente(EntradaNumeroWhatsapp pEntrada, Contato pContato, ItfEventoMatix pEvento) throws ErroComDevolucaoMensagemUsuario {
        return executarGatilhosDefinirTrilha(pEntrada, pContato, new TipoGatilho(pEvento));
    }

    public ItfTrilhaNavegacao getTrilhaByComandoMatrix(EntradaNumeroWhatsapp pEntrada, Contato pContato, ComandoDeAtendimento pComandoAtendimento) throws ErroComDevolucaoMensagemUsuario {
        return executarGatilhosDefinirTrilha(pEntrada, pContato, new TipoGatilho(pComandoAtendimento));
    }

    public ItfTrilhaNavegacao getTrilhaByMensagemWhatasapp(EntradaNumeroWhatsapp pEntrada, Contato pContato, MensagemWhatsapp pMensagem) throws ErroComDevolucaoMensagemUsuario {

        return executarGatilhosDefinirTrilha(pEntrada, pContato, new TipoGatilho(pMensagem));

    }

    private ItfTrilhaNavegacao executarGatilhosDefinirTrilha(EntradaNumeroWhatsapp pEntrada, Contato pContato, TipoGatilho pTipoGatilho) throws ErroComDevolucaoMensagemUsuario {
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
            trilhaAtual = getTrilha(contextoDoUsuario, trilhaAtual, caminhoTrilha);
        } catch (ErroRegraDeNegocio | ErroConexaoServicoChat ex) {
            throw new ErroComDevolucaoMensagemUsuario("A rota não foi definida na trilha" + trilhaAtual.getClass().getSimpleName() + " para " + pContato + " ->" + ex.getMessage(), "A Mensagem não foi entregue. Falha definindo a trilha da sua comunicação, tente digitar menu para retomar ao início, ou entre em contato ligando neste número ");
        }

        if (trilhaAtual == null) {
            throw new ErroComDevolucaoMensagemUsuario("A rota não foi definida na trilha" + trilhaAtual.getClass().getSimpleName() + " para " + pContato, "A Mensagem não foi entregue. Falha definindo a trilha da sua comunicação, tente digitar menu para retomar ao início, ou entre em contato ligando neste número ");
        }
        AcaoGatilhoTrilha acaoGatilho = null;

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

        ULTIMAS_TRILHAS.get(pEntrada).put(pContato, trilhaAtual);

        return trilhaAtual;
    }

    private ItfTrilhaNavegacao processarAcaoGatilho(AcaoGatilhoTrilha pAcaoGatilho) throws ErroComDevolucaoMensagemUsuario {
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

                case NOVA_ROTA:
                    if (pAcaoGatilho.getNovaRota() == null
                            || pAcaoGatilho.getNovaRota().equals(pAcaoGatilho.getTrilha().getCaminhoTrilha())) {
                        return novaTrilha;
                    } else {
                        if (servicoNavegacao.isRotaExiste(pAcaoGatilho.getNovaRota())) {
                            try {
                                novaTrilha = getTrilha(pAcaoGatilho.getContexto(), pAcaoGatilho.getTrilha(), pAcaoGatilho.getNovaRota());

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
                    if (pAcaoGatilho.getMensagemParaContato() != null && !pAcaoGatilho.getMensagemParaContato().isEmpty()) {
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
