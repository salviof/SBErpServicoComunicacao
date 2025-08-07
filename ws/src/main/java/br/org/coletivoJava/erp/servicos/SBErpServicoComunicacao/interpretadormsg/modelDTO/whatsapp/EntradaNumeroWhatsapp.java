package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp;

/**
 *
 * @author salvio
 */
public class EntradaNumeroWhatsapp {

    private String codigo;
    private String nome;
    private String telefonewa_id;
    private String telefoneDivulgacao;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefonewa_id() {
        return telefonewa_id;
    }

    public void setTelefonewa_id(String telefonewa_id) {
        this.telefonewa_id = telefonewa_id;
    }

    public String getTelefoneDivulgacao() {
        return telefoneDivulgacao;
    }

    public void setTelefoneDivulgacao(String telefoneDivulgacao) {
        this.telefoneDivulgacao = telefoneDivulgacao;
    }

    @Override
    public String toString() {
        return getCodigo();
    }

}
