package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.TipoRota;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Pessoa;

/**
 *
 * @author salvio
 */
public class RotaMensagemContato implements ItfRotaMensagemDoContato {

    private Pessoa contatoPrincipal;
    private TipoRota tipoRota;

    public RotaMensagemContato(FabTipoRotaMensagem pTipoRota, Contato p) {
        id = p.getId();
        contatoPrincipal = p;
        tipoRota = pTipoRota.getRegistro();
    }

    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public TipoRota getTipoRota() {
        return tipoRota;
    }

    @Override
    public void setTipoRota(TipoRota tipoRota) {
        this.tipoRota = tipoRota;
    }

    @Override
    public RotaEncaminhamentoSala getComoRotaEncaminhamentoMatrix() {
        return (RotaEncaminhamentoSala) this;
    }

    @Override
    public RotaLinkAcesso getComoRotaLinkAcesso() {
        return (RotaLinkAcesso) this;
    }

    @Override
    public RotaMenuOpcoes getComoRotaMenuOpcoes() {
        return (RotaMenuOpcoes) this;
    }

    @Override
    public RotaWebservice getComoRotaWebService() {
        return (RotaWebservice) this;
    }

    @Override
    public Pessoa getContatoPrincipal() {
        return contatoPrincipal;
    }

}
