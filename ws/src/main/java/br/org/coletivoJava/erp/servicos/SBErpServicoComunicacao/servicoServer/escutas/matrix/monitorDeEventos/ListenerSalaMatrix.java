package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.FabTipoProcessamentoMatrix;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.evento.ProcessadorMtxEventoLeituraMatrix;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.mensagem.ProcessadorMtxMensagem;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.mensagem.ProcessadorMtxReacaoMensagem;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfProcessadorPacoteMatrixWhatsap;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.EscutaSalaMatrixAbst;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoPacoteDeAcaoMatrix;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoPacoteDeAcaoMatrix.ATUALIZACAO_MEMBROS;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoPacoteDeAcaoMatrix.DIGITANDO;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoPacoteDeAcaoMatrix.LEITURA;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoPacoteDeAcaoMatrix.MENSAGEM;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoPacoteDeAcaoMatrix.REACAO;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTransito;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;
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
    private FabTipoPacoteDeAcaoMatrix tipoEvento;

    @Override
    public synchronized boolean isElegivel(RoomEvent pEvento, FabTipoPacoteDeAcaoMatrix pTipoEvento) {

        tipoEvento = pTipoEvento;
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
            switch (pTipoEvento) {

                case MENSAGEM:

                case REACAO:
                    usuarioAtendimento = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(pEvento.getSender());
                    String idMensagem = pEvento.getContent().getJSONObject("m.relates_to").getString("event_id");
                    mensagemReferencia = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getMensagemEnviadaPorWhatsappByRegistroMatrix(idMensagem);
                    if (mensagemReferencia == null) {
                        return false;
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
                    System.out.println("oi");
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
    public void inicioProcessamento(RoomEvent pEvento, FabTipoPacoteDeAcaoMatrix pTipoEvento) {
        em = UtilSBPersistencia.getEntyManagerPadraoNovo();
        getContatos();
        switch (tipoEvento) {

            case MENSAGEM:
                mensagemReferencia = (MensagemTrOrigemMatrix) UtilSBPersistencia.gerarConsultaDeEntidade(MensagemTrOrigemMatrix.class, em)
                        .addcondicaoCampoIgualA(CPMensagemTrOrigemMatrix.codigorecibomensagemmatrix, pEvento.getEvent_id()).getPrimeiroRegistro();
                if (tipoEvento.equals(FabTipoPacoteDeAcaoMatrix.MENSAGEM)) {
                    mensagemReferencia = new MensagemTrOrigemMatrix();
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
    public void finalProcessamento(RoomEvent pEvento, FabTipoPacoteDeAcaoMatrix pTipoEvento) {
        codigoAtendimento = null;
        usuarioAtendimento = null;
        mensagemReferencia = null;
        if (em.isJoinedToTransaction()) {
            UtilSBPersistencia.finalizarTransacao(em);
        }
        UtilSBPersistencia.fecharEM(em);
    }

    @Override
    public void eventoReacao(RoomEvent pEvento) {

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
    public void eventoMensagem(RoomEvent pEvento) {
        if (mensagemReferencia.getId() > 0) {
            if (!mensagemReferencia.getComoMensagemEmTransitoOrigemMtx().getEncaminhamentos().stream().filter(ec -> ec.getReciboEntregaWtzp() == null).findFirst().isPresent()) {
                throw new UnsupportedOperationException("Mensagem já foi encaminhada");
            }
        }

        for (Contato contato : contatos) {

            ProcessadorMtxMensagem processador = (ProcessadorMtxMensagem) FabTipoProcessamentoMatrix.getProcessadorMatrix(FabTipoPacoteDeAcaoMatrix.MENSAGEM, pEvento, getSala(), mensagemReferencia, contato, usuarioAtendimento);
            {
                try {
                    processador.processar();
                    UtilSBPersistencia.mergeRegistro(mensagemReferencia);
                } catch (ErroComDevolucaoMensagemUsuario devolucao) {
                    try {
                        // Devolve mensagem e ignora
                        AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(getSala(), devolucao.getMensagemRetorno());
                    } catch (ErroConexaoServicoChat ex) {
                        try {
                            AplicacaoWsChat.SERVICO_MATRIX.enviarDirect(codigoAtendimento, "Falha enviando mensagem na sala " + getSala().getCodigoChat() + " ");
                        } catch (ErroConexaoServicoChat ex1) {
                            Logger.getLogger(ListenerSalaMatrix.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                    UtilSBPersistencia.mergeRegistro(mensagemReferencia);
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
    public void eventoLeitura(RoomEvent pEvento) {
        ProcessadorMtxEventoLeituraMatrix processador = (ProcessadorMtxEventoLeituraMatrix) FabTipoProcessamentoMatrix.getProcessadorMatrix(FabTipoPacoteDeAcaoMatrix.LEITURA, pEvento, getSala(), null, null, usuarioAtendimento);
        try {
            processador.processar();
        } catch (ErroFalhaEncaminhando | ErroComDevolucaoMensagemUsuario | ErroFalhaGerandoSalaAtendimento | ErroFalhaGerandoUsuarioAtendimento | ErroConexaoServicoChat ex) {
            System.out.println("Falha processando evento de leitura do matrix");
        }

    }

    @Override
    public void eventoDigitando(RoomEvent pEvento) {
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

}
