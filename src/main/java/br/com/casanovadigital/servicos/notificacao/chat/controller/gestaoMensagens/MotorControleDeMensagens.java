package br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens;

import br.com.casanovadigital.servicos.notificacao.chat.controller.listenerMatrix.ListenerNotificacaoMatrixAuxiliadora;
import br.com.casanovadigital.servicos.notificacao.chat.controller.listenerMatrix.ListenerSalaMatrix;
import br.com.casanovadigital.servicos.notificacao.chat.controller.notificacaoLeitura.EventoEncMatrixParaWtsap;
import br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens.ErroFalhaGerandoSalaAtendimento;
import br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens.ItfDefinicaoContextoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.recepcaoMensagens.ItfExecucaoLogicaRecepcaoWtzap;

import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.contato.ErroCriandoContato;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.entrada.EntradaNumeroWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.statusMensagem.StatusMensagemWtzap;
import br.com.casanovadigital.servicos.notificacao.contextoChat.matrix.ContextoMatrix;
import br.com.casanovadigital.servicos.notificacao.contextoChat.whatsapp.ContextoWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoEventoMatrix;
import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import br.org.coletivoJava.integracoes.whatsapp.config.FabConfigApiWhatsapp;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringTelefone;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringValidador;
import com.super_bits.modulosSB.SBCore.UtilGeral.json.ErroProcessandoJson;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivojava.fw.api.tratamentoErros.FabErro;

/**
 *
 * @author salvio
 */
public class MotorControleDeMensagens {

    private static final Map<String, ContextoWhatsapp> contextosWhatsappByCodigoContexto = new ConcurrentHashMap<>();

    private static final ControleDeReciboDeLeitura controleLeitura = new ControleDeReciboDeLeitura();

    private static List<EntradaNumeroWhatsapp> entradas = new ArrayList<>();
    private static boolean motorConfigurado = false;
    private static ItfDefinicaoContextoWhatsapp definicaoContexto;

    public static ControleDeReciboDeLeitura getControleRecibo() {
        return controleLeitura;
    }

    public static void configurarMotor() {
        if (motorConfigurado) {
            return;
        }
        loadEntradas();
        try {

            System.out.println("Registrando listener de salas");
            ContextoMatrix.getChatMatrixService().registrarClasseDeEscutaSalas(ListenerSalaMatrix.class);
            System.out.println("Listener de salas Registrado");
            System.out.println("Registrando listener de Notificações");
            ContextoMatrix.getChatMatrixService().registrarClasseEscutaNotificacoes(ListenerNotificacaoMatrixAuxiliadora.class);
            System.out.println("Listener de notificações registrado");

        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(MotorControleDeMensagens.class.getName()).log(Level.SEVERE, null, ex);
        }

        ServiceLoader<ItfDefinicaoContextoWhatsapp> services = ServiceLoader.load(ItfDefinicaoContextoWhatsapp.class);

        definicaoContexto = services.iterator().next();

        motorConfigurado = true;

    }

