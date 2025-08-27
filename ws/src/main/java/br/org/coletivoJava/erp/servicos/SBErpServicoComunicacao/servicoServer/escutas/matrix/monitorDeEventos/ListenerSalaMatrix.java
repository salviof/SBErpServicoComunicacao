package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.FabTipoProcessamentoMatrix;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.evento.ProcessadorMtxEventoLeituraMatrix;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.mensagem.ProcessadorMtxMensagem;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.mensagem.ProcessadorMtxReacaoMensagem;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfProcessadorPacoteMatrixWhatsap;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroCriandoContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroMtxParalizacaoDeProcessamento;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.sessaoMatrix.listeners.EscutaSalaMatrixAbst;
import br.org.coletivoJava.fw.api.erp.chat.model.FabTipoPacoteDeAcaoMatrix;
import static br.org.coletivoJava.fw.api.erp.chat.model.FabTipoPacoteDeAcaoMatrix.ATUALIZACAO_MEMBROS;
import static br.org.coletivoJava.fw.api.erp.chat.model.FabTipoPacoteDeAcaoMatrix.DIGITANDO;
import static br.org.coletivoJava.fw.api.erp.chat.model.FabTipoPacoteDeAcaoMatrix.LEITURA;
import static br.org.coletivoJava.fw.api.erp.chat.model.FabTipoPacoteDeAcaoMatrix.MENSAGEM;
import static br.org.coletivoJava.fw.api.erp.chat.model.FabTipoPacoteDeAcaoMatrix.REACAO;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTransito;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtrorigemmatrix.CPMensagemTrOrigemMatrix;
import org.coletivojava.fw.api.tratamentoErros.FabErro;
import org.json.JSONArray;

/**
 *
 * @author salvio
 */
public class ListenerSalaMatrix extends EscutaSalaMatrixAbst {

    public ListenerSalaMatrix(ItfChatSalaBean pSala) {
        super(pSala);
    }

    private List<Contato> contatos = null;
    private EntityManager em;
    private MensagemTransito mensagemReferencia;
    private ItfUsuarioChat usuarioAtendimento;
    private String codigoAtendimento;

    @Override
    public synchronized boolean isElegivel(ItfEventoMatix pEvento) {
        if (!super.isElegivel(pEvento)) {
            return false;
        }

        try {
            codigoAtendimento = pEvento.getSender();

            if (codigoAtendimento == null) {
                return false;
            }

            if (AplicacaoWsChat.SERVICO_MATRIX.isUmUsuarioContato(codigoAtendimento)) {
                return false;
            }

            if (codigoAtendimento.equals(AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin().getCodigoUsuario())) {
                return false;
            }
            usuarioAtendimento = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(codigoAtendimento);
            switch (pEvento.getTipoEvento()) {

                case MENSAGEM:

                    break;
                case REACAO:
                    usuarioAtendimento = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(pEvento.getSender());
                    if (pEvento.getContent().has("m.relates_to")) {
                        String idMensagem = pEvento.getContent().getJSONObject("m.relates_to").getString("event_id");
                        mensagemReferencia = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getMensagemEnviadaPorWhatsappByRegistroMatrix(idMensagem);
                        if (mensagemReferencia == null) {
                            return false;
                        }
                    }
                    break;
                case DIGITANDO:
                    // pode ter mais de um usuário digitando, por isso o Matrix, pode enviar mais de um usuaário nesse evento
                    JSONArray usuariosDigitando = pEvento.getContent().getJSONArray("user_ids");
                    for (int i = 0; i < usuariosDigitando.length(); i++) {
                        String userId = usuariosDigitando.getString(i);
                        usuarioAtendimento = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(userId);
                        if (AplicacaoWsChat.SERVICO_MATRIX.isUmUsuarioAtendimento(usuarioAtendimento)) {
                            break;
                        }
                    }
                    break;
                case LEITURA:
                    System.out.println("LeituraMatrix, whatsapp não suporta aviso de leitura" + pEvento.getEvent_id());
                    break;
                case ATUALIZACAO_MEMBROS:
                    atualizarDtoSala();
                    break;

                default:
                    throw new AssertionError();
            }

        } catch (ErroConexaoServicoChat ex) {

            return false;
        }
        if (usuarioAtendimento == null) {
            return false;
        }
        if (AplicacaoWsChat.SERVICO_MATRIX.isUmUsuarioAtendimento(usuarioAtendimento)) {

            return true;
        } else {
            //retuornar falso apos testes
            return true;
        }
    }

