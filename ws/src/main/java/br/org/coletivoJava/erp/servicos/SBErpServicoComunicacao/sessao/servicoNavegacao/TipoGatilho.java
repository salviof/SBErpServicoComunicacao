package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.model.ComandoDeAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfEventoMatix;

/**
 *
 * @author salvio
 */
public class TipoGatilho {

    private FabTipoGatilho tipoGatilho;
    private MensagemWhatsapp mensagemWtzp;
    private ItfEventoMatix evento;
    private ComandoDeAtendimento comando;

    public TipoGatilho(MensagemWhatsapp mensagemWtzp) {
        this.tipoGatilho = FabTipoGatilho.GATILHO_MENSAGEM_WHATSAPP;
        this.mensagemWtzp = mensagemWtzp;

    }

    public TipoGatilho(ComandoDeAtendimento pComando) {
        this.tipoGatilho = FabTipoGatilho.GATILHO_COMANDO_ATENDIMENTO;
        comando = pComando;
    }

    public TipoGatilho(ItfEventoMatix pEvento) {
        this.tipoGatilho = FabTipoGatilho.GATILHO_EVENTO_MATRIX;
        this.evento = pEvento;

    }

    public FabTipoGatilho getTipoGatilho() {
        return tipoGatilho;
    }

    public MensagemWhatsapp getMensagemWtzp() {
        return mensagemWtzp;
    }

    public ItfEventoMatix getEvento() {
        return evento;
    }

    public ComandoDeAtendimento getComando() {
        return comando;
    }

}
