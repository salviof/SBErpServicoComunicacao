/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.comandosAtendimento;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.statusMensagem.EventoMensagemWtzap;
import br.org.coletivoJava.fw.api.erp.chat.model.ComandoDeAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.model.ErroComandoAtendimentoInvalido;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.eventos.EventoSalaMatrix;

/**
 *
 * @author salvio
 */
public class UtilSBComandosAtendimento {

    public static ComandoDeAtendimento gerarComandoAtendimento(ItfEventoMatix pEvento) throws ErroComandoAtendimentoInvalido {
        ComandoDeAtendimento comando = new ComandoDeAtendimento((EventoSalaMatrix) pEvento);
        return comando;
    }

}
