package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import com.google.common.collect.Lists;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Atendente;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import java.util.List;

/**
 *
 * @author salvio
 */
public class RotaEncaminhamentoSala extends RotaMensagemContato {

    private List<Contato> contatosParticipantes;
    private ComoChatSalaBean sala;
    private List<Atendente> atendentes;
    private Atendente atendentePrincipal;

    public RotaEncaminhamentoSala(Contato pContato, ComoChatSalaBean pSala, List<Contato> pContatosParticipantes, Atendente pAtendente, List<Atendente> pAtendentes) {
        super(FabTipoRotaMensagem.ENCAMINHAMENTO, pContato);
        this.contatosParticipantes = pContatosParticipantes;
        this.sala = pSala;
        this.atendentePrincipal = pAtendente;
        this.atendentes = pAtendentes;
    }

    public RotaEncaminhamentoSala(Contato pContato, ComoChatSalaBean pSala, Atendente pAtendente) {
        this(pContato, pSala, Lists.newArrayList(), pAtendente, Lists.newArrayList());
    }

    public List<Contato> getContatosParticipantes() {
        return contatosParticipantes;
    }

    public void setContatosParticipantes(List<Contato> contatosParticipantes) {
        this.contatosParticipantes = contatosParticipantes;
    }

    public ComoChatSalaBean getSala() {
        return sala;
    }

    public void setSala(ComoChatSalaBean sala) {
        this.sala = sala;
    }

    public List<Atendente> getAtendentes() {
        return atendentes;
    }

    public void setAtendentes(List<Atendente> atendentes) {
        this.atendentes = atendentes;
    }

    public Atendente getAtendentePrincipal() {
        return atendentePrincipal;
    }

    public void setAtendentePrincipal(Atendente atendentePrincipal) {
        this.atendentePrincipal = atendentePrincipal;
    }

}
