/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.matrix.mensagem;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfProcessadorPacoteMatrixWhatsap;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoUsuarioChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.EncaminhamentoMatrixParaWtzp;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.modulosSB.SBCore.ConfigGeral.CarameloCode;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;

/**
 * @author salvio
 */
public class ProcessadorMtxMensagem implements
        ItfProcessadorPacoteMatrixWhatsap {

    private final ItfEventoMatix evento;
    private final MensagemTrOrigemMatrix mensagemTransito;
    private final ComoChatSalaBean sala;
    private final Contato contato;

    public ProcessadorMtxMensagem(ItfEventoMatix pEvento, ComoChatSalaBean pSala, MensagemTrOrigemMatrix pMensagem, Contato pContato, ComoUsuarioChat pAtendente) {
        evento = pEvento;
        mensagemTransito = pMensagem;
        sala = pSala;
        contato = pContato;
    }

    private static final String TAG_LOG = "[MTX->WTZP]";

    /**
     * A instrumentação nunca pode interromper a entrega da mensagem, por isso o
     * serviço de log é chamado dentro de um try.
     */
    private void log(FabMensagens pTipo, String pMensagem) {
        try {
            CarameloCode.getServicoLogEventos().registrarLogDeEvento(pTipo, TAG_LOG + " " + pMensagem);
        } catch (Throwable t) {
            System.out.println(TAG_LOG + " " + pTipo + " " + pMensagem);
        }
    }

    @Override
    public void processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento, ErroConexaoServicoChat {

        long inicioProcessar = System.currentTimeMillis();
        String codReciboWhatsapp = null;
        ComoUsuarioChat usuarioAtendimento;

        usuarioAtendimento = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(evento.getSender());

        if (!AplicacaoWsChat.SERVICO_MATRIX.isUmUsuarioAtendimento(usuarioAtendimento)) {
            throw new ErroComDevolucaoMensagemUsuario("Evento " + evento.getEvent_id() + " não foi emitido por um usuário de atendimento", "Mensagem não foi entregue, Seu usuário não é um usuário de atendimento");
        }

        try {
            EntradaNumeroWhatsapp entrada;
            try {
                entrada = AplicacaoWsChat.getCentralLogicaProcesasmento().getEntradaBySala(sala.getApelido());
                System.out.println("ENTRADA::: " + entrada);
            } catch (ErroRegraDeNegocio ex) {
                throw new ErroComDevolucaoMensagemUsuario("Falha identificando telefone de origem para sala " + sala, "Impossível determinar o telefone de origem da sala" + sala.getCodigoChat());
            }

            long inicioEnvioWhatsapp = System.currentTimeMillis();
            codReciboWhatsapp = AplicacaoWsChat.SERVICO_WHATSAPP.encaminharMensagem(entrada, contato.getWaid(), evento, sala);
            long duracaoEnvioWhatsapp = System.currentTimeMillis() - inicioEnvioWhatsapp;
            log(codReciboWhatsapp == null ? FabMensagens.ERRO : FabMensagens.AVISO,
                    "Encaminhamento ao Whatsapp concluído em " + duracaoEnvioWhatsapp + "ms."
                    + " eventoMatrix=" + evento.getEvent_id()
                    + " sala=" + sala.getApelido()
                    + " waid=" + contato.getWaid()
                    + " entrada=" + entrada.getCodigo()
                    + " recibo=" + (codReciboWhatsapp == null ? "NULO (FALHOU)" : codReciboWhatsapp));
            if (codReciboWhatsapp != null) {
                ItfTrilhaNavegacao trilha = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getTrilhaByEventoExistente(entrada, contato, evento);

                EncaminhamentoMatrixParaWtzp encaminhamento = new EncaminhamentoMatrixParaWtzp();
                encaminhamento.setMensagem(mensagemTransito);
                encaminhamento.setReciboRegistrooWtzp(codReciboWhatsapp);
                encaminhamento.setContato(contato);

                mensagemTransito.setCodigoReciboMensagemMatrix(evento.getEvent_id());
                mensagemTransito.getEncaminhamentos().add(encaminhamento);
            } else {
                throw new ErroComDevolucaoMensagemUsuario("", "Falha registrando pedido de entrega de mensagem no servidor do Whatsapp");
            }
        } catch (ErroComDevolucaoMensagemUsuario enc) {
            log(FabMensagens.ERRO, "Devolução ao usuário após " + (System.currentTimeMillis() - inicioProcessar)
                    + "ms. eventoMatrix=" + evento.getEvent_id()
                    + " sala=" + sala.getApelido()
                    + " motivo=" + enc.getMessage()
                    + " | retornoAoUsuario=" + enc.getMensagemRetorno());
            throw enc;
        } catch (Throwable ex) {
            log(FabMensagens.ERRO, "Exceção não prevista encaminhando ao Whatsapp após "
                    + (System.currentTimeMillis() - inicioProcessar) + "ms."
                    + " eventoMatrix=" + evento.getEvent_id()
                    + " sala=" + sala.getApelido()
                    + " erro=" + ex.getClass().getName() + ": " + ex.getMessage());
            throw new ErroComDevolucaoMensagemUsuario(ex.getMessage(), "Falha registrando pedido de entrega de mensagem no servidor do Whatsapp");
        }

    }

}
