/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfCentralLogicasProcessamentoMsg;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.logdeMensagens.RepositorioComunicacaoChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoClient.ServicoWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos.ListenerComandosPadrao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos.ListenerSalaMatrix;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.ServicoRecepcaoEventoSpark;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.whataspp.ApiWhatsappRecepMensagem;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao.GestaoDeServicosNavegacao;
import br.org.coletivoJava.fw.api.erp.chat.ERPChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroMtxParalizacaoDeProcessamento;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import br.org.coletivoJava.fw.erp.implementacao.chat.ChatMatrixOrgimpl;
import br.org.coletivoJava.fw.erp.implementacao.chat.json_bind_matrix_org.pacotematrix.PacoteMatrixParsing;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.SalaChatSessaoEscutaAtiva;
import br.org.coletivoJava.fw.erp.implementacao.chat.sessaoMatrix.SincronizacaoSalasMatrix;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.json.ErroProcessandoJson;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivojava.fw.api.tratamentoErros.FabErro;
import org.json.JSONObject;

/**
 *
 * @author salvio
 */
public class AplicacaoWsChat {

    private static List<EntradaNumeroWhatsapp> entradas = new ArrayList<>();
    public static final GestaoDeServicosNavegacao GESTAO_SERVICO_NAVEGACAO = new GestaoDeServicosNavegacao();
    private static ItfCentralLogicasProcessamentoMsg centralLogicasDeProcessamento;
    public static final ChatMatrixOrgimpl SERVICO_MATRIX = (ChatMatrixOrgimpl) ERPChat.MATRIX_ORG.getImplementacaoDoContexto();
    public static final ServicoWhatsapp SERVICO_WHATSAPP = new ServicoWhatsapp();
    public static final RepositorioComunicacaoChat REPOSITORIO_COMUNICACAO_CHAT = new RepositorioComunicacaoChat();
    private static boolean aplicacaoIniciad = false;

    public static final ItfCentralLogicasProcessamentoMsg getCentralLogicaProcesasmento() throws ErroConexaoServicoChat {
        if (centralLogicasDeProcessamento == null) {
            ServiceLoader<ItfCentralLogicasProcessamentoMsg> services
                    = ServiceLoader.load(ItfCentralLogicasProcessamentoMsg.class);
            centralLogicasDeProcessamento = services.iterator().next();

        }
        return centralLogicasDeProcessamento;
    }

    public static synchronized void iniciarAplicacao() {
        try {
            if (aplicacaoIniciad) {
                return;
            }
            getCentralLogicaProcesasmento();
            loadEntradas();
            //TODO VALIDAR AUTENTICAÇÃO DE SISTEMAS, ANTES DE INICIAR
            centralLogicasDeProcessamento.getSistemas();

            //TODO VALIDAR CODIGOS DE ENTRADA EXECUTANDO CHAMADA DE WHATSAPP ANTES DE INICIAR O SISTEMA
            for (EntradaNumeroWhatsapp entrada : entradas) {

            }
            //TODO VALIDAR AUTENTICAÇÃO DO SERVIÇO MATRIX ANTES DE INICIAR
            AplicacaoWsChat.SERVICO_MATRIX.registrarClasseDeEscutaSalas(ListenerSalaMatrix.class);
            AplicacaoWsChat.SERVICO_MATRIX.registrarClasseEscutaNotificacoes(ListenerComandosPadrao.class);
            //      AplicacaoWsChat.SERVICO_MATRIX.registrarClasseEscutaNotificacoes(ListenerNotificacaoMatrixAuxiliadora.class);
            ServicoRecepcaoEventoSpark.iniciarServico();
            getCentralLogicaProcesasmento().inicializacaoServicosTerceiros();
            aplicacaoIniciad = true;
        } catch (Throwable t) {
            System.out.println("FALHA CONECTANDO COM SERVIÇO DE CHAT, TENTANDO NOVAMENTE EM 10 SEGUNDOS");
            try {
                Thread.sleep(10000);
                iniciarAplicacao();
            } catch (InterruptedException ex) {

            }
        }
    }

