package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.com.casanovadigital.servicos.chat.config.FabConfigServicoComunicacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;

import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.cucumber.fluxomensagemorigemwhatsapp.EtapasFluxoMensagemOrigemWhatsapp;
import cucumber.api.java.pt.Dado;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.FluxoMensagemOrigemWhatsapp;
import org.junit.Assert;

public class B_Dado_o_usuario_Atendimento_esta_conectado_no_Matrix {

    @Dado(EtapasFluxoMensagemOrigemWhatsapp.E_O_USUARIO_ATENDIMENTO_ESTA_CONECTADO_NO_MATRIX)
    public void implementacaoEtapa() {

        if (FabConfigApiMatrixChat.USUARIO_ADMIN.getValorPadrao().equals(FabConfigApiMatrixChat.USUARIO_ADMIN.getValorParametroSistema())) {
            Assert.fail("COnfigure o usuario e senha administrativo  em " + SBCore.getConfigModulo(FabConfigApiMatrixChat.class).getPatchArquivoConfig());
        }
        if (FabConfigServicoComunicacao.USUARIO_ATENDIMENTO_PADRAO.getValorPadrao().equals(FabConfigServicoComunicacao.USUARIO_ATENDIMENTO_PADRAO.getValorParametroSistema())) {
            Assert.fail("Configure o usuário atendimento padrão " + SBCore.getConfigModulo(FabConfigServicoComunicacao.class).getPatchArquivoConfig());
        }
        MensagemWhatsapp msg = FluxoMensagemOrigemWhatsapp.getPacoteEnvioMensagem().getMensagens().get(0);
        Contato contato;
        try {
            contato = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato(msg.getContatoOrigem());
            ItfUsuarioChat usuarioAtendimento = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(msg.getEntrada(), contato);
            Assert.assertNotNull(usuarioAtendimento);
        } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat ex) {
            Logger.getLogger(B_Dado_o_usuario_Atendimento_esta_conectado_no_Matrix.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail("Falhou criando contato");
        }

    }
}
