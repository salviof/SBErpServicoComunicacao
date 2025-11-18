/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.mensagem;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfProcessadorPacoteMatrixWhatsap;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoUsuarioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappMensagem;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;

/**
 *
 * @author salvio
 */
public class ProcessadorMtxReacaoMensagem implements
        ItfProcessadorPacoteMatrixWhatsap {

    private final ItfEventoMatix evento;
    private final MensagemTrOrigemWhatsapp mensagemTransito;
    private final ComoChatSalaBean sala;
    private final Contato contato;

    public ProcessadorMtxReacaoMensagem(ItfEventoMatix pEvento, ComoChatSalaBean pSala, MensagemTrOrigemWhatsapp pMensagem, Contato pContato, ComoUsuarioChat pAtendente) {
        evento = pEvento;
        mensagemTransito = pMensagem;
        sala = pSala;
        contato = pContato;
    }

    @Override
    public void processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento, ErroConexaoServicoChat {

        EntradaNumeroWhatsapp entrada;
        try {
            entrada = AplicacaoWsChat.getCentralLogicaProcesasmento().getEntradaBySala(sala.getApelido());
        } catch (ErroRegraDeNegocio ex) {
            throw new ErroComDevolucaoMensagemUsuario("Falha enviando reação para whatasapp " + ex.getMessage(), "Falha enviando reação, canal de whatsapp não iedentificado pela sala " + sala.getNome());
        }
        if (mensagemTransito == null) {
            return;
        }
        if (mensagemTransito.getCodigoRegistroMensagemWhatsapp() == null) {
            return;
        }

        if (evento.getEvent_id().contains("waid")) {
            ItfRespostaWebServiceSimples resp = FabApiRestIntWhatsappMensagem.MENSAGEM_REACAO.getAcao(entrada.getCodigo(), contato.getWaid(), mensagemTransito.getCodigoRegistroMensagemWhatsapp()).getResposta();
            if (resp.isSucesso()) {

            } else {
                throw new ErroComDevolucaoMensagemUsuario("Falha enviando reação para whatasapp " + resp.getRespostaTexto(), "Falha enviando reação");
            }
        }

    }

}
