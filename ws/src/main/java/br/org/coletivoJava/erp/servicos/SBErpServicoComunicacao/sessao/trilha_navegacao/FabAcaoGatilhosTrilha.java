/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;

/**
 *
 * @author salvio
 */
public enum FabAcaoGatilhosTrilha {

    ENCERRAR_SESSAO,
    NOVA_ROTA,
    MENSAGEM_CONTATO_WHATSAPP,
    MENSAGEM_ATENDIMENTO;

    public AcaoGatilhoTrilha getAcao(ItfTrilhaNavegacao pTrilha, ContextoContato pContexto, String... parametros) {
        AcaoGatilhoTrilha acao = new AcaoGatilhoTrilha(this, pTrilha, pContexto);
        switch (this) {
            case ENCERRAR_SESSAO:
                break;
            case NOVA_ROTA:
                acao.setNovaRota(parametros[0]);
                break;
            case MENSAGEM_CONTATO_WHATSAPP:
                acao.setMensagemParaContato(parametros[0]);
                break;
            case MENSAGEM_ATENDIMENTO:
                acao.setMensagemParaAtendimento(parametros[0]);
                break;

            default:
                throw new AssertionError();
        }
        return acao;
    }

}
