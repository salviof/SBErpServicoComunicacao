package br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens;

public class ErroFalhaEncaminhando extends Exception {

    public ErroFalhaEncaminhando(String pFalha) {
        super(pFalha);
    }

}
