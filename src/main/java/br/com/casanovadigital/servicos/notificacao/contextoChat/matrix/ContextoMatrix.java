package br.com.casanovadigital.servicos.notificacao.contextoChat.matrix;

import br.com.casanovadigital.servicos.notificacao.chat.ServicoNotificacaoChat;
import br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens.MotorControleDeMensagens;
import br.com.casanovadigital.servicos.notificacao.chat.controller.notificacaoLeitura.EventoEncMatrixParaWtsap;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.statusMensagem.StatusMensagemWtzap;
import br.com.casanovadigital.servicos.notificacao.contextoChat.ContextoChat;
import br.com.casanovadigital.servicos.notificacao.contextoChat.FabTipoContexto;
import br.com.casanovadigital.servicos.notificacao.contextoChat.whatsapp.ContextoWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ERPChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.ChatMatrixOrgimpl;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.UsuarioChatMatrixOrg;
import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import br.org.coletivoJava.integracoes.restIntmatrixchat.UtilsbApiMatrixChat;
import br.org.coletivoJava.integracoes.restIntwhatsapp.implementacao.IntegracaoRestIntwhatsapp_HeaderPadrao;
import br.org.coletivoJava.integracoes.restIntwhatsapp.implementacao.UtilSBApiWhatsapp;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappMedia;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappMensagem;
import com.google.common.collect.Lists;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UTilSBCoreInputs;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringSlugs;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringsCammelCase;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.api.transmissao_recepcao_rest_client.ItfAcaoApiRest;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;
import jakarta.json.JsonValue;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivojava.fw.api.tratamentoErros.FabErro;
import org.json.JSONObject;

/**
 *
 * @author salvio
 */
public class ContextoMatrix extends ContextoChat {

    private final static ChatMatrixOrgimpl chatMatrixService = (ChatMatrixOrgimpl) ERPChat.MATRIX_ORG.getImplementacaoDoContexto();

    private ItfUsuarioChat usuarioSalaRelacionadoWtzap;
    private ContextoWhatsapp contextoWhatsapp;
    private List<EventoEncMatrixParaWtsap> ultimosEventosEncaminhado = Collections.synchronizedList(new ArrayList<>());

    public static ItfUsuarioChat getUsuarioChatRelacionado(ContatoWhatsapp pContatoWhatsap) {
        try {
            return chatMatrixService.getUsuarioByTelefone(pContatoWhatsap.getWa_id());
        } catch (ErroConexaoServicoChat ex) {
            return null;
        }
    }

    public static ItfUsuarioChat getUsuarioLeadMatrixCriandoSeNaoExistir(ContatoWhatsapp pcontato) throws ErroConexaoServicoChat {

        System.out.println("Pesquisando usuário pelo telefone " + pcontato.getWa_id());
        ChatMatrixOrgimpl erpChatService = (ChatMatrixOrgimpl) ContextoMatrix.chatMatrixService;
        ItfUsuarioChat usuario = erpChatService.getUsuarioByTelefone(pcontato.getWa_id());
        if (usuario == null) {

            UsuarioChatMatrixOrg usuariochat = new UsuarioChatMatrixOrg();
            usuariochat.setNome(pcontato.getNome());
            usuariochat.setTelefone(pcontato.getWa_id());
            String nomeOriginal = pcontato.getNome();
            String usuarioPart1 = UtilSBCoreStringsCammelCase.getCamelByTextoPrimeiraLetraMaiuscula(nomeOriginal);
            usuarioPart1 = UtilSBCoreStringSlugs.gerarSlugSimples(usuarioPart1);

            usuariochat.setApelido(usuarioPart1);
            String codigoUsuario = UtilsbApiMatrixChat.gerarCodigoBySlugUser(usuarioPart1 + pcontato.getWa_id());
            usuariochat.setCodigoUsuario(codigoUsuario);
            ItfUsuarioChat novoUsuario = erpChatService.usuarioCriar(usuariochat);
            usuario = novoUsuario;
        }

        return usuario;
    }

    public ContextoMatrix(ContextoWhatsapp pContextoWtzapp) {
        super(FabTipoContexto.SALA_MATRIX);
        contextoWhatsapp = pContextoWtzapp;

    }

