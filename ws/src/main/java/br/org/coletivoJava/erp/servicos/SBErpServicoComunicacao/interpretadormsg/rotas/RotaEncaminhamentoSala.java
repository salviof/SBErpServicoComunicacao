package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
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
    private ItfChatSalaBean sala;
    private List<Atendente> atendentes;
    private Atendente atendentePrincipal;

    public RotaEncaminhamentoSala(Contato pContato, ItfChatSalaBean pSala, List<Contato> pContatosParticipantes, Atendente pAtendente, List<Atendente> pAtendentes) {
        super(FabTipoRotaMensagem.ENCAMINHAMENTO, pContato);
        this.contatosParticipantes = pContatosParticipantes;
        this.sala = pSala;
        this.atendentePrincipal = pAtendente;
        this.atendentes = pAtendentes;
    }

    public RotaEncaminhamentoSala(Contato pContato, ItfChatSalaBean pSala, Atendente pAtendente) {
        this(pContato, pSala, Lists.newArrayList(), pAtendente, Lists.newArrayList());
    }

    public List<Contato> getContatosParticipantes() {
        return contatosParticipantes;
    }

    public void setContatosParticipantes(List<Contato> contatosParticipantes) {
        this.contatosParticipantes = contatosParticipantes;
    }

    public ItfChatSalaBean getSala() {
        return sala;
    }

    public void setSala(ItfChatSalaBean sala) {
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