    @Override
    public void inicioProcessamento(ItfEventoMatix pEvento) throws ErroMtxParalizacaoDeProcessamento {
        em = null;
        try {
            em = UtilSBPersistencia.getEntyManagerPadraoNovo();

        } catch (Throwable t) {
            throw new ErroMtxParalizacaoDeProcessamento("Banco de dados está fora do ar" + t.getMessage());
        }
        if (em == null) {
            throw new ErroMtxParalizacaoDeProcessamento("Banco de dados está fora do ar");
        }
        getContatos();
        switch (pEvento.getTipoEvento()) {

            case MENSAGEM:
                mensagemReferencia = (MensagemTrOrigemMatrix) UtilSBPersistencia.gerarConsultaDeEntidade(MensagemTrOrigemMatrix.class, em)
                        .addcondicaoCampoIgualA(CPMensagemTrOrigemMatrix.codigorecibomensagemmatrix, pEvento.getEvent_id()).getPrimeiroRegistro();
                if (mensagemReferencia == null) {

                    mensagemReferencia = new MensagemTrOrigemMatrix();
                    mensagemReferencia.setSalaCodigoMatrix(getSala().getCodigoChat());
                    ((MensagemTrOrigemMatrix) mensagemReferencia).setJsonMensagemOriginal(pEvento.getRaw().toString(4));
                }

                break;
            case DIGITANDO:
                break;
            case LEITURA:
                break;
            case ATUALIZACAO_MEMBROS:
                break;

            case REACAO:
                break;
            default:
                throw new AssertionError();
        }

    }

    @Override
    public void finalProcessamento(ItfEventoMatix pEvento) {

        if (em.getTransaction().isActive()) {
            UtilSBPersistencia.finzalizaTransacaoEFechaEM(em);
        } else {
            UtilSBPersistencia.fecharEM(em);
        }
        codigoAtendimento = null;
        usuarioAtendimento = null;
        mensagemReferencia = null;
    }

