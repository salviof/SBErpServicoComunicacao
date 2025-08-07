package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoClient;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import br.org.coletivoJava.integracoes.restIntwhatsapp.api.model.mensagem.MensagemSimplesEnvioWhatsapp;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappMensagem;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;
import jakarta.json.JsonValue;

/**
 *
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

    public String enviarImagem() throws ErroComDevolucaoMensagemUsuario {

        throw new ErroComDevolucaoMensagemUsuario("O sistema não suporta encaminhamento de imagem", "O sistema não suporta encaminhamento de imagem");

    }

    public String enviarAudio() throws ErroComDevolucaoMensagemUsuario {
        throw new ErroComDevolucaoMensagemUsuario("O sistema não suporta encaminhamento de uadio", "O sistema não suporta encaminhamento de audio");
    }

}
