package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

            MAPA_SERVICOS_NAVEGACAO.put(pEntrada, navegacao);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ErroComDevolucaoMensagemUsuario("Falha criando serviço de navegacao" + ex.getMessage(), "Erro procurando serviço de navegação, entre em contato com o suporte");
        } catch (ErroConexaoServicoChat ex) {
            throw new ErroComDevolucaoMensagemUsuario("Falha criando serviço de navegacao" + ex.getMessage(), "Erro procurando serviço de navegação, entre em contato com o suporte");
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

    public ItfTrilhaNavegacao getTrilha(EntradaNumeroWhatsapp pEntrada, Contato pContato, MensagemWhatsapp pMensagem) throws ErroComDevolucaoMensagemUsuario {
        ItfServicoNavegacao servicoNavegacao = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(pEntrada);
        ItfTrilhaNavegacao trilhaAtual = null;
        if (!ULTIMAS_TRILHAS.containsKey(pEntrada)) {
            ULTIMAS_TRILHAS.put(pEntrada, Collections.synchronizedMap(new HashMap<>()));
        }
        if (ULTIMAS_TRILHAS.get(pEntrada).containsKey(pContato)) {
            trilhaAtual = ULTIMAS_TRILHAS.get(pEntrada).get(pContato);
        }
        ContextoContato contextoDoUsuario = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContextoContato(pEntrada, pContato);
        String caminhoTrilha = null;
        if (pMensagem.getPayloadRespostaProgramada() != null && !pMensagem.getPayloadRespostaProgramada().isEmpty()) {
            caminhoTrilha = pMensagem.getPayloadRespostaProgramada();
        } else {
            caminhoTrilha = contextoDoUsuario.getTrilhaAtual();

        }

        if (trilhaAtual == null) {

            Class classe = servicoNavegacao.getClasseTrilhaDeNavegacao(pContato, caminhoTrilha);
            trilhaAtual = instanciarTrilha(classe, contextoDoUsuario, null, pEntrada, caminhoTrilha);

            try {
                trilhaAtual.iniciarTrilha();
            } catch (ErroConexaoServicoChat ex) {
                throw new ErroComDevolucaoMensagemUsuario("Falha obtendo regra de negocio " + ex.getMessage(), "A Mensagem não foi entregue,a trilha de navegação falhou a ser carregada, entre em contato com o administrador");
            }
        }
        String caminhoNovaTrilha = trilhaAtual.getDesvioTrilhaPorMensgemWhatsapp(pMensagem);

        if (caminhoNovaTrilha != null) {
            String caminhoTrrilhaAtual = trilhaAtual.getCaminhoTrilha();
            if (caminhoTrrilhaAtual == null || !caminhoTrrilhaAtual.equals(caminhoNovaTrilha)) {
                Class<? extends ItfTrilhaNavegacao> classeTrilhaAlternativa = servicoNavegacao.getClasseTrilhaDeNavegacao(pContato, caminhoNovaTrilha);
                contextoDoUsuario.setTrilhaAtual(caminhoTrilha);

                trilhaAtual = instanciarTrilha(classeTrilhaAlternativa, contextoDoUsuario, trilhaAtual, pEntrada, caminhoNovaTrilha);

                try {
                    trilhaAtual.iniciarTrilha();
                    AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.contextoAtualizar(contextoDoUsuario);
                    if (trilhaAtual.getRotaAtual() == null) {
                        throw new ErroComDevolucaoMensagemUsuario("A rota precisa ser definida ao iniciar uma trilha, isso não aconteceu na trilha" + trilhaAtual.getClass().getSimpleName(), "A Mensagem não foi entregue,a trilha de navegação falhou a ser carregada, entre em contato com o administrador");
                    }

                } catch (ErroConexaoServicoChat ex) {
                    throw new ErroComDevolucaoMensagemUsuario("Falha obtendo regra de negocio " + ex.getMessage(), "A Mensagem não foi entregue,a trilha de navegação falhou a ser carregada, entre em contato com o administrador");
                } catch (Throwable t) {
                    throw new ErroComDevolucaoMensagemUsuario("Falha obtendo regra de negocio " + t.getMessage(), "A Mensagem não foi entregue,a trilha de navegação falhou a ser carregada, entre em contato com o administrador");
                }
            }

        }

        if (trilhaAtual.getRotaAtual() == null) {
            throw new ErroComDevolucaoMensagemUsuario("A rota não foi definida na trilha" + trilhaAtual.getClass().getSimpleName() + " para " + pContato, "Não consegui definir uma rota para sua mensagem, entre em contato com nosso suporte");
        }
        ULTIMAS_TRILHAS.get(pEntrada).put(pContato, trilhaAtual);
        return trilhaAtual;
    }

}
