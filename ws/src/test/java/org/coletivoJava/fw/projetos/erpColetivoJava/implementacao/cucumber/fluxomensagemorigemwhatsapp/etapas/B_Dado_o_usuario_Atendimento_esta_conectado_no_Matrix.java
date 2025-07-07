package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas;

import br.com.casanovadigital.servicos.chat.AplicacaoWsChat;
import br.com.casanovadigital.servicos.chat.config.FabConfigServicoComunicacao;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.cucumber.fluxomensagemorigemwhatsapp.EtapasFluxoMensagemOrigemWhatsapp;
import cucumber.api.java.pt.Dado;
import java.lang.UnsupportedOperationException;
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

        ItfUsuarioChat usuarioAtendimento = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(FluxoMensagemOrigemWhatsapp.getPacoteEnvioMensagem().getMensagens().get(0).getEntrada(), FluxoMensagemOrigemWhatsapp.getPacoteEnvioMensagem().getMensagens().get(0).getContatoOrigem());
        Assert.assertNotNull(usuarioAtendimento);
    }
}
