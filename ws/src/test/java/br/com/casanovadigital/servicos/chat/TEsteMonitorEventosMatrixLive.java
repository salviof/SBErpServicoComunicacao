/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config.ConfigCoreServicoComunicacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos.ListenerComandosPadrao;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import com.super_bits.casanovadigital.servicos.messagens.model.configModel.ConfigPercistenciaServicoComunicacao;
import com.super_bits.modulosSB.Persistencia.ConfigGeral.SBPersistencia;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author salvio
 */
public class TEsteMonitorEventosMatrixLive {

    @Before
    public void configuracoes() {
        SBCore.configurar(new ConfigCoreServicoComunicacao(), SBCore.ESTADO_APP.DESENVOLVIMENTO);
        SBPersistencia.configuraJPA(new ConfigPercistenciaServicoComunicacao());

    }

    @Test
    public void teste() {
        try {
            //AplicacaoWsChat.iniciarAplicacao();
            AplicacaoWsChat.SERVICO_MATRIX.registrarClasseDeEscutaSalas(ListenerSalaMatrixTestesLive.class);
            AplicacaoWsChat.SERVICO_MATRIX.registrarClasseEscutaNotificacoes(ListenerComandosPadrao.class);
            while (true) {
                try {
                    Thread.sleep(1000000000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TEsteMonitorEventosMatrixLive.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(TEsteMonitorEventosMatrixLive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
