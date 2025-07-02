package br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens;

import br.com.casanovadigital.servicos.notificacao.chat.controller.notificacaoLeitura.EventoEncMatrixParaWtsap;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.PacoteMemensagemRecebidoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ErroCriandoContato;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import br.com.casanovadigital.servicos.notificacao.config.ConfigCoreCNDNotificacaoContato;
import br.com.casanovadigital.servicos.notificacao.contextoChat.matrix.ContextoMatrix;
import br.com.casanovadigital.servicos.notificacao.contextoChat.whatsapp.ContextoWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.integracoes.matrixChat.FabApiRestIntMatrixChatUsuarios;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author salvio
 */
public class TesteNotificacaoLeituraDoClienteWhatsapp {

    String mensagemWtspAberturaConversaPeloCliente = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\","
            + "\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},"
            + "\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],"
            + "\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSMjIyQjExNkE0NUU2OTJGQjFFAA==\""
            + ",\"timestamp\":\"1722276051\",\"text\":{\"body\":\"ppppp6\"},\"type\":\"text\"}]},"
            + "\"field\":\"messages\"}]}]}";
    String mensagemEnvioMatrix = "Olá, posso te ajudar com uma resposta teste?";
    String notificacaoLeiuraWhatsapp = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\","
            + "\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},"
            + "\"statuses\":[{\"id\":\"[CODIGO WHATSAPP MENSAGEM ENCAMINHADA]\",\"status\":\"read\",\"timestamp\":\"1723162703\",\"recipient_id\":\"553184178550\"}]},\"field\":\"messages\"}]}]}";
    private final String emailAtendente = "salvio@casanovadigital.com.br";
    private final String notificacaoFalhaEntrega = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\","
            + "\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553184178550\","
            + "\"phone_number_id\":\"103007756220088\"},"
            + "\"statuses\":[{\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSOTlBMjVCQTkwQTAzMkJFOTAxAA==\","
            + "\"status\":\"failed\",\"timestamp\":\"1723090973\",\"recipient_id\":"
            + "\"553184178550\",\"errors\":[{\"code\":131047,\"title\":\"Re-engagement message\",\"message\":\"Re-engagement message\","
            + "\"error_data\":{\"details\":\"Message failed to send because more than 24 hours have passed since the customer last replied to this number.\"},\"href\":\"https:\\/\\/developers.facebook.com\\/docs\\/whatsapp\\/cloud-api\\/support\\/error-codes\\/\"}]}]},\"field\":\"messages\"}]}]}";

    private ContextoWhatsapp contexto;

