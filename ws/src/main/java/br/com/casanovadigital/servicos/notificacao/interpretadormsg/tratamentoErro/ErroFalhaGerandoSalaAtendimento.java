package br.com.casanovadigital.servicos.notificacao.interpretadormsg.tratamentoErro;

public class ErroFalhaGerandoSalaAtendimento extends Exception {

    public ErroFalhaGerandoSalaAtendimento(String pFalha) {
        super(pFalha);
    }

}
