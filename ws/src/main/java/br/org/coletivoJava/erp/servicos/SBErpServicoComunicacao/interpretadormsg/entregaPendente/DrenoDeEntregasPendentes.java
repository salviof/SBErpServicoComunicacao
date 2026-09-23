package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.entregaPendente;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config.FabConfigServicoComunicacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.whatsapp.mensagem.ProcessadorWtzpMsg;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.entregaPendente.FilaDeEntregaPendente.MensagemPendente;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoClient.UtilServicoAdministrativo;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.CarameloCode;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * Entrega as mensagens que ficaram na fila esperando a sala do contato.
 *
 * A fila vem do banco a cada ciclo, então um restart no meio do caminho não
 * perde nada: o ciclo seguinte reencontra as pendentes. Dentro de cada contato
 * a ordem de chegada é respeitada, e o contato para na primeira mensagem que
 * não conseguir entregar - para não trocar a ordem da conversa.
 *
 * A entrega é at-least-once por escolha: envia para a sala primeiro, marca
 * depois. O risco de repetir é coberto pelo id da mensagem do WhatsApp, que
 * viaja como txnId do Matrix e é deduplicado pelo Synapse; o risco inverso,
 * marcar antes e morrer, perderia a mensagem em silêncio.
 *
 * @author salvio
 */
public class DrenoDeEntregasPendentes extends Thread {

    private static final String TAG_LOG = "[DRENO-ENTREGA]";

    private static DrenoDeEntregasPendentes drenoAtivo;

    private DrenoDeEntregasPendentes() {
        super("dreno-entrega-pendente");
        setDaemon(true);
    }

    /**
     * A instrumentação nunca pode derrubar o dreno, por isso o serviço de log
     * é chamado dentro de um try.
     */
    private static void log(FabMensagens pTipo, String pMensagem) {
        try {
            CarameloCode.getServicoLogEventos().registrarLogDeEvento(pTipo, TAG_LOG + " " + pMensagem);
        } catch (Throwable t) {
            System.out.println(TAG_LOG + " " + pTipo + " " + pMensagem);
        }
    }

    private static boolean isDrenoAtivoNaConfig() {
        try {
            return Boolean.valueOf(FabConfigServicoComunicacao.DRENO_ENTREGA_PENDENTE_ATIVO
                    .getValorParametroSistema());
        } catch (Throwable t) {
            return true;
        }
    }

    // Lido uma única vez: a leitura de parâmetro de módulo escreve aviso de
    // sintaxe legada no console a cada chamada, e este valor é usado a cada
    // ciclo. Mudança no .prop passa a valer no próximo start.
    private static Long intervaloCicloMs;

    private static synchronized long getIntervaloCicloMs() {
        if (intervaloCicloMs == null) {
            try {
                intervaloCicloMs = Long.valueOf(FabConfigServicoComunicacao.SEGUNDOS_INTERVALO_DRENO_ENTREGA
                        .getValorParametroSistema()) * 1000;
            } catch (Throwable t) {
                intervaloCicloMs = 15000L;
            }
        }
        return intervaloCicloMs;
    }

