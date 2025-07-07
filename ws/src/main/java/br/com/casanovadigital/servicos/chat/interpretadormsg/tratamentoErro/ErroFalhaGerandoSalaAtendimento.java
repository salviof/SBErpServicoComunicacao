package br.com.casanovadigital.servicos.chat.interpretadormsg.tratamentoErro;

public class ErroFalhaGerandoSalaAtendimento extends Exception {

    public ErroFalhaGerandoSalaAtendimento(String pFalha) {
        super(pFalha);
    }

}
