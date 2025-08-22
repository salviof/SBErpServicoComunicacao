package com.super_bits.casanovadigital.servicos.messagens.model.agente;

import com.super_bits.modulosSB.Persistencia.registro.persistidos.ListenerEntidadePadrao;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoObjetoSB;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@InfoObjetoSB(tags = {"Contato "}, plural = "Contatos", icone = "fa fa-user")
@EntityListeners(ListenerEntidadePadrao.class)
public class Contato extends Pessoa {

    private String waid;

    private String jsonDadosDoContexto;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHoraUltimaInteracao;

    @OneToMany(mappedBy = "contato", targetEntity = ContextoContato.class)
    private List<ContextoContato> contextos;

    public String getWaid() {
        return waid;
    }

    public void setWaid(String waid) {
        this.waid = waid;
    }

    public String getJsonDadosDoContexto() {
        return jsonDadosDoContexto;
    }

    public void setJsonDadosDoContexto(String jsonDadosDoContexto) {
        this.jsonDadosDoContexto = jsonDadosDoContexto;
    }

    public Date getDataHoraUltimaInteracao() {
        return dataHoraUltimaInteracao;
    }

    public void setDataHoraUltimaInteracao(Date dataHoraUltimaInteracao) {
        this.dataHoraUltimaInteracao = dataHoraUltimaInteracao;
    }

}