    /**
     * Sobe o dreno. Nunca lança: uma falha aqui não pode impedir a aplicação
     * de iniciar.
     */
    public static synchronized void iniciar() {
        try {
            FilaDeEntregaPendente.recarregarIndice();

            if (!isDrenoAtivoNaConfig()) {
                log(FabMensagens.ALERTA, "Dreno desligado por configuração neste processo"
                        + " (DRENO_ENTREGA_PENDENTE_ATIVO). As mensagens continuam sendo enfileiradas,"
                        + " mas quem entrega é a outra instância.");
                return;
            }
            if (drenoAtivo != null && drenoAtivo.isAlive()) {
                return;
            }
            drenoAtivo = new DrenoDeEntregasPendentes();
            drenoAtivo.start();
            log(FabMensagens.AVISO, "Dreno de entregas pendentes iniciado, ciclo de "
                    + getIntervaloCicloMs() + "ms.");
        } catch (Throwable t) {
            log(FabMensagens.ERRO, "Falha iniciando o dreno de entregas pendentes: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(getIntervaloCicloMs());
            } catch (InterruptedException ex) {
                log(FabMensagens.ALERTA, "Dreno interrompido; encerrando a thread.");
                return;
            }
            try {
                executarCiclo();
            } catch (Throwable t) {
                // O dreno nunca pode morrer por causa de um ciclo ruim.
                log(FabMensagens.ERRO, "Falha no ciclo do dreno: "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
    }

    private void executarCiclo() {
        encerrarComPrazoVencido();

        Map<String, List<MensagemPendente>> porContato = FilaDeEntregaPendente.getPendentesPorContato();
        if (porContato.isEmpty()) {
            return;
        }
        log(FabMensagens.AVISO, "Ciclo com " + porContato.size() + " contato(s) na fila.");

        for (Map.Entry<String, List<MensagemPendente>> filaDoContato : porContato.entrySet()) {
            String waid = filaDoContato.getKey();
            boolean primeiraEntregaDoContato = true;
            try {
                for (MensagemPendente pendente : filaDoContato.getValue()) {
                    if (!entregar(pendente, waid, primeiraEntregaDoContato)) {
                        // Não entregou: para neste contato para não inverter a
                        // ordem das mensagens dele. Tenta tudo no próximo ciclo.
                        break;
                    }
                    primeiraEntregaDoContato = false;
                }
                if (!primeiraEntregaDoContato) {
                    FilaDeEntregaPendente.liberarContato(waid);
                }
            } catch (Throwable t) {
                log(FabMensagens.ERRO, "Falha drenando a fila do contato " + waid + ": "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
    }

    /**
     * Reexecuta o mesmo processador da recepção normal: a trilha decide a rota
     * de novo e, com a sala já criada, a mensagem segue pelo caminho comum.
     *
     * @return true se a mensagem foi entregue e encerrada
     */
    private boolean entregar(MensagemPendente pPendente, String pWaid, boolean pAvisarConectado) {
        EntityManager em = UtilSBPersistencia.getEMPadraoNovo();
        try {
            UtilSBPersistencia.iniciarTransacao(em);
            try {
                ProcessadorWtzpMsg processador
                        = new ProcessadorWtzpMsg(pPendente.getMensagem(), pPendente.getRegistro());
                processador.processar();
            } catch (Throwable falhaNoProcessamento) {
                // Se a resposta já chegou ao contato antes da falha, a mensagem
                // tem de ser encerrada mesmo assim: reprocessar repetiria a
                // resposta no próximo ciclo.
                if (!pPendente.getRegistro().isEncaminhado()) {
                    log(FabMensagens.AVISO, "Mensagem " + pPendente.getRegistro().getCodigoRegistroMensagemWhatsapp()
                            + " do contato " + pWaid + " continua na fila. motivo="
                            + falhaNoProcessamento.getClass().getSimpleName() + ": " + falhaNoProcessamento.getMessage());
                    return false;
                }
                log(FabMensagens.ALERTA, "Mensagem " + pPendente.getRegistro().getCodigoRegistroMensagemWhatsapp()
                        + " do contato " + pWaid + " respondeu ao contato e falhou em seguida;"
                        + " encerrando para não repetir a resposta. motivo="
                        + falhaNoProcessamento.getClass().getSimpleName() + ": " + falhaNoProcessamento.getMessage());
            }

            // Processou sem exceção: a mensagem está tratada, exatamente como na
            // recepção normal, que responde 200 nesse caso. Manter na fila aqui
            // trancaria o contato, porque as mensagens seguintes dele ficam
            // atrás desta para preservar a ordem.
            if (pPendente.getRegistro().getCodigoEncaminhamentoMatrix() == null) {
                log(FabMensagens.ALERTA, "Mensagem " + pPendente.getRegistro().getCodigoRegistroMensagemWhatsapp()
                        + " do contato " + pWaid + " foi processada sem recibo de encaminhamento."
                        + " A rota foi despachada, mas nada chegou na sala. A mensagem será encerrada"
                        + " para não travar a fila do contato - ver o erro registrado pela rota.");
            }

            FilaDeEntregaPendente.concluir(pPendente.getRegistro());
            log(FabMensagens.AVISO, "Mensagem " + pPendente.getRegistro().getCodigoRegistroMensagemWhatsapp()
                    + " do contato " + pWaid + " encerrada pelo dreno. reciboMatrix="
                    + pPendente.getRegistro().getCodigoEncaminhamentoMatrix());

            // O "pronto, você está conectado" só faz sentido se a mensagem
            // realmente chegou na sala do atendimento.
            if (pAvisarConectado && pPendente.getRegistro().getCodigoEncaminhamentoMatrix() != null) {
                AvisoDeEsperaContato.avisarConectado(pPendente.getMensagem());
            }
            return true;

        } finally {
            UtilSBPersistencia.finzalizaTransacaoEFechaEM(em);
        }
    }

    /**
     * Mensagens que passaram do prazo: avisa o contato e encerra, para não
     * ressuscitarem depois - inclusive as que venceram enquanto o serviço
     * estava fora do ar.
     */
    private void encerrarComPrazoVencido() {
        for (MensagemPendente vencida : FilaDeEntregaPendente.getPendentesComPrazoVencido()) {
            try {
                String waid = FilaDeEntregaPendente.getWaid(vencida.getMensagem());
                log(FabMensagens.ERRO, "Prazo de entrega vencido para a mensagem "
                        + vencida.getRegistro().getCodigoRegistroMensagemWhatsapp()
                        + " do contato " + waid + ". O contato será avisado e a mensagem encerrada.");

                if (vencida.getMensagem() != null) {
                    AvisoDeEsperaContato.avisarFalhaDefinitiva(vencida.getMensagem());
                }
                FilaDeEntregaPendente.concluir(vencida.getRegistro());
                FilaDeEntregaPendente.liberarContato(waid);

                UtilServicoAdministrativo.notificarAdmiministrador("ATENÇÃO! Mensagem do whatsapp não entregue no prazo."
                        + " Contato: " + waid
                        + " mensagem: " + vencida.getRegistro().getCodigoRegistroMensagemWhatsapp());
            } catch (Throwable t) {
                log(FabMensagens.ERRO, "Falha encerrando mensagem com prazo vencido: "
                        + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
    }

}
