/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas.testes;

import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.ApiWhatsappRecepcaoMensagem;
import br.com.casanovadigital.servicos.chat.servicoServer.escutas.spark.whataspp.ApiWhatsappRecepMensagem;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.FluxoMensagemOrigemWhatsapp;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas.C_Quando_o_usuario_Contato_envia_a_mensagem_Ola_tudo_bem_pelo_WhatsApp_para_Atendimento;
import static org.mockito.Mockito.mock;
import spark.Request;
import static org.mockito.Mockito.*;
import spark.Request;
import spark.Response;
import spark.Response;

/**
 *
 * @author salvio
 */
public class UtilTestesSpark {

    public static void CriarRequisicao(Class<? extends RotaSparkPadrao> pResposnsavelTratamentoRequisicao, String corpo) {
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

        Constructor<Request> ctor;
        try {
            ctor = Request.class.getDeclaredConstructor(HttpServletRequest.class);
            ctor.setAccessible(true);
            Request requisicao = ctor.newInstance(req);
            try {
                apiREcepcao.handle(requisicao, new Response() {
                });

            } catch (Exception ex) {
                Logger.getLogger(C_Quando_o_usuario_Contato_envia_a_mensagem_Ola_tudo_bem_pelo_WhatsApp_para_Atendimento.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(C_Quando_o_usuario_Contato_envia_a_mensagem_Ola_tudo_bem_pelo_WhatsApp_para_Atendimento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
