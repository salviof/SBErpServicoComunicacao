package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import com.super_bits.modulosSB.SBCore.modulos.erp.ItfSistemaERP;
import java.net.URL;
import java.util.List;

/**
 *
 * @author salvio
 */
public interface ItfCentralLogicasProcessamentoMsg {

    public Class<? extends ItfServicoNavegacao> getClasseServicoNavegacao(EntradaNumeroWhatsapp pEntrada);

//    public Class getClasseDadosContextoIntranetAtendimento(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato);
    public ItfUsuarioChat getUsuarioAtendimentoPadrao(EntradaNumeroWhatsapp pEntrada, Contato pContato);

    public EntradaNumeroWhatsapp getEntradaBySala(String pApelidoSala) throws
            ErroRegraDeNegocio;

    public List<ItfSistemaERP> getSistemas();

    public boolean isSalaAutomonitoravel(String pApelidoSala);

    public URL gerarRedirecionamentoServicoWtzp(EntradaNumeroWhatsapp pEntrada, MensagemWhatsapp pMensagem);

    public List<EntradaNumeroWhatsapp> gerarEntradas();

    public void inicializacaoServicosTerceiros();

}
