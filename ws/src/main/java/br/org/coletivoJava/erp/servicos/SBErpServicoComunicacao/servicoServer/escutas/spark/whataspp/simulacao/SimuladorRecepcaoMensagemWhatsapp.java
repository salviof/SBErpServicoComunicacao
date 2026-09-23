package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.spark.whataspp.simulacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaMenuOpcoes;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaMensagemContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem;
import br.org.coletivoJava.integracoes.restIntwhatsapp.api.model.menu.ItemMenuWhatsapp;
import br.org.coletivoJava.integracoes.restIntwhatsapp.api.model.menu.MenuWhatsapp;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import jakarta.json.JsonObjectBuilder;
import com.super_bits.modulosSB.SBCore.ConfigGeral.CarameloCode;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCJson;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.coletivojava.fw.api.tratamentoErros.FabErro;
import spark.Request;
import spark.Response;
import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Formulário de testes que monta um payload de webhook do WhatsApp Cloud API a
 * partir de nome, telefone e texto, e o injeta no serviço como se tivesse
 * chegado pelo endpoint /api/v1/whatsapp/recepcao/notificacao.
 *
 * Disponível apenas fora do modo produção.
 *
 * @author salvio
 */
public class SimuladorRecepcaoMensagemWhatsapp {

    public static final String CAMINHO_FORMULARIO = "/web/testes/whatzapp";
    private static final String TAG_LOG = "[SIMULADOR-WTZP]";
    private static final String CODIGO_CONTA_SIMULADA = "SIMULADOR";
    /**
     * Contato fictício sugerido na primeira carga do formulário. Precisa ser um
     * número que não exista no WhatsApp, porque a simulação entrega mensagem de
     * verdade e cria/renomeia o usuário Matrix do contato.
     */
    private static final String NOME_FICTICIO = "Fulano de Teste";
    private static final String TELEFONE_FICTICIO = "5531900000000";
    private static final int QUADROS_DE_PILHA_EXIBIDOS = 12;

    public static void registrarRotas() {
        if (CarameloCode.isEmModoProducao()) {
            return;
        }
        for (String caminho : new String[]{CAMINHO_FORMULARIO, CAMINHO_FORMULARIO + "/"}) {
            get(caminho, (requisicao, resposta) -> responder(resposta, gerarPagina(DadosSimulacao.getPadrao(), null)));
            post(caminho, (requisicao, resposta) -> {
                DadosSimulacao dados = DadosSimulacao.getByRequisicao(requisicao);
                return responder(resposta, gerarPagina(dados, executarAcao(dados)));
            });
        }
        log(FabMensagens.AVISO, "Simulador de recepção de mensagem whatsapp disponível em " + CAMINHO_FORMULARIO);
    }

    private static String responder(Response pResposta, String pHtml) {
        pResposta.type("text/html; charset=UTF-8");
        return pHtml;
    }

    private static final String ACAO_VERIFICAR_MENU = "verificarMenu";
    private static final String ACAO_CLICAR_BOTAO = "clicarBotao";

    private static Resultado executarAcao(DadosSimulacao pDados) {
        if (ACAO_VERIFICAR_MENU.equals(pDados.getAcao())) {
            return verificarMenu(pDados);
        }
        if (ACAO_CLICAR_BOTAO.equals(pDados.getAcao())) {
            return clicarBotao(pDados);
        }
        return simular(pDados);
    }

