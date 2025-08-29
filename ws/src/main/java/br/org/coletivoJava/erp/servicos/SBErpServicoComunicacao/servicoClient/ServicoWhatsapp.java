package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoClient;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.UtilMatrixERP;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import br.org.coletivoJava.integracoes.restIntmatrixchat.UtilsbApiMatrixChat;
import br.org.coletivoJava.integracoes.restIntmatrixchat.implementacao.UtilMatrixApiServer;
import br.org.coletivoJava.integracoes.restIntwhatsapp.api.model.mensagem.MensagemSimplesEnvioWhatsapp;
import br.org.coletivoJava.integracoes.restIntwhatsapp.implementacao.UtilSBApiWhatsapp;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappMensagem;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringFiltros;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import jakarta.json.JsonValue;

/**
 * @author salvio
 */
public class ServicoWhatsapp {

    public String encaminharMensagem(EntradaNumeroWhatsapp pEntrada, String pContatoWtzpID, ItfEventoMatix pEvento, ItfChatSalaBean pSala) throws ErroConexaoServicoChat {
        /// implameNTAR o swith case para os tipos de eventos.
        //// IMPLEMENTAR O CABEÇALHO DO CONATO AQUI , REMOVENDO DO ProcessadorMtxMensagem
        MensagemSimplesEnvioWhatsapp novamensagem = new MensagemSimplesEnvioWhatsapp();

        novamensagem.setCorpo(pEvento.getContent().getString("body"));
        ItfUsuarioChat usuarioAtendimento = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(pEvento.getSender());

        if (usuarioAtendimento == null) {
            throw new ErroConexaoServicoChat("Usuário de atendimento " + pEvento.getSender() + " não foi encontrado");
        }
        //
        // byte[] arquivo = UtilMatrixApiServer.getMediaBytesByID(idMedia);
        String tipo = "texto";
        switch (tipo) {
            case "texto":
                enviarMensagemTexto(pEntrada, pContatoWtzpID, novamensagem);

                break;
            default:
                throw new AssertionError();
        }
        MensagemSimplesEnvioWhatsapp novaMensagem = new MensagemSimplesEnvioWhatsapp();
        novaMensagem.setCabecalho(usuarioAtendimento.getNome() + ":");
        String textomensagem = pEvento.getContent().getString("body");
        novaMensagem.setCorpo(textomensagem);

        FabTipoSalaMatrix tipoSAla = FabTipoSalaMatrix.getTipoByAlias(pSala.getApelido());
        switch (tipoSAla) {
            case MATRIX_CHAT_ATENDIMENTO_CHAMADO:
                novaMensagem.setCabecalho("Chamado #" + UtilSBCoreStringFiltros.filtrarApenasNumeros(pSala.getNome()) + " " + usuarioAtendimento.getNome());
                break;
        }

//        FabTipoPacoteDeAcaoMatrix
//        EventoSalaMatrix
        novamensagem.setCabecalho(usuarioAtendimento.getNome() + ":");
        return enviarMensagemTexto(pEntrada, pContatoWtzpID, novamensagem);
    }

    public String encaminharMensagem(EntradaNumeroWhatsapp pEntrada, String pContatoWtzpID, String pMensagem) throws ErroConexaoServicoChat {

        MensagemSimplesEnvioWhatsapp novamensagem = new MensagemSimplesEnvioWhatsapp();
        novamensagem.setCorpo(pMensagem);
        novamensagem.setCabecalho(AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin().getNome());

        return enviarMensagemTexto(pEntrada, pContatoWtzpID, novamensagem);
    }

    public String enviarMensagemTexto(EntradaNumeroWhatsapp pEntrada, String pContatoWtzpID, MensagemSimplesEnvioWhatsapp pMensagem) throws ErroConexaoServicoChat {

        ItfRespostaWebServiceSimples resposta = FabApiRestIntWhatsappMensagem.MENSAGEM_ENVIAR.getAcao(
                pEntrada.getCodigo(),
                pContatoWtzpID,
                pMensagem
        ).getResposta();
        if (!resposta.isSucesso()) {
            throw new ErroConexaoServicoChat(resposta.getRespostaTexto());
        }
        JsonValue valor = resposta.getRespostaComoObjetoJson().getJsonArray("messages").stream().findFirst().get();
        return valor.asJsonObject().getString("id");
    }

    public String enviarMensagemTexto(EntradaNumeroWhatsapp pEntrada, String pWpID, String pMensagemStr) throws ErroConexaoServicoChat {
        MensagemSimplesEnvioWhatsapp msg = new MensagemSimplesEnvioWhatsapp();
        msg.setCabecalho(AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin().getNome());
        msg.setCorpo(pMensagemStr);
        ItfRespostaWebServiceSimples resposta = FabApiRestIntWhatsappMensagem.MENSAGEM_ENVIAR.getAcao(
                pEntrada.getCodigo(),
                pWpID,
                msg
        ).getResposta();
        if (!resposta.isSucesso()) {
            throw new ErroConexaoServicoChat(resposta.getRespostaTexto());
        }
        JsonValue valor = resposta.getRespostaComoObjetoJson().getJsonArray("messages").stream().findFirst().get();
        return valor.asJsonObject().getString("id");
    }

    public String enviarImagem(EntradaNumeroWhatsapp pEntrada, Contato pContato, byte[] pArquivo, String pNomeArquivo) throws ErroConexaoServicoChat {
        ItfRespostaWebServiceSimples resposta = FabApiRestIntWhatsappMensagem.MENSAGEM_IMAGEM_ENVIAR.getAcao(pEntrada.getCodigo(), pContato.getWaid(), pArquivo, pNomeArquivo).getResposta();
        if (!resposta.isSucesso()) {
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
        if (!resposta.isSucesso()) {
            throw new ErroConexaoServicoChat(resposta.getRespostaTexto());
        }
        JsonValue valor = resposta.getRespostaComoObjetoJson().getJsonArray("messages").stream().findFirst().get();
        return valor.asJsonObject().getString("id");
    }

}