    public ChatMatrixOrgimpl getServicoChat() {
        return chatMatrixService;
    }

    public ContextoWhatsapp getContextoWhatsapp() {
        return contextoWhatsapp;
    }
//{"event_id":"$id9CTIVpRGbvx6lFXWHUjaI-nB1-eMDaP7OS0SblNs4"}

    public boolean motitificarLeituraMatrixNoWtsapp(RoomEvent pEvento) {
        System.out.println(pEvento.getEvent_id());
        System.out.println(pEvento.getRaw());
        System.out.println(pEvento.getEvent_id());
        System.out.println(pEvento.getContent());
        //getServicoChat().salaNotificarLeitura(pSala, usuarioSalaRelacionadoWtzap, pCodigoReciboMatix);

        return true;
    }

    public boolean notificarLeituraWtsappNaSala(StatusMensagemWtzap msg) {

        ItfChatSalaBean sala = getContextoWhatsapp().getSalaMatrixAtiva();
        String reciboMatrix = MotorControleDeMensagens.getControleRecibo().getCodigoEventoMatrixByCodigoWhatsapp(msg.getCodigoMensagem());
        try {
            ItfUsuarioChat usuarioLeitura = getServicoChat().getUsuarioByTelefone(getContextoWhatsapp().getContatoWhatsapp().getWa_id());
            getServicoChat().salaNotificarLeitura(sala, usuarioLeitura, reciboMatrix);
            return true;
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(ContextoMatrix.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public String encaminharMensagemParaWtsapp(ContatoWhatsapp pContato, String pMensagem) {

        ItfRespostaWebServiceSimples resposta = FabApiRestIntWhatsappMensagem.MENSAGEM_ENVIAR.getAcao(getContextoWhatsapp().getContatoWhatsapp().getWa_id(),
                pMensagem).getResposta();
        if (resposta.isSucesso()) {
            JsonValue valor = resposta.getRespostaComoObjetoJson().getJsonArray("messages").stream().findFirst().get();
            String codReciboWhatsapp = valor.asJsonObject().getString("id");

            return codReciboWhatsapp;
        } else {
            return null;
        }

    }

    public String encaminharMensagemParaWtsapp(RoomEvent pEventoSala) {
        ItfChatSalaBean sala;
        try {
            sala = ContextoMatrix.getChatMatrixService().getSalaByCodigo(pEventoSala.getRoom_id());
            contextoWhatsapp.setSalaMatrixAtiva(sala);
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(ContextoMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }

        //JSONObject conteudo = pEventoSala.getContent();
        String texto = pEventoSala.getContent().getString("body");
        //Encaminhar para Whatsapp
        String codReciboWhatsapp = encaminharMensagemParaWtsapp(getContextoWhatsapp().getContatoWhatsapp(), texto);
        if (codReciboWhatsapp != null) {
            adicionarUltimosEventosEnvaminhados(new EventoEncMatrixParaWtsap(pEventoSala, codReciboWhatsapp));
            return codReciboWhatsapp;
        }
        return null;
    }

    public String encaminharMensagemWtzapParaSala(MensagemWhatsapp msg, ItfChatSalaBean pSala) throws ErroConexaoServicoChat {
        return autoExecucaoEncaminharParaSalaAtiva(msg, pSala);
    }

    private String autoExecucaoEncaminharParaSalaAtiva(MensagemWhatsapp msg, ItfChatSalaBean pSala) throws ErroConexaoServicoChat {

        String conteudomsg = msg.getMensagem();

        ItfUsuarioChat usuarioLead;
        String codigoeventoMatrix = null;

        InputStream arquivo = null;
        switch (msg.getTipoMensagem()) {

            case IMAGEM:
            case AUDIO:
            case VIDEO:
            case DOCUMENTO:
                arquivo = getMediaFromMessage(msg);
                break;

            default:

        }

        try {
            switch (msg.getTipoMensagem()) {
                case TEXTO_SIMPLES:
                    ItfUsuarioChat usuario = getUsuarioSalaRelacionadoWtzap();
                    codigoeventoMatrix = getServicoChat().salaEnviarMesagem(pSala, usuario, msg.getId(), conteudomsg);
                    System.out.println("CodEvento envioMensagem:");
                    System.out.println(codigoeventoMatrix);
                    break;

                case REACAO:
                    codigoeventoMatrix = getServicoChat().salaEnviarMesagem(pSala, getUsuarioSalaRelacionadoWtzap(), null, "Enviou uma reação" + conteudomsg);
                    break;

                case IMAGEM:
                    codigoeventoMatrix = getServicoChat().salaEnviarImagem(pSala, getUsuarioSalaRelacionadoWtzap(), msg.getId(), msg.getMensagem(), arquivo);
                    break;

                case AUDIO:
                    codigoeventoMatrix = getServicoChat().salaEnviarAudio(pSala, getUsuarioSalaRelacionadoWtzap(), msg.getId(), msg.getMensagem(), arquivo);
                    break;

                case VIDEO:

                    codigoeventoMatrix = getServicoChat().salaEnviarMesagem(pSala, getUsuarioSalaRelacionadoWtzap(), msg.getId(), "Enviou um vídeo" + conteudomsg);
                    break;

                case DOCUMENTO:
                    codigoeventoMatrix = getServicoChat().salaEnviarDocumento(pSala, getUsuarioSalaRelacionadoWtzap(), msg.getId(), msg.getMensagem(), arquivo);
                    break;
                case DESCONHECIDO:
                    codigoeventoMatrix = getServicoChat().salaEnviarDocumento(pSala, getUsuarioSalaRelacionadoWtzap(), msg.getId(), msg.getMensagem(), arquivo);
                    break;

                default:
                    codigoeventoMatrix = getServicoChat().salaEnviarMesagem(pSala, getUsuarioSalaRelacionadoWtzap(), msg.getId(), "Enviou um tipo de arquivo não conhecido" + conteudomsg);

                    break;

            }

            if (codigoeventoMatrix != null) {

                return codigoeventoMatrix;
            }
        } catch (ErroConexaoServicoChat t) {
            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha enviando mensagem", t);
            //UtilSBCoreEmail.enviarPorServidorPadraoV2("salviof@gmail.com", msg.getContatoOrigem().getNome() + msg.getContatoOrigem().getWa_id() + " "
            //       + msg.getEntrada() + msg.getNome() + " -- " + msg.getMensagem(), "Falha encaminhando mensagem whatsapp para matrix");
            throw t;

        } finally {
            getServicoChat().salaAbrirSessao(pSala);
        }

        return null;

    }

    public InputStream getMediaFromMessage(MensagemWhatsapp msg) {

        String textoMidia = "Arquivo de Mídia enviado por " + msg.getContatoOrigem().getNome();
        if (msg.getTipoMensagem().isTipoMedia()) {
            try {

                return UtilSBApiWhatsapp.getMediaFromMessage(msg.getCodigoMedia());
            } catch (Throwable t) {
                System.out.println("Falha obtendo arquivo" + t.getMessage());
            }
        }
        return null;
    }

    public static ChatMatrixOrgimpl getChatMatrixService() {
        return chatMatrixService;
    }

    public ItfUsuarioChat getUsuarioSalaRelacionadoWtzap() {
        if ((usuarioSalaRelacionadoWtzap != null)) {
            return usuarioSalaRelacionadoWtzap;
        } else {
            try {
                usuarioSalaRelacionadoWtzap = getUsuarioLeadMatrixCriandoSeNaoExistir(getContextoWhatsapp().getContatoWhatsapp());
            } catch (ErroConexaoServicoChat ex) {
                return null;
            }
        }
        return usuarioSalaRelacionadoWtzap;

    }

    public List<EventoEncMatrixParaWtsap> getUltimosEventosEncaminhado() {
        List<EventoEncMatrixParaWtsap> listaEventos
                = Lists.newArrayList(
                        adicionarUltimosEventosEnvaminhados(null));
        Collections.reverse(listaEventos);
        return listaEventos;
    }

    private synchronized List<EventoEncMatrixParaWtsap> adicionarUltimosEventosEnvaminhados(EventoEncMatrixParaWtsap eventoEca) {
        if (eventoEca != null) {
            ultimosEventosEncaminhado.add(eventoEca);
            if (ultimosEventosEncaminhado.size() > 5) {
                ultimosEventosEncaminhado.remove(0);
            }
        }
        return ultimosEventosEncaminhado;

    }

}
