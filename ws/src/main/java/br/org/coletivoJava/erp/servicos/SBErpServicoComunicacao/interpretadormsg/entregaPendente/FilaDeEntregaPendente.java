package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.entregaPendente;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config.FabConfigServicoComunicacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.CarameloCode;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCDataHora;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fila das mensagens do WhatsApp que chegaram enquanto a sala do contato ainda
 * estava sendo criada.
 *
 * A fila é o próprio registro de trânsito no banco (encaminhado = false e
 * prazo não vencido), por isso ela sobrevive a restart. O mapa em memória é
 * apenas um índice de quem está aguardando, para a requisição não precisar
 * varrer o banco a cada mensagem; ele é reconstruído a partir do banco no
 * start do serviço.
 *
 * @author salvio
 */
public class FilaDeEntregaPendente {

    private static final String TAG_LOG = "[FILA-ENTREGA]";

    private static final Map<String, Date> CONTATOS_AGUARDANDO = new ConcurrentHashMap<>();

    /**
     * A instrumentação nunca pode interromper o fluxo da mensagem, por isso o
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
    // sintaxe legada no console a cada chamada, e este valor é usado a cada
    // mensagem. Mudança no .prop passa a valer no próximo start.
    private static Integer minutosPrazoEntrega;

    private static synchronized int getMinutosPrazoEntrega() {
        if (minutosPrazoEntrega == null) {
            try {
                minutosPrazoEntrega = Integer.valueOf(FabConfigServicoComunicacao.MINUTOS_PRAZO_ENTREGA_PENDENTE
                        .getValorParametroSistema());
            } catch (Throwable t) {
                minutosPrazoEntrega = 10;
            }
        }
        return minutosPrazoEntrega;
    }

    public static boolean isContatoAguardando(String pWaidContato) {
        return pWaidContato != null && CONTATOS_AGUARDANDO.containsKey(pWaidContato);
    }

    /**
     * Coloca a mensagem na fila: define o prazo de entrega e marca o contato
     * como aguardando. Não persiste - quem chama é responsável pelo merge, para
     * respeitar a transação da requisição.
     */
    public static void enfileirar(MensagemTrOrigemWhatsapp pRegistro, MensagemWhatsapp pMensagem) {
        Date prazoNovo = UtilCRCDataHora.incrementaMinutos(new Date(), getMinutosPrazoEntrega());
        // Só encurta, nunca estende. O registro nasce com o prazo padrão de 5
        // dias do modelo, que precisa ser encurtado; já uma reentrega do mesmo
        // pacote pelo WhatsApp cai aqui de novo e não pode empurrar o
        // vencimento para frente - nem ressuscitar um prazo já vencido.
        if (pRegistro.getDaHoraExpirar() == null || pRegistro.getDaHoraExpirar().after(prazoNovo)) {
            pRegistro.setDaHoraExpirar(prazoNovo);
        }
        if (pMensagem != null) {
            if (pMensagem.getEntrada() != null) {
                pRegistro.setEntradaIdentificadorWhatsapp(pMensagem.getEntrada().getCodigo());
            }
            String waid = getWaid(pMensagem);
            pRegistro.setNome("Aguardando sala: " + waid + " " + pRegistro.getCodigoRegistroMensagemWhatsapp());
            if (waid != null) {
                CONTATOS_AGUARDANDO.put(waid, new Date());
            }
        }
    }

    /**
     * Encerra o ciclo da mensagem: ela não volta na fila nem é reprocessada se
     * o WhatsApp reentregar o pacote.
     */
    public static void concluir(MensagemTrOrigemWhatsapp pRegistro) {
        pRegistro.setEncaminhado(true);
        UtilSBPersistencia.mergeRegistro(pRegistro);
    }

    /**
     * Tira o contato do índice de espera. Chamado pelo dreno quando a fila do
     * contato foi esvaziada, para o próximo atendimento voltar a receber o
     * aviso de espera se precisar.
     */
    public static void liberarContato(String pWaidContato) {
        if (pWaidContato != null) {
            CONTATOS_AGUARDANDO.remove(pWaidContato);
        }
    }

    /**
     * Reconstrói a mensagem a partir do envelope do webhook guardado no
     * registro. É o que permite ao dreno retomar a entrega depois de um
     * restart, sem depender de nada em memória.
     *
     * Um pacote pode trazer várias mensagens e cada registro guarda o pacote
     * inteiro, por isso a seleção pelo id da mensagem.
     */
    public static MensagemWhatsapp reconstruirMensagem(MensagemTrOrigemWhatsapp pRegistro) {
        if (pRegistro.getCorpoJsonRecebido() == null || pRegistro.getCodigoRegistroMensagemWhatsapp() == null) {
            return null;
        }
        try {
            PacoteMemensagemRecebidoWhatsapp pacote
                    = new PacoteMemensagemRecebidoWhatsapp(pRegistro.getCorpoJsonRecebido());
            for (MensagemWhatsapp mensagem : pacote.getMensagens()) {
                if (pRegistro.getCodigoRegistroMensagemWhatsapp().equals(mensagem.getId())) {
                    return mensagem;
                }
            }
            log(FabMensagens.ALERTA, "O pacote guardado no registro " + pRegistro.getId()
                    + " não contém a mensagem " + pRegistro.getCodigoRegistroMensagemWhatsapp());
            return null;
        } catch (Throwable t) {
            log(FabMensagens.ERRO, "Falha reconstruindo a mensagem "
                    + pRegistro.getCodigoRegistroMensagemWhatsapp() + " do registro " + pRegistro.getId()
                    + ". erro=" + t.getClass().getSimpleName() + ": " + t.getMessage());
            return null;
        }
    }