    public void testeSimpples() throws ErroCriandoContato {
        SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.HOMOLOGACAO);
        MotorControleDeMensagens.configurarMotor();
        String notificaca = "wamid.HBgMNTUzMTg0MTc4NTUwFQIAERgSODE3RUY1MzA5QUNCRUREOTNBAA==";
        notificacaoLeiuraWhatsapp = notificacaoLeiuraWhatsapp.replace("[CODIGO WHATSAPP MENSAGEM ENCAMINHADA]", notificaca);
        PacoteMemensagemRecebidoWhatsapp pacote = new PacoteMemensagemRecebidoWhatsapp(notificacaoLeiuraWhatsapp);
        MotorControleDeMensagens.executarMensagemStatusWhatsapp(pacote.getStatusMensagem().get(0).getEntrada(), pacote.getStatusMensagem().get(0));

    }

    @Test
    public void testeNotificacaoStatusNaoEntregue() throws ErroCriandoContato {
        SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.HOMOLOGACAO);
        MotorControleDeMensagens.configurarMotor();
        System.out.println(notificacaoFalhaEntrega);
        PacoteMemensagemRecebidoWhatsapp pacoteMensagemFalha = new PacoteMemensagemRecebidoWhatsapp(notificacaoFalhaEntrega);
        try {
            Thread.sleep(15000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TesteNotificacaoLeituraDoClienteWhatsapp.class.getName()).log(Level.SEVERE, null, ex);
        }
        MotorControleDeMensagens.executarMensagemStatusWhatsapp(pacoteMensagemFalha.getStatusMensagem().get(0).getEntrada(), pacoteMensagemFalha.getStatusMensagem().get(0));
    }

    public void testeTokenUsuario() throws ErroCriandoContato {
        SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.HOMOLOGACAO);
        System.out.println(mensagemWtspAberturaConversaPeloCliente);
        System.out.println(notificacaoFalhaEntrega);
        System.out.println(notificacaoLeiuraWhatsapp);
        MotorControleDeMensagens.configurarMotor();
        SimulacaoEnvioMensagemWhatsapp simular = new SimulacaoEnvioMensagemWhatsapp(mensagemWtspAberturaConversaPeloCliente);
        simular.start();
        PacoteMemensagemRecebidoWhatsapp pct = new PacoteMemensagemRecebidoWhatsapp(mensagemWtspAberturaConversaPeloCliente);
        ContatoWhatsapp ctWtsap = pct.getMensagens().get(0).getContatoOrigem();
        ItfUsuarioChat usuarioChat = ContextoMatrix.getUsuarioChatRelacionado(ctWtsap);
        System.out.println(usuarioChat.getId());
        FabApiRestIntMatrixChatUsuarios.USUARIOS_DA_SALA.getGestaoToken(usuarioChat).getToken();

    }

    public void testeIntegracao() throws ErroCriandoContato {
        SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.HOMOLOGACAO);
        MotorControleDeMensagens.configurarMotor();
        //1 enviar Mensagem atendente
        SimulacaoEnvioMensagemWhatsapp simular = new SimulacaoEnvioMensagemWhatsapp(mensagemWtspAberturaConversaPeloCliente);
        simular.start();

        while (1 == 1) {
            System.out.println("up");
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TesteNotificacaoLeituraDoClienteWhatsapp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void testeIntegracaoNotificação() throws ErroCriandoContato {
//        SBCore.configurar(new ConfigCoreCNDNotificacaoContato(), SBCore.ESTADO_APP.HOMOLOGACAO);
//        MotorControleDeMensagens.configurarMotor();
        //1 enviar Mensagem atendente
        SimulacaoEnvioMensagemWhatsapp simular = new SimulacaoEnvioMensagemWhatsapp(mensagemWtspAberturaConversaPeloCliente);
        simular.start();

        //2 recebe a Mensagem no Matrix
        String reciboMarrixTransientAberturaSala = simular.aguardarReciboMatrix();
        ContextoWhatsapp contextoWtsp = getContexto();
        //3 atendente responde -> Não é possível simular, enviar pelo flufy matrix.casanovadigital.com.br
        //String tokenEnvioMatrix = ContextoMatrix.getChatMatrixService().salaEnviarMesagem(contextoWtsp.getSalaMatrixAtiva(), Util, mensagemEnvioMatrix, mensagemEnvioMatrix);
        boolean encaminhamentoWhatsappRealizado = false;
        String codigoEventoWhatsapp = "";

        ItfUsuarioChat usuario = getUsuarioAtendimento();
        while (!encaminhamentoWhatsappRealizado) {
            System.out.println("Mensagem enviada manualmente via flfy, do usuario " + emailAtendente + " com o texto:" + mensagemEnvioMatrix + " não foi econtrada");
            EventoEncMatrixParaWtsap eventoEncaminhamentoMatrixParaCliente = MotorControleDeMensagens.monitorGetCodEventoMatrixEncaminhamentoByTextoERemetente(usuario, mensagemEnvioMatrix); //String codigoEventoWhatsapp = MotorControleDeMensagens.getControleRecibo().getCodigoEventoWhatsappByCodigoMatrix(mensagemEnvioMatrix);

            if (eventoEncaminhamentoMatrixParaCliente != null) {
                encaminhamentoWhatsappRealizado = true;
                codigoEventoWhatsapp = eventoEncaminhamentoMatrixParaCliente.getCodigoEventoWhatsapp();
            }

            try {
                if (!encaminhamentoWhatsappRealizado) {
                    Thread.sleep(30000);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(TesteNotificacaoLeituraDoClienteWhatsapp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //4 o Whatsapp envia uma notificação informando a leitura dessa mensagem
        //JsonObjectBuilder jsonEventoLeituraSimulacao = Json.createObjectBuilder(notificacaoLeiuraWhatsapp);

        notificacaoLeiuraWhatsapp = notificacaoLeiuraWhatsapp.replace("[CODIGO WHATSAPP MENSAGEM ENCAMINHADA]", codigoEventoWhatsapp);
        PacoteMemensagemRecebidoWhatsapp pacote = new PacoteMemensagemRecebidoWhatsapp(notificacaoLeiuraWhatsapp);
        MotorControleDeMensagens.executarMensagemStatusWhatsapp(pacote.getStatusMensagem().get(0).getEntrada(), pacote.getStatusMensagem().get(0));
        PacoteMemensagemRecebidoWhatsapp pacoteMensagem = new PacoteMemensagemRecebidoWhatsapp(notificacaoFalhaEntrega);
        MotorControleDeMensagens.executarMensagemStatusWhatsapp(pacoteMensagem.getStatusMensagem().get(0).getEntrada(), pacoteMensagem.getStatusMensagem().get(0));

    }

    private ItfUsuarioChat getUsuarioAtendimento() {
        try {
            ItfUsuarioChat usuarioAtendimento = ContextoMatrix.getChatMatrixService().getUsuarioByEmail(emailAtendente);
            return usuarioAtendimento;
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(TesteNotificacaoLeituraDoClienteWhatsapp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private ContextoWhatsapp getContexto() throws ErroCriandoContato {
        EntradaNumeroWhatsapp entrada = MotorControleDeMensagens.getEntradaByCodigoEntrada("114354588403482");
        PacoteMemensagemRecebidoWhatsapp pctmensam = new PacoteMemensagemRecebidoWhatsapp(mensagemWtspAberturaConversaPeloCliente);
        ContatoWhatsapp contato = pctmensam.getMensagens().get(0).getContatoOrigem();

        ContextoWhatsapp ctx = MotorControleDeMensagens.getContextoWhatsapp(entrada, contato);
        return ctx;
    }

}
