/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.interpretadormsg.padrao.whatsapp;

import br.com.casanovadigital.servicos.chat.AplicacaoWsChat;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import static br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.FabTipoMensagemWhatsapp.AUDIO;
import static br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.FabTipoMensagemWhatsapp.DESCONHECIDO;
import static br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.FabTipoMensagemWhatsapp.DOCUMENTO;
import static br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.FabTipoMensagemWhatsapp.IMAGEM;
import static br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.FabTipoMensagemWhatsapp.REACAO;
import static br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.FabTipoMensagemWhatsapp.TEXTO_SIMPLES;
import static br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.FabTipoMensagemWhatsapp.VIDEO;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.integracoes.restIntwhatsapp.implementacao.UtilSBApiWhatsapp;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import java.io.InputStream;
import org.coletivojava.fw.api.tratamentoErros.FabErro;

/**
 *
 * @author salvio
 */
public abstract class ProcessadorWtzp {

    protected boolean sucesso = false;

    public ProcessadorWtzp() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {
        sucesso = processar();
    }

    protected abstract boolean processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento;

    protected String encaminharMensagemParaMatrix(MensagemWhatsapp msg, ItfChatSalaBean pSala, ItfUsuarioChat pContato) throws ErroConexaoServicoChat {

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
                    ItfUsuarioChat usuario = pContato;
                    codigoeventoMatrix = AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(pSala, usuario, msg.getId(), conteudomsg);
                    System.out.println("CodEvento envioMensagem:");
                    System.out.println(codigoeventoMatrix);
                    break;

                case REACAO:
                    codigoeventoMatrix = AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(pSala, pContato, null, "Enviou uma reação" + conteudomsg);
                    break;

                case IMAGEM:
                    codigoeventoMatrix = AplicacaoWsChat.SERVICO_MATRIX.salaEnviarImagem(pSala, pContato, msg.getId(), msg.getMensagem(), arquivo);
                    break;

                case AUDIO:
                    codigoeventoMatrix = AplicacaoWsChat.SERVICO_MATRIX.salaEnviarAudio(pSala, pContato, msg.getId(), msg.getMensagem(), arquivo);
                    break;

                case VIDEO:

                    codigoeventoMatrix = AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(pSala, pContato, msg.getId(), "Enviou um vídeo" + conteudomsg);
                    break;

                case DOCUMENTO:
                    codigoeventoMatrix = AplicacaoWsChat.SERVICO_MATRIX.salaEnviarDocumento(pSala, pContato, msg.getId(), msg.getMensagem(), arquivo);
                    break;
                case DESCONHECIDO:
                    codigoeventoMatrix = AplicacaoWsChat.SERVICO_MATRIX.salaEnviarDocumento(pSala, pContato, msg.getId(), msg.getMensagem(), arquivo);
                    break;

                default:
                    codigoeventoMatrix = AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(pSala, pContato, msg.getId(), "Enviou um tipo de arquivo não conhecido" + conteudomsg);

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
            //AplicacaoWsChat.SERVICO_MATRIX.salaAbrirSessao(pSala);
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

}