    public static String getWaid(MensagemWhatsapp pMensagem) {
        if (pMensagem == null || pMensagem.getContatoOrigem() == null) {
            return null;
        }
        return pMensagem.getContatoOrigem().getWa_id();
    }

    /**
     * Pendentes no prazo, agrupadas por contato e preservando a ordem de
     * chegada dentro de cada contato.
     */
    public static Map<String, List<MensagemPendente>> getPendentesPorContato() {
        Date inicioDaVarredura = new Date();
        Map<String, List<MensagemPendente>> porContato = new LinkedHashMap<>();
        for (MensagemTrOrigemWhatsapp registro : AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT
                .getMensagensPendentesDeEntrega(getPrazoMaximoDaFila(), getCriacaoMaisAntigaAceita())) {
            MensagemWhatsapp mensagem = reconstruirMensagem(registro);
            String waid = getWaid(mensagem);
            if (mensagem == null || waid == null) {
                // Registro que não dá para reprocessar: encerra para não ficar
                // preso na fila para sempre.
                concluir(registro);
                continue;
            }
            List<MensagemPendente> doContato = porContato.get(waid);
            if (doContato == null) {
                doContato = new ArrayList<>();
                porContato.put(waid, doContato);
            }
            doContato.add(new MensagemPendente(registro, mensagem));
            CONTATOS_AGUARDANDO.put(waid, registro.getDataHoraCriacao() == null
                    ? new Date() : registro.getDataHoraCriacao());
        }
        removerDoIndiceQuemNaoTemMaisPendente(porContato.keySet(), inicioDaVarredura);
        return porContato;
    }

    /**
     * Tira do índice quem não apareceu na varredura. Sem isso, um contato
     * marcado como aguardando cujas mensagens já foram encerradas por outro
     * caminho ficaria preso no índice, e toda mensagem dele iria para a fila em
     * vez de ser atendida na hora.
     *
     * Entradas criadas depois do início da varredura são preservadas: a
     * requisição marca o índice antes de o registro ser persistido, então
     * remover seria uma corrida com quem acabou de enfileirar.
     */
    private static void removerDoIndiceQuemNaoTemMaisPendente(java.util.Set<String> pContatosComPendente,
            Date pInicioDaVarredura) {
        for (Map.Entry<String, Date> aguardando : CONTATOS_AGUARDANDO.entrySet()) {
            if (pContatosComPendente.contains(aguardando.getKey())) {
                continue;
            }
            if (aguardando.getValue() != null && aguardando.getValue().after(pInicioDaVarredura)) {
                continue;
            }
            CONTATOS_AGUARDANDO.remove(aguardando.getKey());
        }
    }

    public static List<MensagemPendente> getPendentesComPrazoVencido() {
        List<MensagemPendente> vencidas = new ArrayList<>();
        for (MensagemTrOrigemWhatsapp registro : AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT
                .getMensagensPendentesComPrazoVencido(getCriacaoMaisAntigaAceita())) {
            vencidas.add(new MensagemPendente(registro, reconstruirMensagem(registro)));
        }
        return vencidas;
    }

    /**
     * Prazo máximo que um registro da fila pode ter. É o que distingue a
     * mensagem enfileirada do registro que nasceu com o prazo padrão de 5 dias
     * do modelo. A folga de um minuto cobre a diferença de relógio entre o
     * momento do enfileiramento e o da consulta.
     */
    private static Date getPrazoMaximoDaFila() {
        return UtilCRCDataHora.incrementaMinutos(new Date(), getMinutosPrazoEntrega() + 1);
    }

    /**
     * Idade máxima de um registro para ser considerado da fila. Cobre o serviço
     * ter ficado fora do ar por até uma hora; registros mais antigos que isso
     * ficam de fora, porque avisar o contato sobre uma mensagem daquele tempo
     * atrapalharia mais do que ajudaria.
     */
    private static Date getCriacaoMaisAntigaAceita() {
        return UtilCRCDataHora.incrementaHoras(new Date(), -1);
    }

    /**
     * Reconstrói o índice em memória a partir do banco. Chamado no start do
     * serviço: sem isso, uma mensagem que ficou na fila antes do restart não
     * seria reconhecida como espera e o contato receberia o aviso de novo.
     */
    public static void recarregarIndice() {
        try {
            CONTATOS_AGUARDANDO.clear();
            int total = 0;
            for (Map.Entry<String, List<MensagemPendente>> porContato : getPendentesPorContato().entrySet()) {
                total = total + porContato.getValue().size();
            }
            log(FabMensagens.AVISO, "Índice de espera recarregado do banco: "
                    + CONTATOS_AGUARDANDO.size() + " contato(s) aguardando, " + total + " mensagem(ns) na fila.");
        } catch (Throwable t) {
            log(FabMensagens.ERRO, "Falha recarregando o índice de espera: "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    /**
     * Registro da fila junto com a mensagem reconstruída.
     */
    public static class MensagemPendente {

        private final MensagemTrOrigemWhatsapp registro;
        private final MensagemWhatsapp mensagem;

        public MensagemPendente(MensagemTrOrigemWhatsapp pRegistro, MensagemWhatsapp pMensagem) {
            registro = pRegistro;
            mensagem = pMensagem;
        }

        public MensagemTrOrigemWhatsapp getRegistro() {
            return registro;
        }

        public MensagemWhatsapp getMensagem() {
            return mensagem;
        }

    }

}
