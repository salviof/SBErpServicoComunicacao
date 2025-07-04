/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.interpretadormsg.padrao.whatsapp;

import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author salvio
 */
public abstract class ProcessadorWtzpPadrao implements Runnable {

    private boolean processamentoFinalizado = false;
    protected boolean sucesso = false;

    public ProcessadorWtzpPadrao() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            processar();
        } finally {
            processamentoFinalizado = true;
        }
    }

    protected abstract void processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento;

    protected void aguardarProcessamento() {
        while (!processamentoFinalizado) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProcessadorWtzpPadrao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
