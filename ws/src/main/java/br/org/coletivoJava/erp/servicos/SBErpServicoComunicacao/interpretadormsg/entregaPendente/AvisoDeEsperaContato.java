package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.entregaPendente;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import com.super_bits.modulosSB.SBCore.ConfigGeral.CarameloCode;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;

/**
 * Mensagens que o contato recebe enquanto a sala dele está sendo criada.
 *
 * São enviadas no máximo uma vez por janela de espera: o aviso de espera sai
 * quando a mensagem abre a fila do contato, e o de conexão concluída só sai se
 * houve espera.
 *
 * @author salvio
 */
public class AvisoDeEsperaContato {

    private static final String TAG_LOG = "[AVISO-ESPERA]";

    /**
     * A instrumentação nunca pode interromper o fluxo, por isso o serviço de
     * log é chamado dentro de um try.
     */
    private static void log(FabMensagens pTipo, String pMensagem) {
        try {
            CarameloCode.getServicoLogEventos().registrarLogDeEvento(pTipo, TAG_LOG + " " + pMensagem);
        } catch (Throwable t) {
            System.out.println(TAG_LOG + " " + pTipo + " " + pMensagem);
        }
    }

    private static boolean enviar(MensagemWhatsapp pMensagem, String pTexto, String pDescricaoAviso) {
        String waid = FilaDeEntregaPendente.getWaid(pMensagem);
        if (pMensagem == null || pMensagem.getEntrada() == null || waid == null) {
            log(FabMensagens.ALERTA, "Não foi possível enviar o aviso de " + pDescricaoAviso
                    + ": dados de origem da mensagem incompletos.");
            return false;
        }
        try {
            String recibo = AplicacaoWsChat.SERVICO_WHATSAPP.enviarMensagemTexto(pMensagem.getEntrada(), waid, pTexto);
            if (recibo == null) {
                log(FabMensagens.ERRO, "Aviso de " + pDescricaoAviso + " para " + waid
                        + " não recebeu recibo do WhatsApp.");
                return false;
            }
            log(FabMensagens.AVISO, "Aviso de " + pDescricaoAviso + " enviado para " + waid + ". recibo=" + recibo);
            return true;
        } catch (Throwable t) {
            log(FabMensagens.ERRO, "Falha enviando aviso de " + pDescricaoAviso + " para " + waid
                    + ". erro=" + t.getClass().getSimpleName() + ": " + t.getMessage());
            return false;
        }
    }

    public static boolean avisarEspera(MensagemWhatsapp pMensagem) {
        return enviar(pMensagem,
                "Recebi sua mensagem! Aguarde um instante, estamos te conectando com nossos consultores.",
                "espera");
    }

    public static boolean avisarConectado(MensagemWhatsapp pMensagem) {
        return enviar(pMensagem,
                "Pronto, você está conectado! Sua mensagem já foi encaminhada para nossa equipe.",
                "conexão concluída");
    }

    public static boolean avisarFalhaDefinitiva(MensagemWhatsapp pMensagem) {
        return enviar(pMensagem,
                "Não conseguimos entregar sua mensagem para nossa equipe agora."
                + " Por favor, tente novamente em alguns minutos ou ligue para este mesmo número.",
                "falha definitiva");
    }

}
