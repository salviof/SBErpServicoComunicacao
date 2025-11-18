/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config.ConfigCoreServicoComunicacao;

import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.ChatMatrixOrgimpl;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import br.org.coletivoJava.integracoes.matrixChat.FabApiRestIntMatrixChatSalas;
import br.org.coletivoJava.integracoes.matrixChat.FabApiRestIntMatrixSpaces;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author salvio
 */
public class TesteEspacos {

    @Test
    public void teste() {
        SBCore.configurar(new ConfigCoreServicoComunicacao(), SBCore.ESTADO_APP.HOMOLOGACAO);
        ChatMatrixOrgimpl erpChatService = AplicacaoWsChat.SERVICO_MATRIX;
        FabTipoSalaMatrix tipoSala = FabTipoSalaMatrix.WTZAP_VENDAS;

        try {
            ComoUsuarioChat wagner = erpChatService.getUsuarioByEmail("wagner@casanovadigital.com.br");
            System.out.println(wagner.getCodigoUsuario());
            ComoChatSalaBean espaco = erpChatService.getSalaByAlias(tipoSala.getApelidoNomeUnicoSpace());
            System.out.println(espaco.getCodigoChat());
            System.out.println("!sysqUVrbhFXRcPuBcH:casanovadigital.com.br");
            ComoChatSalaBean salaFilha = erpChatService.getSalaByAlias("#553184178550wv:casanovadigital.com.br");
            System.out.println(salaFilha.getCodigoChat());

            ComoUsuarioChat auxiliadora = erpChatService.getUsuarioByEmail("auxiliadora@casanovadigital.com.br");
            System.out.println(auxiliadora.getCodigoUsuario());

            System.out.println(FabApiRestIntMatrixChatSalas.SALA_ADICIONAR_USUARIO.getAcao(espaco.getCodigoChat(), auxiliadora.getCodigoUsuario()).getResposta().getRespostaTexto());

            System.out.println(FabApiRestIntMatrixChatSalas.SALA_ADICIONAR_USUARIO.getAcao(salaFilha.getCodigoChat(), auxiliadora.getCodigoUsuario()).getResposta().getRespostaTexto());

            System.out.println(espaco.getCodigoChat());
            System.out.println(salaFilha.getCodigoChat());
            ItfRespostaWebServiceSimples resp2 = FabApiRestIntMatrixSpaces.ESPACO_ADICIONAR_FILHO_DO_ESPACO
                    .getAcao(espaco.getCodigoChat(),
                            salaFilha.getCodigoChat()).getResposta();

            System.out.println(resp2.getRespostaTexto());

        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(TesteEspacos.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Codigo espaço:");

    }
}