    @Override
    public void eventoReacao(ItfEventoMatix pEvento) {

        for (Contato contato : getContatos()) {
            ProcessadorMtxReacaoMensagem processador = (ProcessadorMtxReacaoMensagem) FabTipoProcessamentoMatrix
                    .getProcessadorMatrix(FabTipoPacoteDeAcaoMatrix.REACAO, pEvento,
                            getSala(), mensagemReferencia, contato, usuarioAtendimento);
            try {
                processador.processar();
            } catch (ErroComDevolucaoMensagemUsuario devolucao) {
                try {
                    // Devolve mensagem e ignora
                    AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(getSala(), devolucao.getMensagemRetorno());
                } catch (ErroConexaoServicoChat ex) {
                    try {
                        AplicacaoWsChat.SERVICO_MATRIX.enviarDirect(codigoAtendimento, "Falha enviando reação na sala " + getSala().getCodigoChat() + " ");
                    } catch (ErroConexaoServicoChat ex1) {
                        Logger.getLogger(ListenerSalaMatrix.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
                UtilSBPersistencia.mergeRegistro(mensagemReferencia);
            } catch (ErroFalhaEncaminhando
                    | ErroConexaoServicoChat
                    | ErroFalhaGerandoSalaAtendimento
                    | ErroFalhaGerandoUsuarioAtendimento ex) {
                System.out.println("Processamento de reação ignorado");
            }
        }
    }

    @Override
    public void eventoMensagem(ItfEventoMatix pEvento) {
        if (mensagemReferencia.getId() != null && mensagemReferencia.getId() > 0) {
            if (mensagemReferencia.getComoMensagemEmTransitoOrigemMtx().getEncaminhamentos() != null) {
                //Tem encamimnhamentos?
                if (mensagemReferencia.getComoMensagemEmTransitoOrigemMtx().getEncaminhamentos().stream().filter(ec -> ec.isFoiEnviadoPeloWhatsapp()).findFirst().isPresent()) {
                    System.out.println("mensagem já foi encamiinhada");
                    return;
                }
            }
        }

        if (contatos.isEmpty()) {

        }

        for (Contato contato : contatos) {

            ProcessadorMtxMensagem processador = (ProcessadorMtxMensagem) FabTipoProcessamentoMatrix.getProcessadorMatrix(FabTipoPacoteDeAcaoMatrix.MENSAGEM, pEvento, getSala(), mensagemReferencia, contato, usuarioAtendimento);
            {
                try {
                    processador.processar();
                    mensagemReferencia = UtilSBPersistencia.mergeRegistro(mensagemReferencia, em);
                } catch (ErroComDevolucaoMensagemUsuario devolucao) {
                    try {
                        // Devolve mensagem e ignora
                        String codigoEnvioWhatsapp = AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(getSala(), devolucao.getMensagemRetorno());
                    } catch (ErroConexaoServicoChat ex) {
                        try {
                            AplicacaoWsChat.SERVICO_MATRIX.enviarDirect(codigoAtendimento, "Falha enviando mensagem na sala " + getSala().getCodigoChat() + " ");
                        } catch (ErroConexaoServicoChat ex1) {
                            Logger.getLogger(ListenerSalaMatrix.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                    mensagemReferencia = UtilSBPersistencia.mergeRegistro(mensagemReferencia, em);
                } catch (ErroFalhaEncaminhando
                        | ErroConexaoServicoChat
                        | ErroFalhaGerandoSalaAtendimento
                        | ErroFalhaGerandoUsuarioAtendimento ex) {
                    try {
                        AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(getSala(), "Falha entregando mensagem");
                    } catch (ErroConexaoServicoChat ex1) {
                        Logger.getLogger(ListenerSalaMatrix.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
        }

    }

    @Override
    public void eventoLeitura(ItfEventoMatix pEvento) {
        ProcessadorMtxEventoLeituraMatrix processador = (ProcessadorMtxEventoLeituraMatrix) FabTipoProcessamentoMatrix.getProcessadorMatrix(FabTipoPacoteDeAcaoMatrix.LEITURA, pEvento, getSala(), null, null, usuarioAtendimento);
        try {
            processador.processar();
        } catch (ErroFalhaEncaminhando | ErroComDevolucaoMensagemUsuario | ErroFalhaGerandoSalaAtendimento | ErroFalhaGerandoUsuarioAtendimento | ErroConexaoServicoChat ex) {
            System.out.println("Falha processando evento de leitura do matrix");
        }

    }

    @Override
    public void eventoDigitando(ItfEventoMatix pEvento) {
        ItfProcessadorPacoteMatrixWhatsap processador = FabTipoProcessamentoMatrix.getProcessadorMatrix(FabTipoPacoteDeAcaoMatrix.DIGITANDO, pEvento, getSala(), null, null, usuarioAtendimento);
        try {
            processador.processar();
        } catch (Throwable t) {
            System.out.println("Falha processando evento digitando do matrix");
        }
    }

    private List<Contato> getContatos() {
        if (contatos == null) {
            contatos = new ArrayList<>();
            getSala().getUsuarios().stream()
                    .filter(usr -> AplicacaoWsChat.SERVICO_MATRIX.isUmUsuarioContato(usr))
                    .map(usr -> {
                        try {
                            return AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato(usr);
                        } catch (Throwable e) {
                            // Aqui você ignora o erro e retorna null
                            return null;
                        }
                    })
                    .filter(Objects::nonNull) // remove os nulls
                    .forEach(contatos::add);
        }
        if (contatos.isEmpty()) {
            ItfUsuarioChat contatoPrincipal = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getUsuarioWhatsappPricipalLeadBySala(getSala());
            if (contatoPrincipal != null) {
                try {
                    contatos.add(AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato(contatoPrincipal));
                } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat | ErroCriandoContato ex) {
                    Logger.getLogger(ListenerSalaMatrix.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return contatos;
    }

    @Override
    public ItfChatSalaBean atualizarDtoSala() {
        try {
            return AplicacaoWsChat.SERVICO_MATRIX.getSalaByCodigo(getSala().getCodigoChat());
        } catch (ErroConexaoServicoChat ex) {
            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha atualizando dta de sala ", ex);
            return null;
        }
    }

    @Override
    public boolean isSalaComAutoMonitoramento(String pNomeSAla) {
        try {
            return AplicacaoWsChat.getCentralLogicaProcesasmento().isSalaAutomonitoravel(pNomeSAla);
        } catch (ErroConexaoServicoChat ex) {
            return false;
        }
    }

}
