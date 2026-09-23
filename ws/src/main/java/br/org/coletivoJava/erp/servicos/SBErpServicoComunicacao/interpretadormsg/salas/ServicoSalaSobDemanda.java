package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.salas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config.FabConfigServicoComunicacao;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import com.super_bits.modulosSB.SBCore.ConfigGeral.CarameloCode;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cria salas com prazo de espera.
 *
 * O trabalho de criação roda em outra thread; a espera tem prazo. Quando o
 * prazo estoura, a espera é abandonada mas <b>o trabalho não</b>: a criação
 * segue e aquece o cache de salas do ChatMatrixOrgimpl, de modo que a próxima
 * tentativa (reentrega do WhatsApp ou o dreno da fila) encontre a sala pronta.
 *
 * Também deduplica criações: uma rajada de mensagens do mesmo contato dispara
 * uma única criação, e todas as chamadas esperam o mesmo resultado.
 *
 * @author salvio
 */
public class ServicoSalaSobDemanda {

    private static final String TAG_LOG = "[SALA-DEMANDA]";

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(6, new ThreadFactory() {

        private final AtomicInteger sequencia = new AtomicInteger();

        @Override
        public Thread newThread(Runnable pTarefa) {
            Thread thread = new Thread(pTarefa, "sala-sob-demanda-" + sequencia.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    });

    private static final Map<String, CompletableFuture<ComoChatSalaBean>> SALAS_EM_CRIACAO = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> APELIDOS_EM_CRIACAO_POR_CONTATO = new ConcurrentHashMap<>();

    /**
     * A instrumentação nunca pode interromper a criação da sala, por isso o
     * serviço de log é chamado dentro de um try.
     */
    private static void log(FabMensagens pTipo, String pMensagem) {
        try {
            CarameloCode.getServicoLogEventos().registrarLogDeEvento(pTipo, TAG_LOG + " " + pMensagem);
        } catch (Throwable t) {
            System.out.println(TAG_LOG + " " + pTipo + " " + pMensagem);
        }
    }

    // Lido uma única vez: a leitura de parâmetro de módulo escreve aviso de
    // sintaxe legada no console a cada chamada, e este valor é usado em toda
    // criação de sala. Mudança no .prop passa a valer no próximo start.
    private static Long prazoEsperaMs;

    private static synchronized long getPrazoEsperaMs() {
        if (prazoEsperaMs == null) {
            try {
                prazoEsperaMs = Long.valueOf(FabConfigServicoComunicacao.SEGUNDOS_TIMEOUT_SALA_SOB_DEMANDA
                        .getValorParametroSistema()) * 1000;
            } catch (Throwable t) {
                prazoEsperaMs = 3000L;
            }
        }
        return prazoEsperaMs;
    }

    /**
     * Devolve a sala se ela ficar pronta dentro do prazo.
     *
     * @param pApelidoSala apelido canônico da sala, usado como chave de
     * deduplicação
     * @param pWaidContato telefone do contato, usado para responder
     * {@link #isSalaEmCriacaoParaContato(java.lang.String)}
     * @param pCriador o trabalho de criação
     * @throws ErroConexaoServicoChat quando a criação falha, ou quando o prazo
     * estoura antes de a sala ficar pronta. Nesse segundo caso a criação
     * continua rodando, e
     * {@link #isSalaEmCriacaoParaContato(java.lang.String)} responde true.
     */
    public static ComoChatSalaBean getSalaComPrazo(final String pApelidoSala, final String pWaidContato,
            final CriadorDeSala pCriador) throws ErroConexaoServicoChat {

        final long inicio = System.currentTimeMillis();

        CompletableFuture<ComoChatSalaBean> criacao = SALAS_EM_CRIACAO.computeIfAbsent(pApelidoSala,
                new java.util.function.Function<String, CompletableFuture<ComoChatSalaBean>>() {

            @Override
            public CompletableFuture<ComoChatSalaBean> apply(final String pApelido) {
                final CompletableFuture<ComoChatSalaBean> futuro = new CompletableFuture<>();
                marcarEmCriacao(pWaidContato, pApelido);
                EXECUTOR.execute(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            futuro.complete(pCriador.criar());
                        } catch (Throwable falha) {
                            // completeExceptionally aceita Throwable, então a
                            // causa original chega intacta em quem espera.
                            futuro.completeExceptionally(falha);
                        } finally {
                            SALAS_EM_CRIACAO.remove(pApelido);
                            desmarcarEmCriacao(pWaidContato, pApelido);
                        }
                    }
                });
                return futuro;
            }
        });