    private synchronized static void loadEntradas() {

        if (SBCore.getConfigModulo(FabConfigApiWhatsapp.class).getRepositorioDeArquivosExternos().getJsonObjeto("entradas.json").isEmpty()) {
            try {
                JsonObjectBuilder exemplo = UtilSBCoreJson.getJsonBuilderBySequenciaChaveValor("nomeAplicacao", SBCore.getNomeProjeto());
                JsonArrayBuilder entradasJson = Json.createArrayBuilder();
                entradasJson.add(UtilSBCoreJson.getJsonObjectBySequenciaChaveValor("codigo", "114354588403482", "nome", "Vendas Casanova", "telefonewa_id", "553121159755", "telefoneDivulgacao", "(31) 2115-9755"));
                exemplo.add("entradas", entradasJson.build());
                SBCore.getConfigModulo(FabConfigApiWhatsapp.class).getRepositorioDeArquivosExternos().putConteudoRecursoExterno("entradas.json", UtilSBCoreJson.getTextoByJsonObjeect(exemplo.build()));
            } catch (ErroProcessandoJson ex) {
                Logger.getLogger(MotorControleDeMensagens.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            JsonObject entradasJson = SBCore.getConfigModulo(FabConfigApiWhatsapp.class).getRepositorioDeArquivosExternos().getJsonObjeto("entradas.json");
            JsonArray entradasJsons = entradasJson.getJsonArray("entradas");
            for (JsonValue valor : entradasJsons) {
                JsonObject entradaJson = valor.asJsonObject();
                EntradaNumeroWhatsapp entrada = new EntradaNumeroWhatsapp();
                entrada.setCodigo(entradaJson.getString("codigo"));
                entrada.setNome(entradaJson.getString("nome"));
                entrada.setTelefonewa_id(entradaJson.getString("telefonewa_id"));
                entrada.setTelefoneDivulgacao(entradaJson.getString("telefoneDivulgacao"));
                entradas.add(entrada);
            }
        }
    }

    public static String gerarCodigoContextoWhatsapp(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato) {
        return pEntrada.getCodigo() + pContato.getWa_id();
    }

    public static EntradaNumeroWhatsapp getEntradaByCodigoEntrada(String pCodigo) {
        if (entradas.isEmpty()) {
            loadEntradas();
        }
        Optional<EntradaNumeroWhatsapp> pesquisaEntrada = entradas.stream().filter(et -> et.getCodigo().equals(pCodigo)).findFirst();
        if (pesquisaEntrada.isPresent()) {
            return pesquisaEntrada.get();
        }
        return null;
    }

    public static ItfDefinicaoContextoWhatsapp getDefinicaoContexto() {
        if (definicaoContexto == null) {
            configurarMotor();
        }
        return definicaoContexto;
    }

    public synchronized static EventoEncMatrixParaWtsap monitorGetCodEventoMatrixEncaminhamentoByTextoERemetente(ItfUsuarioChat pUsuario, String pTexto) {

        for (ContextoWhatsapp ctx : contextosWhatsappByCodigoContexto.values()) {
            for (EventoEncMatrixParaWtsap evento : ctx.getContextoMatrix().getUltimosEventosEncaminhado()) {
                try {
                    System.out.println(evento.getEventoOrigem().getSender());
                    ContextoMatrix.getChatMatrixService().getUsuarioByCodigo(evento.getEventoOrigem().getSender());
                    String texto = evento.getEventoOrigem().getContent().getString("body");
                    if (texto.equals(pTexto)) {
                        return evento;
                    }
                } catch (ErroConexaoServicoChat ex) {
                    Logger.getLogger(MotorControleDeMensagens.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ThreadDeath ex) {
                    SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Erro pesquisando mensagem enviada ", ex);
                }

            }
        }
        return null;
    }

    public synchronized static ItfExecucaoLogicaRecepcaoWtzap monitorGetProcessamentoWhatsappByMesagem(MensagemWhatsapp pMesamgem) {
        for (ContextoWhatsapp ctx : contextosWhatsappByCodigoContexto.values()) {
            for (ItfExecucaoLogicaRecepcaoWtzap treadMensagemRecebida : ctx.getMensagensRecebidas()) {

                if (treadMensagemRecebida.getMensagem().getId().equals(pMesamgem.getId())) {
                    if (treadMensagemRecebida.isEncaminhada()) {
                        return treadMensagemRecebida;
                    }
                }
            }
        }
        return null;
    }

    public synchronized static ContextoWhatsapp getContextoWhatsapp(EntradaNumeroWhatsapp pEntrada, ContatoWhatsapp pContato) {

        String codigoContexto = gerarCodigoContextoWhatsapp(pEntrada, pContato);
        if (!contextosWhatsappByCodigoContexto.containsKey(codigoContexto)) {

            ContextoWhatsapp contexto = getDefinicaoContexto().gerarNovoCotexto(pEntrada, pContato);
            contexto.setContextoMatrix(new ContextoMatrix(contexto));
            contextosWhatsappByCodigoContexto.put(contexto.getCodigoContexto(), contexto);
        }
        return contextosWhatsappByCodigoContexto.get(codigoContexto);
    }

    public static void executarMensagemWhatsapp(EntradaNumeroWhatsapp pEntrada, MensagemWhatsapp pMensagem) {
        getContextoWhatsapp(pEntrada, pMensagem.getContatoOrigem()).executarMensagem(pMensagem);
    }

    public static void executarMensagemStatusWhatsapp(EntradaNumeroWhatsapp pEntrada, StatusMensagemWtzap statusMensagem) {

        //  ItfUsuarioChat usuarioLead = MapAtendentesMatrix.getUsuarioWhatsappLead(sala);
        System.out.println("Processando status");
        System.out.println(statusMensagem.getTipoStatus());
        String codigoMensagem = statusMensagem.getCodigoMensagem();
        ContextoWhatsapp ctx = null;
        try {
            String codigowhatzap = statusMensagem.getCodigoMensagem();
            String codigoContexto = getControleRecibo().getCodigoContextoWtspByEventoWhatsapp(codigowhatzap);
            ctx = contextosWhatsappByCodigoContexto.get(codigoContexto);
            if (ctx == null) {
                throw new UnsupportedOperationException("Falha obtendo contexto pelo codigo do whatsapp");
            }
        } catch (Throwable t) {
            ItfUsuarioChat usuario;
            try {
                usuario = ContextoMatrix.getChatMatrixService().getUsuarioByTelefone(statusMensagem.getWaIdContatoOrigem());
                ContatoWhatsapp contato = new ContatoWhatsapp(statusMensagem.getWaIdContatoOrigem(), usuario.getNome());
                ctx = getContextoWhatsapp(pEntrada, contato);
            } catch (ErroConexaoServicoChat ex) {
                SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha identificando contexto do contato", t);
                try {
                    ItfUsuarioChat usuarioGestorInfra = ContextoMatrix.getChatMatrixService().getUsuarioByEmail("salvio@casanovadigital.com.br");
                    ContextoMatrix.getChatMatrixService().enviarDirect(usuarioGestorInfra.getCodigoUsuario(), "Falha notificando status " + statusMensagem.getDescricaoErro() + statusMensagem.getTipoStatus() + statusMensagem.getWaIdContatoOrigem());

                } catch (ErroConexaoServicoChat ex1) {

                }
                return;
            } catch (ErroCriandoContato ex) {
                SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha criando contato" + ex.getMessage(), t);
            }

        }
        if (ctx.getSalaMatrixAtiva() == null) {
            try {
                ctx.setSalaMatrixAtiva(getDefinicaoContexto().getSalaPadrao(pEntrada, ctx.getContatoWhatsapp()));
            } catch (ErroFalhaGerandoSalaAtendimento ex) {
                SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "falha registrando status, sala de atendimento referente não detectada", ex);
                return;
            }
        }
        try {

            switch ((statusMensagem.getTipoStatus())) {

                case FALHA_ENTREGA:

                    if (ctx.getSalaMatrixAtiva() != null) {

                        ctx.getContextoMatrix().getServicoChat().salaEnviarMesagem(ctx.getSalaMatrixAtiva(), statusMensagem.getDescricaoErro());// .encaminharMensagemWtzapParaSala(msg, pSala)            chatMatrixService.salaEnviarMesagem(sala, usuarioLead, null, );
                    }
                    break;

                case MENSAGEM_ENTREGUE:
                    //ctx.getContextoMatrix().getServicoChat().salaEnviarMesagem(ctx.getSalaMatrixAtiva(), null, null, "Mensagem entregue com sucesso no whatsapp " + ctx.getContatoWhatsapp().getNome());
                    break;
                case MENSAGEM_ENVIADA:
                    //        chatMatrixService.salaEnviarMesagem(sala, usuarioLead, null, "Mensagem enviada com sucesso no whatsapp de " + usuarioLead.getNome());
                    break;
                case MENSAGEM_LIDA:
                    ctx.getContextoMatrix().notificarLeituraWtsappNaSala(statusMensagem);
                    //ctx.declararLeitura(codMatrix);
                    break;
                case MENSAGEM_EXCLUIDA:
                    break;
                case DESCONHECIDO:
                    break;
                default:
                    throw new AssertionError();
            }
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(MotorControleDeMensagens.class.getName()).log(Level.SEVERE, null, ex);
        }

        JsonObjectBuilder resp = Json.createObjectBuilder();
        // JsonObject json = UtilSBCoreJson.getJsonObjectByTexto(requisicao.body());
        resp.add("retorno", "ok");
        //return "dUaZtgigZHazBCLBgmoDyY99fvhtWgfxfV5DMpjsM3luML4y73u9pLsbOTGHSWO2";

    }

    public static synchronized void executarEventoSalaMatrix(RoomEvent pEventoSala) {
        FabTipoEventoMatrix tipoEvento = FabTipoEventoMatrix.getTipoEventoByTypeStr(pEventoSala.getType());

        ItfChatSalaBean sala = null;
        try {
            sala = ContextoMatrix.getChatMatrixService().getSalaByCodigo(pEventoSala.getRoom_id());
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(MotorControleDeMensagens.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (tipoEvento) {
            case LEITURA:
                String codReciboEvento = pEventoSala.getContent().keys().next();
                String contextoWtzp = controleLeitura.getCodigoContextoWtspByEventoMatrix(codReciboEvento);
                ContextoWhatsapp ctx = contextosWhatsappByCodigoContexto.get(contextoWtzp);
                ctx.declararLeitura(codReciboEvento);

                break;

        }

        for (ItfUsuarioChat usuario : sala.getUsuarios()) {
            boolean atendimentoInterno = false;
            if (!UtilSBCoreStringValidador.isNuloOuEmbranco(usuario.getEmail())
                    && usuario.getEmail().contains(SBCore.getConfigModulo(FabConfigApiMatrixChat.class).getPropriedade(FabConfigApiMatrixChat.DOMINIO_FEDERADO))) {
                atendimentoInterno = true;
            }
            if (!atendimentoInterno) {
                boolean temCelular = !UtilSBCoreStringValidador.isNuloOuEmbranco(usuario.getTelefone());
                if (temCelular) {
                    EntradaNumeroWhatsapp entrada = getDefinicaoContexto().getEntradaWhatappBySala(sala);
                    ContatoWhatsapp contato;
                    try {
                        contato = new ContatoWhatsapp(UtilSBCoreStringTelefone.gerarCeluarWhatasapp(usuario.getTelefone()), usuario.getNome());

                        ContextoWhatsapp ctx = getContextoWhatsapp(entrada, contato);

                        if (tipoEvento != null) {
                            switch (tipoEvento) {

                                case MENSAGEM:
                                    String codigoReciboWhatsapp = ctx.getContextoMatrix().encaminharMensagemParaWtsapp(pEventoSala);

                                    controleLeitura.registrarEventoEncaminhamentoMatrixParaWhatsapp(ctx.getCodigoContexto(), pEventoSala, codigoReciboWhatsapp);

                                    break;
                                case DIGITANDO:
                                    ctx.getContextoMatrix().motitificarLeituraMatrixNoWtsapp(pEventoSala);
                                    break;

                            }

                        }

                    } catch (ErroCriandoContato ex) {
                        SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha criando contato " + ex.getMessage(), ex);
                    }
                }

            }

        }

    }

}
