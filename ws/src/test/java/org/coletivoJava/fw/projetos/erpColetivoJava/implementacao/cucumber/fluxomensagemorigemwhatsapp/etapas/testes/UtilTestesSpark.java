/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas.testes;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.whataspp.ApiWhatsappRecepMensagem;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.RespostaHttpResumo;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.RotaSparkPadrao;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.FluxoMensagemOrigemWhatsapp;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas.C_Quando_o_usuario_Contato_envia_a_mensagem_Ola_tudo_bem_pelo_WhatsApp_para_Atendimento;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import spark.Request;
import spark.Response;

/**
 *
 * @author salvio
 */
public class UtilTestesSpark {

    public static RespostaHttpResumo criarRequisicao(Class<? extends RotaSparkPadrao> pResposnsavelTratamentoRequisicao, String corpo) {
        ApiWhatsappRecepMensagem apiREcepcao = new ApiWhatsappRecepMensagem();

        HttpServletRequest req = mock(HttpServletRequest.class);

// ensina apenas o que seu teste precisa
        when(req.getMethod()).thenReturn("POST");
        when(req.getHeader("payload")).thenReturn("{\"ok\":true}");
        when(req.getParameter("id")).thenReturn("42");

        try {
            when(req.getInputStream())
                    .thenReturn(new SimpleServletInputStreamTestes(FluxoMensagemOrigemWhatsapp.MENSAGEM_whatsapp_SIMPLES_payload));
        } catch (IOException ex) {
            throw new UnsupportedOperationException("corpo não enviado");
        }

        HttpServletResponse response = mock(HttpServletResponse.class);

        try {
            Constructor<Request> ctorequisicao;
            ctorequisicao = Request.class.getDeclaredConstructor(HttpServletRequest.class);
            ctorequisicao.setAccessible(true);

            Request requisicao = ctorequisicao.newInstance(req);

            Constructor<Response> ctoresposta;
            ctoresposta = Response.class.getDeclaredConstructor(HttpServletResponse.class);
            ctoresposta.setAccessible(true);
            Response resposta = ctoresposta.newInstance(response);
            try {
                apiREcepcao.handle(requisicao, resposta);
                return apiREcepcao.getRepostaHttpResumo();
            } catch (Exception ex) {
                Logger.getLogger(C_Quando_o_usuario_Contato_envia_a_mensagem_Ola_tudo_bem_pelo_WhatsApp_para_Atendimento.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(C_Quando_o_usuario_Contato_envia_a_mensagem_Ola_tudo_bem_pelo_WhatsApp_para_Atendimento.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Falha indetermnada simulando requisição");
    }

}
