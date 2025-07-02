/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato;

/**
 *
 * @author salvio
 */
public class ContatoWhatsapp {

    public ContatoWhatsapp(String pwaid, String pNome) throws ErroCriandoContato {
        wa_id = pwaid;
        nome = pNome;
        if (wa_id == null) {
            throw new ErroCriandoContato("Wa_id não enviado");
        }
        if (nome == null) {
            throw new ErroCriandoContato("Nome não pode ser nulo");
        }
    }

    private String wa_id;
    private String nome;

    public String getWa_id() {
        return wa_id;
    }

    public void setWa_id(String wa_id) {
        this.wa_id = wa_id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
