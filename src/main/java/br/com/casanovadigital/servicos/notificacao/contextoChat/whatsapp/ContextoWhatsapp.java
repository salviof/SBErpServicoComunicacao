package br.com.casanovadigital.servicos.notificacao.contextoChat.whatsapp;

import br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens.FabAcaoControleMensagemWtsappRecepcao;
import br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens.MotorControleDeMensagens;
import br.com.casanovadigital.servicos.notificacao.contextoChat.ItfContextoLoadContexto;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.contextoChat.ContextoChat;
import br.com.casanovadigital.servicos.notificacao.contextoChat.matrix.ContextoMatrix;
import br.com.casanovadigital.servicos.notificacao.contextoChat.FabTipoContexto;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.api.transmissao_recepcao_rest_client.ItfProcessadorRespostaPacote;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import java.util.Collections;
import br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens.ItfExecucaoLogicaRecepcaoWtzap;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreDataHora;
import com.super_bits.modulosSB.SBCore.modulos.tempo.FabTipoQuantidadeTempo;
import org.coletivojava.fw.api.tratamentoErros.FabErro;

/**
 *
 * @author salvio
 */
public class ContextoWhatsapp extends ContextoChat {

    private String codigoContexto;
    private EntradaNumeroWhatsapp entradaWhatsapp;
    private final ContatoWhatsapp contatoWhatsapp;
    private ProcessadorMensagens limpesaDeMensagens = new ProcessadorMensagens();

    private ItfChatSalaBean salaMatrixAtiva;

    private List<ItfExecucaoLogicaRecepcaoWtzap> mensagensRecebidas = Collections.synchronizedList(new ArrayList());

    private ContextoMatrix contextoMatrix;

    private ItfContextoLoadContexto leituraContexto;
    private Constructor<ItfProcessadorRespostaPacote> constructorProcessamentoMensagem;