    public static EntradaNumeroWhatsapp getEntradaByCodigoEntrada(String pCodigo) throws ErroRegraDeNegocio {
        if (entradas.isEmpty()) {
            loadEntradas();
        }

        Optional<EntradaNumeroWhatsapp> pesquisaEntrada = entradas.stream().filter(et -> et.getCodigo().equals(pCodigo)).findFirst();
        if (pesquisaEntrada.isPresent()) {
            EntradaNumeroWhatsapp entrada = pesquisaEntrada.get();
            return entrada;
        }
        throw new ErroRegraDeNegocio("Falha encontrando telefone relacionado a integração, código" + pCodigo);
    }

    private synchronized static void loadEntradas() {
        try {
            entradas = AplicacaoWsChat.getCentralLogicaProcesasmento().gerarEntradas();
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(AplicacaoWsChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void encerrrarSessao(EntradaNumeroWhatsapp pCanalComunicacaoWtp, String pWpIDcontado) {
        Contato ct;
        try {
            ct = REPOSITORIO_COMUNICACAO_CHAT.getContato(pWpIDcontado);
        } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat ex) {
            return;
        }
        if (ct == null) {
            return;
        }

        ContextoContato ctxContato = REPOSITORIO_COMUNICACAO_CHAT.getContextoContato(pCanalComunicacaoWtp, ct);
        ctxContato.setTrilhaAtual(null);
        ctxContato.setDataHoraFinalSessao(new Date());
        REPOSITORIO_COMUNICACAO_CHAT.contextoAtualizar(ctxContato);
        try {
            GESTAO_SERVICO_NAVEGACAO.removerRota(ctxContato);
        } catch (Throwable ex) {
            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha encerrando sesssao" + ctxContato.getContato().getNome(), ex);

        }

    }

    /**
     *
     * TODO CRIAR, e mover esse método para um pacote de testes
     *
     * @param corpoWhatsapp
     * @return
     * @throws ErroProcessandoJson
     * @throws ErroRegraDeNegocio
     * @th rows ErroRecursoNaoEncontrado
     * @thr ows ErroConexaoSistemaTerceiro
     */
    public static String injetarPacoteWhatsapp(String corpoWhatsapp) throws ErroProcessandoJson, ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
        if (SBCore.isEmModoProducao()) {
            throw new ErroRegraDeNegocio("Este método tem o propósito de ser usado em testes apenas");

        }
        ApiWhatsappRecepMensagem apiAplicacao = new ApiWhatsappRecepMensagem();
        return apiAplicacao.processar(new PacoteMemensagemRecebidoWhatsapp(corpoWhatsapp));
    }

    /**
     *
     *
     * TODO CRIAR, e mover esse método para um pacote de testes
     *
     *
     * @param pPacoteMatrix
     * @throws ErroProcessandoJson
     * @throws ErroRegraDeNegocio
     * @throws ErroRecursoNaoEncontrado
     * @throws ErroConexaoSistemaTerceiro
     * @throws ErroConexaoServicoChat
     */
    public static void injetarPacoteMatrix(String pPacoteMatrix) throws ErroProcessandoJson, ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro, ErroConexaoServicoChat, ErroMtxParalizacaoDeProcessamento {
        if (SBCore.isEmModoProducao()) {
            throw new ErroRegraDeNegocio("Este método tem o propósito de ser usado em testes apenas");
        }
        JSONObject syncData = new JSONObject(pPacoteMatrix);
        List<ItfEventoMatix> eventosDeSala = PacoteMatrixParsing.parseEventoSalas(syncData, AplicacaoWsChat.SERVICO_MATRIX).getEventos();
        for (ItfEventoMatix evento : eventosDeSala) {
            ComoChatSalaBean sala;

            sala = AplicacaoWsChat.SERVICO_MATRIX.getSalaByCodigo(evento.getRoom_id());

            if (AplicacaoWsChat.SERVICO_MATRIX.isSalaMonitoramentoAutomatica(sala.getApelido())) {
                SalaChatSessaoEscutaAtiva escuta = AplicacaoWsChat.SERVICO_MATRIX.salaAbrirSessao(sala);
                if (escuta.getEscuta().isElegivel(evento)) {
                    escuta.getEscuta().processarEvento(evento);
                }

            }

        }
    }
}