    /**
     * Descobre em qual menu o contato está e devolve os botões clicáveis.
     *
     * Se a rota atual dele não tem menu (ou ele ainda não tem trilha), envia a
     * palavra "menu" - que leva à rota com menu mais próxima - e consulta de
     * novo.
     */
    private static Resultado verificarMenu(DadosSimulacao pDados) {

        EntradaNumeroWhatsapp entrada;
        try {
            entrada = getEntrada(pDados);
        } catch (ErroRegraDeNegocio ex) {
            return Resultado.falha(ex.getMessage(), null);
        }
        if (pDados.getTelefone().isEmpty()) {
            return Resultado.falha("Informe o telefone do contato para verificar o menu.", null);
        }

        MenuWhatsapp menu = getMenuDaRotaAtual(entrada, pDados.getTelefone());
        if (menu != null) {
            return Resultado.sucesso("O contato já está numa rota com menu. Botões disponíveis abaixo.", null)
                    .comMenu(menu);
        }

        log(FabMensagens.AVISO, "Contato " + pDados.getTelefone()
                + " não está numa rota com menu; enviando a palavra \"menu\" para chegar à mais próxima.");

        Resultado envioMenu = injetar(pDados, entrada,
                gerarPayloadTexto(pDados, entrada, "menu"),
                "Palavra \"menu\" enviada");
        if (!envioMenu.isSucesso()) {
            return envioMenu;
        }

        menu = getMenuDaRotaAtual(entrada, pDados.getTelefone());
        if (menu == null) {
            return Resultado.sucesso("A palavra \"menu\" foi processada, mas a rota resultante não tem menu de"
                    + " opções (pode ser encaminhamento direto para uma sala). Nada para clicar.",
                    envioMenu.getPayload());
        }
        return Resultado.sucesso("Menu obtido depois de enviar a palavra \"menu\". Botões disponíveis abaixo.",
                envioMenu.getPayload()).comMenu(menu);
    }

    /**
     * Simula o clique num botão do menu: o pacote é uma mensagem interativa
     * do tipo button_reply, igual à que a Meta envia quando o contato toca no
     * botão.
     */
    private static Resultado clicarBotao(DadosSimulacao pDados) {

        EntradaNumeroWhatsapp entrada;
        try {
            entrada = getEntrada(pDados);
        } catch (ErroRegraDeNegocio ex) {
            return Resultado.falha(ex.getMessage(), null);
        }
        if (pDados.getBotao().isEmpty()) {
            return Resultado.falha("Informe o id do botão (o mesmo id que o menu envia).", null);
        }

        Resultado resultado = injetar(pDados, entrada, gerarPayloadBotao(pDados, entrada),
                "Clique no botão \"" + pDados.getBotao() + "\" processado");

        // Depois do clique o contato pode ter caído em outro menu: já devolve os
        // botões novos para continuar a navegação sem recarregar na mão.
        MenuWhatsapp menuPosClique = getMenuDaRotaAtual(entrada, pDados.getTelefone());
        return menuPosClique == null ? resultado : resultado.comMenu(menuPosClique);
    }

    /**
     * Menu da rota em que o contato está, ou nulo quando ele não tem contato,
     * trilha, rota, ou a rota não é de menu de opções.
     */
    private static MenuWhatsapp getMenuDaRotaAtual(EntradaNumeroWhatsapp pEntrada, String pTelefone) {
        try {
            Contato contato = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato(pTelefone);
            if (contato == null) {
                return null;
            }
            ContextoContato contexto = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT
                    .getContextoContato(pEntrada, contato);
            if (contexto == null) {
                return null;
            }
            ItfTrilhaNavegacao trilha = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getTrilhaAtualDoContato(contexto);
            if (trilha == null || trilha.getRotaAtual() == null) {
                return null;
            }
            RotaMensagemContato rota = trilha.getRotaAtual();
            if (rota.getTipoRota() == null
                    || !FabTipoRotaMensagem.MENU_OPCOES.equals(rota.getTipoRota().getTipoRotaMensagem())) {
                return null;
            }
            MenuWhatsapp menu = ((RotaMenuOpcoes) rota).getComoRotaMenuOpcoes().getMenuWhatsapp();
            if (menu == null || menu.getItensMenu() == null || menu.getItensMenu().isEmpty()) {
                return null;
            }
            return menu;
        } catch (Throwable t) {
            log(FabMensagens.ALERTA, "Falha consultando o menu atual do contato " + pTelefone + ": "
                    + t.getClass().getSimpleName() + " - " + t.getMessage());
            return null;
        }
    }

