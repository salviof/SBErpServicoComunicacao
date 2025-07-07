/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.interpretadormsg.interfaces;

import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;

/**
 *
 * @author salvio
 */
public interface ItfProcessadorPayloadWhatsapp {

    public boolean isSucesso() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento;
}
