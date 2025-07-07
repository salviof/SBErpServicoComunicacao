/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.interpretadormsg.interfaces;

import br.com.casanovadigital.servicos.chat.interpretadormsg.FabTipoRetornoEncaminhamentoWhatsapp;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.mensagem.MensagemWhatsapp;

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