    public ContextoWhatsapp(ContatoWhatsapp pContato, EntradaNumeroWhatsapp pEntrada, Class pClasseProcessarMensagemWhatsapp, Class pContextoLoadContexto) {
        super(FabTipoContexto.USUARIO_WHATSAPP);

        entradaWhatsapp = pEntrada;
        codigoContexto = MotorControleDeMensagens.gerarCodigoContextoWhatsapp(pEntrada, pContato);

        contatoWhatsapp = pContato;
        try {
            leituraContexto = (ItfContextoLoadContexto) pContextoLoadContexto.newInstance();
            dadosDeContexto = leituraContexto.gerarDadosDoCotexto(pContato);
            constructorProcessamentoMensagem = pClasseProcessarMensagemWhatsapp.getConstructor(MensagemWhatsapp.class, ContextoWhatsapp.class);

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(ContextoWhatsapp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ItfChatSalaBean getSalaMatrixAtiva() {
        if (salaMatrixAtiva == null) {
            //    salaMatrixAtiva=
        }
        return salaMatrixAtiva;
    }
    private Date dataHoraDefinicaoSala = new Date();

    public Date getDataHoraDefinicaoSala() {
        return dataHoraDefinicaoSala;
    }

    public void setSalaMatrixAtiva(ItfChatSalaBean salaMatrixAtiva) {

        dataHoraDefinicaoSala = new Date();
        this.salaMatrixAtiva = salaMatrixAtiva;

    }

    private void processarMensagem(String pCodigoReciboEncaminhamento) {

        processarMensagem(FabAcaoControleMensagemWtsappRecepcao.CONTROLE_MENSAGEM_REGISTRO_LEITURA, null, pCodigoReciboEncaminhamento);
    }

    private void processarMensagem(MensagemWhatsapp pMensagem) {
        processarMensagem(FabAcaoControleMensagemWtsappRecepcao.CONTROLE_MENSAGEM_ENCAMINHAR_MATRIX, pMensagem, null);
    }

    private synchronized void processarMensagem(FabAcaoControleMensagemWtsappRecepcao pTipoAlteracao, MensagemWhatsapp pMensagem, String pCodigoReciboEncaminhamento) {
        dataHoraUltimaMensagem = new Date();
        if (limpesaDeMensagens.isEmpause()) {
            limpesaDeMensagens = new ProcessadorMensagens();
            limpesaDeMensagens.start();
        }
        switch (pTipoAlteracao) {
            case CONTROLE_MENSAGEM_ENCAMINHAR_MATRIX:
                        try {
                if (mensagensRecebidas.size() > 10) {
                    mensagensRecebidas.remove(0);
                }
                ItfExecucaoLogicaRecepcaoWtzap msgProcess = (ItfExecucaoLogicaRecepcaoWtzap) constructorProcessamentoMensagem.newInstance(pMensagem, this);

                ((Thread) msgProcess).start();
                mensagensRecebidas.add(msgProcess);

            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "falha executando encaminhamento de mensagem whatsapp p/ Matrix" + pMensagem.getMensagem(), ex);
            }
            break;
            case CONTROLE_MENSAGEM_REGISTRO_LEITURA:
                for (ItfExecucaoLogicaRecepcaoWtzap mensagensEmProcessamento : mensagensRecebidas) {
                    if (!mensagensEmProcessamento.isLida() && mensagensEmProcessamento.isEncaminhada()) {

                        if (mensagensEmProcessamento.getCodigoReciboEncaminhamento().equals(pCodigoReciboEncaminhamento)) {
                            mensagensEmProcessamento.registrarLeituraDaMensagemPorEquipeInternaMatrix(pCodigoReciboEncaminhamento);
                        }
                    }
                }
                break;
            case CONTROLE_MENSAGEM_LIMPAR_MENSAGENS:

                List<ItfExecucaoLogicaRecepcaoWtzap> mensagensFinalizadas = new ArrayList<>();
                mensagensRecebidas.stream().filter(proc -> proc.isFinalizada()).forEach(mensagensFinalizadas::add);
                mensagensRecebidas.removeAll(mensagensFinalizadas);
                if (mensagensRecebidas.isEmpty()) {
                    limpesaDeMensagens.pausar();
                }
                break;
            case CONTROLE_MENSAGEM_SALVAR:
                break;
            default:
                throw new AssertionError();
        }

    }

    public synchronized void executarMensagem(MensagemWhatsapp pMensagem) {
        processarMensagem(pMensagem);
    }

    public synchronized void declararLeitura(String pcodigoReciboWhatsapp) {
        processarMensagem(pcodigoReciboWhatsapp);
    }

    public void setContextoMatrix(ContextoMatrix contextoMatrix) {
        this.contextoMatrix = contextoMatrix;
    }

    public ContextoMatrix getContextoMatrix() {
        return contextoMatrix;
    }

    public ContatoWhatsapp getContatoWhatsapp() {
        return contatoWhatsapp;
    }

    public String getCodigoContexto() {
        return codigoContexto;
    }

    public EntradaNumeroWhatsapp getEntradaWhatsapp() {
        return entradaWhatsapp;
    }

    public List<ItfExecucaoLogicaRecepcaoWtzap> getMensagensRecebidas() {
        return mensagensRecebidas;
    }

    public void atualizarDadosContexto() {
        dadosDeContexto = leituraContexto.gerarDadosDoCotexto(getContatoWhatsapp());
    }

    public class ProcessadorMensagens extends Thread {

        private boolean empause;

        public void pausar() {
            empause = true;
        }

        public boolean isEmpause() {
            return empause;
        }

        public void monitorar() {
            while (true && !empause) {
                // keep doing what this thread should do.

                try {
                    Thread.sleep(60000);

                    processarMensagem(FabAcaoControleMensagemWtsappRecepcao.CONTROLE_MENSAGEM_LIMPAR_MENSAGENS, null, null);
                } catch (InterruptedException e) {
                    empause = true;
                }

            }
        }

        @Override
        public void run() {
            monitorar();
        }

    }

}
