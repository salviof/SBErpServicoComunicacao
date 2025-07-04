package br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro;

public class ErroFalhaEncaminhando extends Exception {

    public ErroFalhaEncaminhando(String pFalha) {
        super(pFalha);
    }

}
