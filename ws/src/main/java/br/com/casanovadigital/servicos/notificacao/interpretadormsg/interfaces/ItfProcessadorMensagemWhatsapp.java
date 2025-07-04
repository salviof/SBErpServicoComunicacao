/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.interpretadormsg.interfaces;

import br.com.casanovadigital.servicos.notificacao.interpretadormsg.FabTipoRetornoEncaminhamentoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.com.casanovadigital.servicos.notificacao.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;

/**
 *
 * @author salvio
 */
public interface ItfProcessadorMensagemWhatsapp extends ItfProcessadorPayloadWhatsapp {

    public String getReciboEncaminhamentoMatrix();

    public String getLink();

    public String getCodigoSalaMatrixEncaminhamento();

    public FabTipoRetornoEncaminhamentoWhatsapp getTipoEncaminhamento();

    public MensagemWhatsapp getMensagemWhatsapp();

}
