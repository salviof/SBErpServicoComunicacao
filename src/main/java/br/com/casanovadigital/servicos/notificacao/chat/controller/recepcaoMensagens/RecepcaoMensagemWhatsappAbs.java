package br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens;

import br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens.MotorControleDeMensagens;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.contextoChat.FabTipoContexto;
import br.com.casanovadigital.servicos.notificacao.contextoChat.matrix.ContextoMatrix;
import br.com.casanovadigital.servicos.notificacao.contextoChat.whatsapp.ContextoWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreDataHora;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivojava.fw.api.tratamentoErros.FabErro;

/**
 *
 * @author salvio
 */
public abstract class RecepcaoMensagemWhatsappAbs extends Thread implements ItfExecucaoLogicaRecepcaoWtzap {

    private final MensagemWhatsapp mensagem;
    private final ContextoWhatsapp contextoWhatsapp;
    private String codigoReciboEncaminhamento;
    private final FabTipoContexto tipoContexto;
    private Date dataHoraEvento;

    private boolean finalizada;
    private boolean encaminhada;
    private boolean lida;
    private int quantidadeTentativasEncaminhar;
    private final TimerFinalizacao timerFInal = new TimerFinalizacao();

    @Override
    public long getId() {
        return mensagem.getId().hashCode();
    }

    @Override
    public String toString() {
        return mensagem.getId();
    }

    @Override
    public boolean isFinalizada() {
        return finalizada;
    }

    public RecepcaoMensagemWhatsappAbs(MensagemWhatsapp pMensagem, ContextoWhatsapp pContexto) {
        mensagem = pMensagem;
        contextoWhatsapp = pContexto;
        dataHoraEvento = new Date();
        tipoContexto = FabTipoContexto.USUARIO_WHATSAPP;
        timerFInal.start();
    }

    public abstract String processarEncaminhamentoMatrix() throws ErroFalhaEncaminhando;

    public abstract ItfChatSalaBean gerarSalaMatrix() throws ErroFalhaGerandoSalaAtendimento;

    public abstract ItfUsuarioChat gerarUsuarioAtendmento() throws ErroFalhaGerandoUsuarioAtendimento;

    public abstract void validarMensagem() throws ErroComDevolucaoMensagemUsuario, ErroFalhaEncaminhando;

    @Override
    public void declararfoiLida() {
        lida = true;
    }

    public void salvarMensagemPastaTemporaria(MensagemWhatsapp pMensagem) {

    }

    public void removerMensagemPastaTemporaria(MensagemWhatsapp pMensagem) {

    }

    @Override
    public synchronized void run() {
        if (contextoWhatsapp.getContextoMatrix() == null) {
            contextoWhatsapp.setContextoMatrix(new ContextoMatrix(contextoWhatsapp));
        }

        while (!encaminhada) {
            Date dataHoraDefinicaoSala = contextoWhatsapp.getDataHoraDefinicaoSala();

            try {
                validarMensagem();
                if (contextoWhatsapp.getSalaMatrixAtiva() == null || UtilSBCoreDataHora.intervaloTempoSegundos(dataHoraDefinicaoSala, new Date()) > 5) {
                    contextoWhatsapp.setSalaMatrixAtiva(gerarSalaMatrix());
                }

                codigoReciboEncaminhamento = processarEncaminhamentoMatrix();
            } catch (ErroComDevolucaoMensagemUsuario pDevolucao) {
                encaminhada = true;
                finalizada = true;
                getContextoWhatsapp().getContextoMatrix().encaminharMensagemParaWtsapp(getContextoWhatsapp().getContatoWhatsapp(), pDevolucao.getMensagemRetorno());
            } catch (ErroFalhaEncaminhando | ErroFalhaGerandoSalaAtendimento t) {
                if (quantidadeTentativasEncaminhar > 5) {
                    getContextoWhatsapp().getContextoMatrix().encaminharMensagemParaWtsapp(getContextoWhatsapp().getContatoWhatsapp(), "houve uma falha ");
                }

                SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha encaminhando mensagem para o Atendimento, por favor, entre em contato com o suporte", t);

                try {
                    sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RecepcaoMensagemWhatsappAbs.class.getName()).log(Level.SEVERE, null, ex);
                }

                quantidadeTentativasEncaminhar++;
            } catch (Throwable t) {
                SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha encaminhando mensagem para o [Matrix]", t);
                quantidadeTentativasEncaminhar++;
                try {
                    sleep(5000);
                } catch (InterruptedException ex) {
                    salvarMensagemPastaTemporaria(mensagem);
                }
            }

            //MotorControleDeMensagens.registrarReciboEntregaChat(codigoReciboEncaminhamento, getContextoWhatsapp().getCodigoContexto());
            if (!encaminhada) {

                encaminhada = codigoReciboEncaminhamento != null;
            }
        }

    }

    @Override
    public MensagemWhatsapp getMensagem() {
        return mensagem;
    }

    @Override
    public ContextoWhatsapp getContextoWhatsapp() {
        return contextoWhatsapp;
    }

    @Override
    public boolean isEncaminhada() {
        return encaminhada;
    }

    @Override
    public boolean isLida() {
        return lida;
    }

    public int getQuantidadeTentativasEncaminhar() {
        return quantidadeTentativasEncaminhar;
    }

    @Override
    public boolean registrarLeituraDaMensagemPorEquipeInternaMatrix(String pRegistroLeitura) {
        if (codigoReciboEncaminhamento != null) {

            if (pRegistroLeitura.equals(codigoReciboEncaminhamento)) {
                lida = true;
                finalizada = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public String getCodigoReciboEncaminhamento() {
        return codigoReciboEncaminhamento;
    }

    public class TimerFinalizacao extends Thread {

        private final Date dataHoraExpira;

        public TimerFinalizacao() {
            dataHoraExpira = UtilSBCoreDataHora.incrementaHoras(new Date(), 14);
        }

        @Override
        public void run() {

            while (!encaminhada) {
                try {
                    sleep(300);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RecepcaoMensagemWhatsappAbs.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (encaminhada) {
                MotorControleDeMensagens.getControleRecibo().registrarEventoEncaminhamentoWhatsappParaMatrix(contextoWhatsapp.getCodigoContexto(), mensagem, codigoReciboEncaminhamento);
            }

            while (!finalizada) {
                // keep doing what this thread should do.
                if (lida) {
                    finalizada = true;
                } else {
                    if (!finalizada) {
                        finalizada = dataHoraExpira.getTime() <= new Date().getTime();
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    finalizada = true;
                }

            }

        }

    }

}
