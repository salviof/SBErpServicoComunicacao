/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.whatsapp;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.FabTipoMensagemWhatsapp.AUDIO;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.FabTipoMensagemWhatsapp.DOCUMENTO;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.FabTipoMensagemWhatsapp.IMAGEM;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.FabTipoMensagemWhatsapp.REACAO;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.FabTipoMensagemWhatsapp.TEXTO_SIMPLES;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.FabTipoMensagemWhatsapp.VIDEO;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.FabTipoStatusMensagemWhtzap.DESCONHECIDO;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;

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
public abstract class ProcessadorSocketWhatsapp {

    public ProcessadorSocketWhatsapp() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {

    }

    protected String encaminharMensagemTextoAdministraParaMatrix(String texto, ItfChatSalaBean pSala, ItfUsuarioChat pAtendimento) throws ErroConexaoServicoChat {
        if (pSala != null) {
            return AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(pSala, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(), String.valueOf(texto.hashCode()), texto);
        } else {
            return AplicacaoWsChat.SERVICO_MATRIX.enviarDirect(pAtendimento.getCodigoUsuario(), texto);
        }
    }

    protected String encaminharMensagemParaMatrix(MensagemWhatsapp msg, ItfChatSalaBean pSala, ItfUsuarioChat pContato) throws ErroConexaoServicoChat {

        String conteudomsg = msg.getMensagem();
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