    private static EntradaNumeroWhatsapp getEntrada(DadosSimulacao pDados) throws ErroRegraDeNegocio {
        if (pDados.getCodigoEntrada().isEmpty()) {
            throw new ErroRegraDeNegocio("Informe o código da entrada (phone_number_id) que receberá a mensagem.");
        }
        return AplicacaoWsChat.getEntradaByCodigoEntrada(pDados.getCodigoEntrada());
    }

    /**
     * Entrega o pacote ao processamento real, com o mesmo tratamento de erro da
     * simulação de texto.
     */
    private static Resultado injetar(DadosSimulacao pDados, EntradaNumeroWhatsapp pEntrada, JsonObject pPacote,
            String pDescricaoSucesso) {

        String payload = UtilCRCJson.getTextoByJsonObjeect(pPacote);
        log(FabMensagens.AVISO, "Injetando pacote simulado do telefone " + pDados.getTelefone()
                + " na entrada " + pEntrada.getCodigo());
        try {
            String retorno = AplicacaoWsChat.injetarPacoteWhatsapp(payload);
            return Resultado.sucesso(pDescricaoSucesso + ". Retorno do serviço: " + retorno, payload);
        } catch (Throwable t) {
            String pilha = getPilha(t);
            log(FabMensagens.ERRO, "Falha processando pacote simulado: "
                    + t.getClass().getName() + ": " + t.getMessage() + " | " + pilha.replace("\n", " <- "));
            CarameloCode.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha processando pacote whatsapp simulado", t);
            return Resultado.falha("Falha processando a mensagem: " + t.getClass().getSimpleName()
                    + (t.getMessage() == null ? "" : " - " + t.getMessage()), payload, pilha);
        }
    }

    /**
     * Monta o pacote e o entrega ao processamento real de mensagens recebidas.
     */
    private static Resultado simular(DadosSimulacao pDados) {

        if (pDados.getTelefone().isEmpty()) {
            return Resultado.falha("Informe o telefone do contato (somente números, com DDI e DDD).", null);
        }
        if (pDados.getTexto().isEmpty()) {
            return Resultado.falha("Informe o texto da mensagem.", null);
        }
        if (pDados.getCodigoEntrada().isEmpty()) {
            return Resultado.falha("Informe o código da entrada (phone_number_id) que receberá a mensagem.", null);
        }

        EntradaNumeroWhatsapp entrada;
        try {
            entrada = AplicacaoWsChat.getEntradaByCodigoEntrada(pDados.getCodigoEntrada());
        } catch (ErroRegraDeNegocio ex) {
            return Resultado.falha("Entrada não reconhecida: " + ex.getMessage(), null);
        }

        // Exceções sem mensagem (NullPointerException, por exemplo) só são
        // identificáveis pela pilha, por isso ela vai para o log e para a tela.
        Resultado resultado = injetar(pDados, entrada, gerarPayloadTexto(pDados, entrada, pDados.getTexto()),
                "Mensagem processada pelo serviço");

        // Se a resposta do serviço foi um menu, já mostra os botões dele.
        MenuWhatsapp menu = getMenuDaRotaAtual(entrada, pDados.getTelefone());
        return menu == null ? resultado : resultado.comMenu(menu);
    }

    /**
     * Origem da exceção: causa raiz, quando houver, e os primeiros quadros de
     * pilha, que é o que localiza um erro sem mensagem.
     */
    private static String getPilha(Throwable pErro) {
        Throwable erro = pErro;
        StringBuilder pilha = new StringBuilder();
        while (erro.getCause() != null && erro.getCause() != erro) {
            erro = erro.getCause();
            pilha.append("Causado por ").append(erro.getClass().getName())
                    .append(": ").append(erro.getMessage()).append("\n");
        }
        StackTraceElement[] quadros = erro.getStackTrace();
        for (int i = 0; i < quadros.length && i < QUADROS_DE_PILHA_EXIBIDOS; i++) {
            pilha.append(quadros[i].toString()).append("\n");
        }
        return pilha.toString();
    }

