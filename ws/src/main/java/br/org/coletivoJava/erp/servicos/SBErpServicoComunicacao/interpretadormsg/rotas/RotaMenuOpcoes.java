package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem;
import br.org.coletivoJava.integracoes.restIntwhatsapp.api.model.menu.MenuWhatsapp;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;

/**
 *
 * @author salvio
 */
public class RotaMenuOpcoes extends RotaMensagemContato {

    private MenuWhatsapp menuWhatsapp;

    public RotaMenuOpcoes(Contato p) {
        super(FabTipoRotaMensagem.MENU_OPCOES, p);
    }

    public MenuWhatsapp getMenuWhatsapp() {
        return menuWhatsapp;
    }

    public void setMenuWhatsapp(MenuWhatsapp menuWhatsapp) {
        this.menuWhatsapp = menuWhatsapp;
    }

}
