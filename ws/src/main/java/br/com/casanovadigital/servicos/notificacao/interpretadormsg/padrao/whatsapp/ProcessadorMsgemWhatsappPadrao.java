/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.interpretadormsg.padrao.whatsapp;

import br.com.casanovadigital.servicos.notificacao.AplicacaoWsChat;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.FabTipoRetornoEncaminhamentoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.interfaces.ItfProcessadorMensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Atendente;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;

/**
 *
 * @author salvio
 */
public class ProcessadorMsgemWhatsappPadrao extends ProcessadorWtzpPadrao implements ItfProcessadorMensagemWhatsapp {

    private final MensagemWhatsapp mensagem;

    private String reciboEncaminhamentoMatrix;
    private String link;
    private FabTipoRetornoEncaminhamentoWhatsapp tipoRetorno;

    private Atendente atendentePrincipal;
    private Contato contatoPrincipal;

    public ProcessadorMsgemWhatsappPadrao(MensagemWhatsapp pMensagem) {
        super();
        mensagem = pMensagem;

    }

    @Override
    protected void processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {

        tipoRetorno = FabTipoRetornoEncaminhamentoWhatsapp.ENCAMINHAMENTO;

        ItfUsuarioChat usuarioChatContato = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(mensagem.getEntrada(), mensagem.getContatoOrigem());
        ItfUsuarioChat usuarioChatAtendimento = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(mensagem.getEntrada(), mensagem.getContatoOrigem());
        try {
            ItfChatSalaBean sala = AplicacaoWsChat.getCentralLogicaProcesasmento().getSalaPadrao(mensagem.getEntrada(), mensagem.getContatoOrigem());
            try {
                reciboEncaminhamentoMatrix = AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(sala, usuarioChatContato, getMensagemWhatsapp().getId(), mensagem.getMensagem());
            } catch (ErroConexaoServicoChat ex) {
                throw new ErroFalhaEncaminhando("Houve falha de conxeão com o serviço Matrix");
            }
        } catch (ErroFalhaGerandoSalaAtendimento ex) {
            throw new ErroFalhaGerandoSalaAtendimento("Falha gerando sala de atendimento");
        }

    }

    @Override
    public String getReciboEncaminhamentoMatrix() {
        aguardarProcessamento();
        return reciboEncaminhamentoMatrix;
    }

    @Override
    public String getLink() {
        aguardarProcessamento();
        return link;
    }

    @Override
    public FabTipoRetornoEncaminhamentoWhatsapp getTipoEncaminhamento() {
        aguardarProcessamento();
        return tipoRetorno;

    }

    @Override
    public boolean isSucesso() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {
        aguardarProcessamento();
        return sucesso;
    }

    @Override
    public MensagemWhatsapp getMensagemWhatsapp() {
        return mensagem;
    }

}
