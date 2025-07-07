/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.interpretadormsg.padrao.whatsapp;

import br.com.casanovadigital.servicos.chat.AplicacaoWsChat;
import br.com.casanovadigital.servicos.chat.interpretadormsg.FabTipoRetornoEncaminhamentoWhatsapp;
import br.com.casanovadigital.servicos.chat.interpretadormsg.interfaces.ItfProcessadorMensagemWhatsapp;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappPerfil;
import com.amazonaws.monitoring.ApiCallMonitoringEvent;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Atendente;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author salvio
 */
public class ProcessadorMsgWhatsappPadrao extends ProcessadorWtzp implements ItfProcessadorMensagemWhatsapp {

    private final MensagemWhatsapp mensagem;

    private String reciboEncaminhamentoMatrix;
    private String link;
    private FabTipoRetornoEncaminhamentoWhatsapp tipoRetorno;
    private String codigoSalaMatrixEncaminhamento;

    private Atendente atendentePrincipal;
    private Contato contatoPrincipal;

    public ProcessadorMsgWhatsappPadrao(MensagemWhatsapp pMensagem) throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {
        super();
        mensagem = pMensagem;

    }

    @Override
    protected boolean processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {

        tipoRetorno = FabTipoRetornoEncaminhamentoWhatsapp.ENCAMINHAMENTO;

        ItfUsuarioChat usuarioMAtrixContato;
        try {
            //String jsonUrlAvatar = FabApiRestIntWhatsappPerfil.PERFIL_DADOS_BASICOS.getAcao(mensagem.getContatoOrigem().getWa_id()).getResposta().getRespostaTexto();
            //AplicacaoWsChat.SERVICO_MATRIX.us
            //AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getMensagemEnviadaPorMatrixByRegistroWhatsapp();
            usuarioMAtrixContato = AplicacaoWsChat.SERVICO_MATRIX.gerarUsuarioContato(mensagem.getNome(), mensagem.getTelefone());
        } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat ex) {
            throw new ErroFalhaEncaminhando("Falha obtendo usuário correspentente ao contato no sistema Matrix" + ex.getMessage());
        }
        //  ItfUsuarioChat usuarioChatAtendimento = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(mensagem.getEntrada(), mensagem.getContatoOrigem());
        try {
            ItfChatSalaBean sala = AplicacaoWsChat.getCentralLogicaProcesasmento().getSalaPadrao(mensagem.getEntrada(), mensagem.getContatoOrigem());
            codigoSalaMatrixEncaminhamento = sala.getCodigoChat();
            try {

                reciboEncaminhamentoMatrix = encaminharMensagemParaMatrix(mensagem, sala, usuarioMAtrixContato);

            } catch (ErroConexaoServicoChat ex) {
                throw new ErroFalhaEncaminhando("Houve falha de conxeão com o serviço Matrix");
            }
        } catch (ErroFalhaGerandoSalaAtendimento ex) {
            throw new ErroFalhaGerandoSalaAtendimento("Falha gerando sala de atendimento");
        }
        return true;

    }

    @Override
    public String getCodigoSalaMatrixEncaminhamento() {
        return codigoSalaMatrixEncaminhamento;
    }

    @Override
    public String getReciboEncaminhamentoMatrix() {

        return reciboEncaminhamentoMatrix;
    }

    @Override
    public String getLink() {

        return link;
    }

    @Override
    public FabTipoRetornoEncaminhamentoWhatsapp getTipoEncaminhamento() {

        return tipoRetorno;

    }

    @Override
    public boolean isSucesso() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {

        return sucesso;
    }

    @Override
    public MensagemWhatsapp getMensagemWhatsapp() {
        return mensagem;
    }

}
