package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoClient;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import br.org.coletivoJava.integracoes.restIntwhatsapp.api.model.mensagem.MensagemSimplesEnvioWhatsapp;
import br.org.coletivoJava.integracoes.restIntwhatsapp.implementacao.UtilSBApiWhatsapp;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappMensagem;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;
import jakarta.json.JsonValue;

/**
 * @author salvio
 */
public class ServicoWhatsapp {

    public String encaminharMensagem(ItfEventoMatix pEvento, EntradaNumeroWhatsapp pEntrada, Contato pContato, MensagemSimplesEnvioWhatsapp pMensagem) throws ErroConexaoServicoChat {
        return enviarMensagemTexto(pEvento, pEntrada, pContato, pMensagem);
    }

    public String enviarMensagemTexto(ItfEventoMatix pEvento, EntradaNumeroWhatsapp pEntrada, Contato pContato, MensagemSimplesEnvioWhatsapp pMensagem) throws ErroConexaoServicoChat {

        ItfRespostaWebServiceSimples resposta = FabApiRestIntWhatsappMensagem.MENSAGEM_ENVIAR.getAcao(
                pEntrada.getCodigo(),
                pContato.getWaid(),
                pMensagem
        ).getResposta();
        if (!resposta.isSucesso()) {
            throw new ErroConexaoServicoChat(resposta.getRespostaTexto());
        }
        JsonValue valor = resposta.getRespostaComoObjetoJson().getJsonArray("messages").stream().findFirst().get();
        return valor.asJsonObject().getString("id");
    }

    public String enviarImagem(EntradaNumeroWhatsapp pEntrada, Contato pContato, byte[] pArquivo, String pNomeArquivo) throws ErroConexaoServicoChat {
        ItfRespostaWebServiceSimples resposta = FabApiRestIntWhatsappMensagem.MENSAGEM_IMAGEM_ENVIAR.getAcao(pEntrada.getCodigo(), pContato.getWaid(), pArquivo, pNomeArquivo).getResposta();
        if(!resposta.isSucesso()) {
            throw new ErroConexaoServicoChat(resposta.getRespostaTexto());
        }
        JsonValue valor = resposta.getRespostaComoObjetoJson().getJsonArray("messages").stream().findFirst().get();
        return valor.asJsonObject().getString("id");
    }

    public String enviarAudio(EntradaNumeroWhatsapp pEntrada, Contato pContato, byte[] pArquivo, String pNomeArquivo) throws ErroConexaoServicoChat {
        String tipoArquivo = "audio/ogg";
        JsonValue valor = null;
        try {
            String codigoMetaArquivo = UtilSBApiWhatsapp.mediaUpload(pArquivo, pNomeArquivo, tipoArquivo);
            ItfRespostaWebServiceSimples resposta = FabApiRestIntWhatsappMensagem.MENSAGEM_AUDIO_ENVIAR.getAcao(pEntrada.getCodigo(), pContato.getWaid(), codigoMetaArquivo).getResposta();
            if (!resposta.isSucesso()) {
                throw new ErroConexaoServicoChat(resposta.getRespostaTexto());
            }
            valor = resposta.getRespostaComoObjetoJson().getJsonArray("messages").stream().findFirst().get();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return valor.asJsonObject().getString("id");
    }

    public String enviarPdf(EntradaNumeroWhatsapp pEntrada, Contato pContato, byte[] pArquivo, String pNomeArquivo) throws ErroConexaoServicoChat {
        ItfRespostaWebServiceSimples resposta = FabApiRestIntWhatsappMensagem.MENSAGEM_PDF_ENVIAR.getAcao(pEntrada.getCodigo(), pContato.getWaid(), pArquivo, pNomeArquivo).getResposta();
        if(!resposta.isSucesso()) {
            throw new ErroConexaoServicoChat(resposta.getRespostaTexto());
        }
        JsonValue valor = resposta.getRespostaComoObjetoJson().getJsonArray("messages").stream().findFirst().get();
        return valor.asJsonObject().getString("id");
    }

}
