package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoClient;


import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config.ConfigCoreServicoComunicacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappMensagem;
import br.org.coletivoJava.integracoes.whatsapp.config.FabConfigApiWhatsapp;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreBytes;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.api.token.ItfTokenGestao;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class ServicoWhatsappTest {

    private final EntradaNumeroWhatsapp entradaNumeroWhatsapp = new EntradaNumeroWhatsapp();
    private final Contato contato = new Contato();
    @Before
    public void setUp() {
        SBCore.configurar(new ConfigCoreServicoComunicacao(), SBCore.ESTADO_APP.DESENVOLVIMENTO);

    }


    @Test
    public void testEnviarImagem() throws Exception, ErroConexaoServicoChat {

        byte[] arquivo = UtilSBCoreBytes.gerarBytesPorArquivo(new File("/home/superBits/projetos/coletivoJava/source/erpColetivoJava/SBErpServicoComunicacao/ws/src/test/resources/arquivos/teste.pdf"));
        contato.setWaid("5531986831481");
        entradaNumeroWhatsapp.setCodigo("103007756220088");
        AplicacaoWsChat.SERVICO_WHATSAPP.enviarImagem(entradaNumeroWhatsapp, contato, arquivo, "pdfDeTeste");
    }

    @Test
    public void testEnviarAudio() {
    }

    @Test
    public void testEnviarPdf() {


    }
}