package br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro;

public class ErroFalhaEncaminhando extends Exception {

    public ErroFalhaEncaminhando(String pFalha) {
        super(pFalha);
    }

}
