package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas;

import br.com.casanovadigital.servicos.chat.AplicacaoWsChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.cucumber.fluxomensagemorigemwhatsapp.EtapasFluxoMensagemOrigemWhatsapp;
import cucumber.api.java.pt.Entao;
import java.lang.UnsupportedOperationException;
import org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.FluxoMensagemOrigemWhatsapp;

public class D_Entao_a_mensagem_Ola_tudo_bem_e_encaminhada_para_o_usuario_Atendimento_no_Matrix {

    @Entao(EtapasFluxoMensagemOrigemWhatsapp.ENTAO_A_MENSAGEM_OLA_TUDO_BEM_E_ENCAMINHADA_PARA_O_USUARIO_ATENDIMENTO_NO_MATRIX)
    public void implementacaoEtapa() {

        ItfUsuarioChat usuarioAtendimento = AplicacaoWsChat.getCentralLogicaProcesasmento().getUsuarioAtendimentoPadrao(
                FluxoMensagemOrigemWhatsapp.getPacoteEnvioMensagem().getMensagens().get(0).getEntrada(),
                FluxoMensagemOrigemWhatsapp.getPacoteEnvioMensagem().getMensagens().get(0).getContatoOrigem());

        //AplicacaoWsChat.SERVICO_MATRIX.salaNotificarLeitura(pSala, usuarioAtendimento, pCodigoReciboMatix)
    }
}