    /**
     * Payload equivalente ao enviado pela Meta para uma mensagem de texto.
     */
    private static JsonObject gerarPayloadTexto(DadosSimulacao pDados, EntradaNumeroWhatsapp pEntrada, String pTexto) {
        return gerarEnvelope(pDados, pEntrada, Json.createObjectBuilder()
                .add("from", pDados.getTelefone())
                .add("id", gerarCodigoMensagem())
                .add("timestamp", String.valueOf(System.currentTimeMillis() / 1000))
                .add("text", Json.createObjectBuilder()
                        .add("body", pTexto))
                .add("type", "text"));
    }

    /**
     * Payload equivalente ao enviado pela Meta quando o contato toca num botão
     * do menu. O id do botão é o que a navegação lê como rota explícita.
     */
    private static JsonObject gerarPayloadBotao(DadosSimulacao pDados, EntradaNumeroWhatsapp pEntrada) {
        return gerarEnvelope(pDados, pEntrada, Json.createObjectBuilder()
                .add("from", pDados.getTelefone())
                .add("id", gerarCodigoMensagem())
                .add("timestamp", String.valueOf(System.currentTimeMillis() / 1000))
                .add("type", "interactive")
                .add("interactive", Json.createObjectBuilder()
                        .add("type", "button_reply")
                        .add("button_reply", Json.createObjectBuilder()
                                .add("id", pDados.getBotao())
                                .add("title", pDados.getTituloBotao()))));
    }

