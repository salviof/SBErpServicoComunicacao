package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.sessao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import org.coletivojava.fw.api.tratamentoErros.FabErro;

/**
 *
 * @author salvio
 */
public class SessaoDeContato {

    private ContextoContato contexto;
    private EntradaNumeroWhatsapp entrada;

    public SessaoDeContato(ContextoContato contexto, EntradaNumeroWhatsapp pEntrada) {
        this.contexto = contexto;
        this.entrada = pEntrada;

    }

    public void setContexto(ContextoContato contexto) {
        this.contexto = contexto;
    }

    public ContextoContato getContexto() {

        return contexto;
    }

    public ItfServicoNavegacao getServicoNavegacao() {
        try {
            return AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada);
        } catch (ErroComDevolucaoMensagemUsuario ex) {
            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha obtendo serviço de navegação", ex);
            return null;
        }

    }

}
