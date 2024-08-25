/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens.MotorControleDeMensagens;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.statusMensagem.StatusMensagemWtzap;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;

/**
 *
 * @author salvio
 */
public class ApiWhatsappRecepcaoMensagem extends RotaSparkPadrao {

    @Override
    public void validarParamentros() throws ErroParamentosInvalidos {

    }

    @Override
    public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {

        for (MensagemWhatsapp msg : pacoteMensagem.getMensagens()) {
            try {
                EntradaNumeroWhatsapp entrada = MotorControleDeMensagens.getEntradaByCodigoEntrada(msg.getCodigoContaConectada());
                MotorControleDeMensagens.executarMensagemWhatsapp(entrada, msg);
                return "ok";
            } catch (Throwable t) {
                System.out.println("Falha processando mesnagem " + t.getMessage());
                throw new ErroConexaoSistemaTerceiro("Erro não tratado" + t.getMessage());
            }

        }
        for (StatusMensagemWtzap statusMensagem : pacoteMensagem.getStatusMensagem()) {
            try {
                MotorControleDeMensagens.executarMensagemStatusWhatsapp(statusMensagem.getEntrada(), statusMensagem);
            } catch (Throwable t) {
                System.out.println("Falha processando status" + t.getMessage());
                throw new ErroConexaoSistemaTerceiro("Erro não tratado" + t.getMessage());
            }
        }

        pacoteMensagem.getStatusMensagem();
        return "ok";
    }

}