    private static JsonObject gerarEnvelope(DadosSimulacao pDados, EntradaNumeroWhatsapp pEntrada,
            JsonObjectBuilder pMensagem) {

        String telefoneDivulgacao = pEntrada.getTelefoneDivulgacao() == null
                ? pEntrada.getCodigo() : pEntrada.getTelefoneDivulgacao();

        return Json.createObjectBuilder()
                .add("object", "whatsapp_business_account")
                .add("entry", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("id", CODIGO_CONTA_SIMULADA)
                                .add("changes", Json.createArrayBuilder()
                                        .add(Json.createObjectBuilder()
                                                .add("value", Json.createObjectBuilder()
                                                        .add("messaging_product", "whatsapp")
                                                        .add("metadata", Json.createObjectBuilder()
                                                                .add("display_phone_number", telefoneDivulgacao)
                                                                .add("phone_number_id", pEntrada.getCodigo()))
                                                        .add("contacts", Json.createArrayBuilder()
                                                                .add(Json.createObjectBuilder()
                                                                        .add("profile", Json.createObjectBuilder()
                                                                                .add("name", pDados.getNome()))
                                                                        .add("wa_id", pDados.getTelefone())))
                                                        .add("messages", Json.createArrayBuilder()
                                                                .add(pMensagem)))
                                                .add("field", "messages")))))
                .build();
    }

    /**
     * O código precisa ser único a cada simulação, pois é a chave usada para
     * localizar o log de trânsito da mensagem.
     */
    private static String gerarCodigoMensagem() {
        return "wamid.SIMULADO." + System.currentTimeMillis();
    }

    private static List<EntradaNumeroWhatsapp> getEntradasDisponiveis() {
        try {
            List<EntradaNumeroWhatsapp> entradas = AplicacaoWsChat.getCentralLogicaProcesasmento().gerarEntradas();
            return entradas == null ? new ArrayList<>() : entradas;
        } catch (Throwable t) {
            log(FabMensagens.ALERTA, "Falha carregando entradas para o formulário: " + t.getMessage());
            return new ArrayList<>();
        }
    }

    private static String gerarPagina(DadosSimulacao pDados, Resultado pResultado) {

        List<EntradaNumeroWhatsapp> entradas = getEntradasDisponiveis();
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html lang=\"pt-br\"><head><meta charset=\"UTF-8\">")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
                .append("<title>Simulador de recepção WhatsApp</title><style>")
                .append("body{font-family:sans-serif;background:#f0f2f5;margin:0;padding:24px;color:#111}")
                .append(".caixa{max-width:640px;margin:0 auto;background:#fff;border-radius:8px;padding:24px;")
                .append("box-shadow:0 1px 4px rgba(0,0,0,.15)}")
                .append("h1{font-size:20px;margin:0 0 4px}p.sub{color:#555;margin:0 0 20px;font-size:13px}")
                .append("label{display:block;font-size:13px;font-weight:600;margin:14px 0 4px}")
                .append("input,textarea,select{width:100%;box-sizing:border-box;padding:8px;border:1px solid #ccc;")
                .append("border-radius:4px;font-size:14px;font-family:inherit}")
                .append("textarea{min-height:90px;resize:vertical}")
                .append("button{margin-top:20px;background:#25d366;color:#fff;border:0;border-radius:4px;")
                .append("padding:10px 20px;font-size:15px;font-weight:600;cursor:pointer}")
                .append(".msg{margin-bottom:20px;padding:12px;border-radius:4px;font-size:14px}")
                .append(".ok{background:#e6f4ea;border:1px solid #34a853}")
                .append(".erro{background:#fdecea;border:1px solid #d93025}")
                .append(".aviso{background:#fef7e0;border:1px solid #f9ab00}")
                .append(".aviso ul{margin:8px 0 0;padding-left:20px}")
                .append("button.secundario{background:#3b6cf5;margin-left:8px}")
                .append(".menu{margin-top:24px;border-top:1px solid #e0e0e0;padding-top:12px}")
                .append(".menu label{margin-top:0}")
                .append(".linhaBotao{display:flex;align-items:center;gap:8px}")
                .append(".linhaBotao button{margin-top:8px;background:#075e54}")
                .append(".idBotao{color:#777;font-size:12px;font-family:monospace;margin-top:8px}")
                .append("pre{background:#282c34;color:#e6e6e6;padding:12px;border-radius:4px;overflow:auto;")
                .append("font-size:12px;white-space:pre-wrap;word-break:break-all}")
                .append("</style></head><body><div class=\"caixa\">")
                .append("<h1>Simulador de recepção de mensagem WhatsApp</h1>")
                .append("<p class=\"sub\">O envio monta um payload de webhook da Meta e o injeta no serviço, ")
                .append("como se a mensagem tivesse chegado pelo endpoint de recepção.</p>")
                .append("<div class=\"msg aviso\"><strong>Atenção: a simulação usa os serviços reais.</strong>")
                .append("<ul>")
                .append("<li>O Matrix e a API do WhatsApp são os configurados neste ambiente — em homologação ")
                .append("eles apontam para produção.</li>")
                .append("<li>A resposta do atendimento é <strong>entregue de verdade</strong> no número informado.</li>")
                .append("<li>O usuário Matrix do contato é criado ou renomeado com o nome informado, ")
                .append("sobrescrevendo o nome de um contato real se o telefone existir.</li>")
                .append("<li>Use sempre nome e telefone fictícios, como os já sugeridos abaixo ")
                .append("(").append(escaparHtml(NOME_FICTICIO)).append(" / ")
                .append(escaparHtml(TELEFONE_FICTICIO)).append(").</li>")
                .append("</ul></div>");

        if (pResultado != null) {
            html.append("<div class=\"msg ").append(pResultado.isSucesso() ? "ok" : "erro").append("\">")
                    .append(escaparHtml(pResultado.getMensagem())).append("</div>");
            if (pResultado.getPilha() != null) {
                html.append("<label>Origem do erro</label><pre>")
                        .append(escaparHtml(pResultado.getPilha())).append("</pre>");
            }
        }

        html.append("<form method=\"post\" action=\"").append(CAMINHO_FORMULARIO)
                .append("\" accept-charset=\"UTF-8\">");

        html.append("<label for=\"nome\">Nome</label>")
                .append("<input id=\"nome\" name=\"nome\" required value=\"")
                .append(escaparHtml(pDados.getNome())).append("\">");

        html.append("<label for=\"telefone\">Telefone (wa_id, somente números com DDI e DDD)</label>")
                .append("<input id=\"telefone\" name=\"telefone\" required placeholder=\"")
                .append(TELEFONE_FICTICIO).append("\" value=\"")
                .append(escaparHtml(pDados.getTelefone())).append("\">");

        html.append("<label for=\"texto\">Texto</label>")
                .append("<textarea id=\"texto\" name=\"texto\">")
                .append(escaparHtml(pDados.getTexto())).append("</textarea>");

        html.append("<label for=\"codigoEntrada\">Entrada que recebe a mensagem (phone_number_id)</label>");
        if (entradas.isEmpty()) {
            html.append("<input id=\"codigoEntrada\" name=\"codigoEntrada\" required value=\"")
                    .append(escaparHtml(pDados.getCodigoEntrada())).append("\">");
        } else {
            html.append("<select id=\"codigoEntrada\" name=\"codigoEntrada\" required>");
            for (EntradaNumeroWhatsapp entrada : entradas) {
                boolean selecionada = pDados.getCodigoEntrada().isEmpty()
                        ? entradas.get(0) == entrada
                        : pDados.getCodigoEntrada().equals(entrada.getCodigo());
                html.append("<option value=\"").append(escaparHtml(entrada.getCodigo())).append("\"")
                        .append(selecionada ? " selected" : "").append(">")
                        .append(escaparHtml(entrada.getNome() == null ? entrada.getCodigo()
                                : entrada.getNome() + " (" + entrada.getCodigo() + ")"))
                        .append("</option>");
            }
            html.append("</select>");
        }

        html.append("<button type=\"submit\" name=\"acao\" value=\"enviar\">Enviar</button>")
                .append("<button type=\"submit\" class=\"secundario\" name=\"acao\" value=\"")
                .append(ACAO_VERIFICAR_MENU).append("\">Verificar menu</button>")
                .append("</form>");

        html.append(gerarBotoesDoMenu(pDados, pResultado));

        if (pResultado != null && pResultado.getPayload() != null) {
            html.append("<label>Payload simulado</label><pre>")
                    .append(escaparHtml(pResultado.getPayload())).append("</pre>");
        }

        html.append("</div></body></html>");
        return html.toString();
    }

    /**
     * Os botões do menu em que o contato está. Cada um posta um clique
     * simulado, mantendo nome, telefone e entrada da tela.
     */
    private static String gerarBotoesDoMenu(DadosSimulacao pDados, Resultado pResultado) {

        if (pResultado == null || pResultado.getMenuDisponivel() == null) {
            return "";
        }
        MenuWhatsapp menu = pResultado.getMenuDisponivel();

        StringBuilder html = new StringBuilder();
        html.append("<div class=\"menu\"><label>Menu atual do contato</label>");
        if (menu.getMensagem() != null && menu.getMensagem().getCorpo() != null) {
            html.append("<p class=\"sub\">").append(escaparHtml(menu.getMensagem().getCorpo())).append("</p>");
        }
        for (ItemMenuWhatsapp item : menu.getItensMenu()) {
            html.append("<form method=\"post\" action=\"").append(CAMINHO_FORMULARIO)
                    .append("\" accept-charset=\"UTF-8\" class=\"linhaBotao\">")
                    .append(gerarCampoOculto("nome", pDados.getNome()))
                    .append(gerarCampoOculto("telefone", pDados.getTelefone()))
                    .append(gerarCampoOculto("codigoEntrada", pDados.getCodigoEntrada()))
                    .append(gerarCampoOculto("botao", item.getId()))
                    .append(gerarCampoOculto("tituloBotao", item.getTitulo()))
                    .append("<button type=\"submit\" name=\"acao\" value=\"").append(ACAO_CLICAR_BOTAO)
                    .append("\">").append(escaparHtml(item.getTitulo()))
                    .append("</button>")
                    .append("<span class=\"idBotao\">").append(escaparHtml(item.getId())).append("</span>")
                    .append("</form>");
        }
        html.append("</div>");
        return html.toString();
    }

    private static String gerarCampoOculto(String pNome, String pValor) {
        return "<input type=\"hidden\" name=\"" + pNome + "\" value=\"" + escaparHtml(pValor) + "\">";
    }

    private static String escaparHtml(String pTexto) {
        if (pTexto == null) {
            return "";
        }
        return pTexto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * A instrumentação nunca pode alterar o fluxo da simulação, por isso o
     * serviço de log é chamado dentro de um try.
     */
    private static void log(FabMensagens pTipo, String pMensagem) {
        try {
            CarameloCode.getServicoLogEventos().registrarLogDeEvento(pTipo, TAG_LOG + " " + pMensagem);
        } catch (Throwable t) {
            System.out.println(TAG_LOG + " " + pTipo + " " + pMensagem);
        }
    }

    private static class DadosSimulacao {

        private String nome = "";
        private String telefone = "";
        private String texto = "";
        private String codigoEntrada = "";
        private String acao = "";
        private String botao = "";
        private String tituloBotao = "";

        /**
         * Sugestão exibida na primeira carga. A partir do primeiro envio o
         * formulário reaproveita o que o usuário digitou.
         */
        public static DadosSimulacao getPadrao() {
            DadosSimulacao dados = new DadosSimulacao();
            dados.nome = NOME_FICTICIO;
            dados.telefone = TELEFONE_FICTICIO;
            return dados;
        }

        public static DadosSimulacao getByRequisicao(Request pRequisicao) {
            DadosSimulacao dados = new DadosSimulacao();
            try {
                pRequisicao.raw().setCharacterEncoding("UTF-8");
            } catch (Throwable t) {
                log(FabMensagens.ALERTA, "Falha definindo UTF-8 na requisição: " + t.getMessage());
            }
            dados.nome = getTexto(pRequisicao, "nome");
            dados.telefone = getTexto(pRequisicao, "telefone").replaceAll("[^0-9]", "");
            dados.texto = getTexto(pRequisicao, "texto");
            dados.codigoEntrada = getTexto(pRequisicao, "codigoEntrada");
            dados.acao = getTexto(pRequisicao, "acao");
            dados.botao = getTexto(pRequisicao, "botao");
            dados.tituloBotao = getTexto(pRequisicao, "tituloBotao");
            return dados;
        }

        public String getAcao() {
            return acao;
        }

        public String getBotao() {
            return botao;
        }

        public String getTituloBotao() {
            return tituloBotao.isEmpty() ? botao : tituloBotao;
        }

        private static String getTexto(Request pRequisicao, String pCampo) {
            String valor = pRequisicao.queryParams(pCampo);
            return valor == null ? "" : valor.trim();
        }

        public String getNome() {
            return nome;
        }

        public String getTelefone() {
            return telefone;
        }

        public String getTexto() {
            return texto;
        }

        public String getCodigoEntrada() {
            return codigoEntrada;
        }
    }

    private static class Resultado {

        private final boolean sucesso;
        private final String mensagem;
        private final String payload;
        private final String pilha;
        private MenuWhatsapp menuDisponivel;

        private Resultado(boolean pSucesso, String pMensagem, String pPayload, String pPilha) {
            sucesso = pSucesso;
            mensagem = pMensagem;
            payload = pPayload;
            pilha = pPilha;
        }

        /**
         * Menu em que o contato está agora, para a tela desenhar os botões
         * clicáveis.
         */
        public Resultado comMenu(MenuWhatsapp pMenu) {
            menuDisponivel = pMenu;
            return this;
        }

        public MenuWhatsapp getMenuDisponivel() {
            return menuDisponivel;
        }

        public static Resultado sucesso(String pMensagem, String pPayload) {
            return new Resultado(true, pMensagem, pPayload, null);
        }

        public static Resultado falha(String pMensagem, String pPayload) {
            return new Resultado(false, pMensagem, pPayload, null);
        }

        public static Resultado falha(String pMensagem, String pPayload, String pPilha) {
            return new Resultado(false, pMensagem, pPayload, pPilha);
        }

        public String getPilha() {
            return pilha;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }

        public String getPayload() {
            return payload;
        }
    }
}