        try {
            ComoChatSalaBean sala = criacao.get(getPrazoEsperaMs(), TimeUnit.MILLISECONDS);
            log(FabMensagens.AVISO, "Sala " + pApelidoSala + " disponível em "
                    + (System.currentTimeMillis() - inicio) + "ms para o contato " + pWaidContato);
            return sala;

        } catch (TimeoutException prazoEstourado) {
            // Deliberadamente não cancela: a criação continua em background e
            // aquece o cache de salas para a próxima tentativa.
            log(FabMensagens.ALERTA, "Prazo de " + getPrazoEsperaMs() + "ms esgotado esperando a sala "
                    + pApelidoSala + " do contato " + pWaidContato
                    + ". A criação continua em background e a entrega vai para a fila.");
            throw new ErroConexaoServicoChat("A sala " + pApelidoSala + " ainda está sendo criada");

        } catch (InterruptedException interrompido) {
            Thread.currentThread().interrupt();
            throw new ErroConexaoServicoChat("Espera pela sala " + pApelidoSala + " interrompida");

        } catch (ExecutionException falhaNaCriacao) {
            Throwable causa = falhaNaCriacao.getCause() == null ? falhaNaCriacao : falhaNaCriacao.getCause();
            log(FabMensagens.ERRO, "Falha criando a sala " + pApelidoSala + " do contato " + pWaidContato
                    + " após " + (System.currentTimeMillis() - inicio) + "ms. erro="
                    + causa.getClass().getSimpleName() + ": " + causa.getMessage());
            if (causa instanceof ErroConexaoServicoChat) {
                throw (ErroConexaoServicoChat) causa;
            }
            throw new ErroConexaoServicoChat("Falha criando a sala " + pApelidoSala + ": " + causa.getMessage());
        }
    }

    /**
     * Responde se existe criação de sala em andamento para o contato. É o que
     * distingue "ainda não ficou pronta" de "falhou de verdade" para quem
     * atende a requisição.
     *
     * A pergunta é feita ao estado do serviço, e não ao tipo da exceção,
     * porque as trilhas convertem qualquer falha em
     * ErroComDevolucaoMensagemUsuario antes de a exceção chegar à rota.
     */
    public static boolean isSalaEmCriacaoParaContato(String pWaidContato) {
        if (pWaidContato == null) {
            return false;
        }
        Set<String> apelidos = APELIDOS_EM_CRIACAO_POR_CONTATO.get(pWaidContato);
        return apelidos != null && !apelidos.isEmpty();
    }

    private static void marcarEmCriacao(String pWaidContato, String pApelidoSala) {
        if (pWaidContato == null) {
            return;
        }
        APELIDOS_EM_CRIACAO_POR_CONTATO
                .computeIfAbsent(pWaidContato, waid -> ConcurrentHashMap.newKeySet())
                .add(pApelidoSala);
    }

    private static void desmarcarEmCriacao(String pWaidContato, String pApelidoSala) {
        if (pWaidContato == null) {
            return;
        }
        Set<String> apelidos = APELIDOS_EM_CRIACAO_POR_CONTATO.get(pWaidContato);
        if (apelidos == null) {
            return;
        }
        apelidos.remove(pApelidoSala);
        if (apelidos.isEmpty()) {
            APELIDOS_EM_CRIACAO_POR_CONTATO.remove(pWaidContato);
        }
    }

}
