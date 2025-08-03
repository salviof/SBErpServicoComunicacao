package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.whatsapp.mensagem;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.classesBaseAbstrata.whatsapp.ProcessadorSocketWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfProcessadorMensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaEncaminhamentoSala;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaLinkAcesso;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaMensagemContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaMenuOpcoes;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaWebservice;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem.ENCAMINHAMENTO;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem.MENU_OPCOES;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem.RESPOSTA_WEBSERVICE;
import static br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.tipos.FabTipoRotaMensagem.RETORNO_LINK;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoSalaAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaGerandoUsuarioAtendimento;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.integracoes.restIntwhatsapp.api.model.menu.MenuWhatsapp;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappMensagem;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.implementacao.ChamadaHttpSimples;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.implementacao.UtilSBApiRestClient;
import jakarta.json.JsonObject;

/**
 *
 * @author salvio
 */
public class ProcessadorWtzpMsg extends ProcessadorSocketWhatsapp implements ItfProcessadorMensagemWhatsapp {

    protected MensagemWhatsapp mensagem;
    private RotaMensagemContato dadosRotaMensagemProcessada;
    private ItfUsuarioChat usuarioMAtrixContato;
    private Contato contato;
    private MensagemTrOrigemWhatsapp mensagemEmTransito;

    @Override
    public void processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento, ErroConexaoServicoChat {
        try {
            contato = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato(mensagem.getContatoOrigem());
            usuarioMAtrixContato = AplicacaoWsChat.SERVICO_MATRIX.gerarUsuarioContato(mensagem.getNome(), mensagem.getTelefone());
            if (AplicacaoWsChat.SERVICO_MATRIX.isUmUsuarioAtendimento(usuarioMAtrixContato)) {
                throw new ErroComDevolucaoMensagemUsuario("Usuário de atendimento, entrou em contato para obter atendimento", "Seu número está cadastrado como número de atendimento, entre em contato ");
            }
        } catch (ErroRegraDeNEgocioChat erroRegraDeNegocio) {
            throw new ErroComDevolucaoMensagemUsuario("Falha obtendo usuario contato", "Erro obtendo usuário representante do contato: " + erroRegraDeNegocio.getMessage());
        } catch (ErroConexaoServicoChat tServicoIndisponivel) {
            throw new ErroFalhaEncaminhando("Falha obtendo usuário correspentente ao contato no sistema Matrix, serviço indisponivel" + tServicoIndisponivel.getMessage());
        }

        try {

            ItfTrilhaNavegacao trilha = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getTrilha(mensagem.getEntrada(), contato, mensagem);

            RotaMensagemContato rota = trilha.getRotaAtual();
            switch (rota.getTipoRota().getTipoRotaMensagem()) {
                case MENU_OPCOES:
                    despachar((RotaMenuOpcoes) rota);
                    break;
                case RESPOSTA_WEBSERVICE:
                    despachar((RotaWebservice) rota);
                    break;
                case RETORNO_LINK:
                    despachar((RotaLinkAcesso) rota);
                    break;
                case ENCAMINHAMENTO:
                    despachar((RotaEncaminhamentoSala) rota);
                    break;

                default:
                    throw new AssertionError();
            }
        } catch (ErroConexaoServicoChat ex) {
            throw new ErroFalhaEncaminhando("Falha encaminhando mensagem " + ex.getMessage());
        } catch (ErroRegraDeNEgocioChat ex) {
            throw new ErroComDevolucaoMensagemUsuario("Falha de comunicação, com retorno para o usuário" + ex.getMessage(), "Falha encaminhando mensagem:" + ex.getMessage());
        }
    }

    public ProcessadorWtzpMsg(MensagemWhatsapp pMensagem, MensagemTrOrigemWhatsapp pMensagemEmTransito) throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {
        mensagem = pMensagem;
        mensagemEmTransito = pMensagemEmTransito;

    }

    protected String enviarMenu(EntradaNumeroWhatsapp pEntrada, MenuWhatsapp pMenu, ItfChatSalaBean pSalaRelatorio, ItfUsuarioChat pContato) throws ErroConexaoServicoChat {
        ItfRespostaWebServiceSimples resposta = FabApiRestIntWhatsappMensagem.MENSAGEM_MENU_ATE_10_OPCOES_ENVIAR.getAcao(pEntrada, pMenu).getResposta();
        if (resposta.isSucesso()) {

        }
        String recibo = AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(pSalaRelatorio, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioAdmin(), null, "Um menú [" + pMenu.getMensagem().getCorpo() + "] foi enviado para " + pContato.getNome());
        return recibo;
    }

    protected void despachar(RotaLinkAcesso pRotaMenu) throws ErroConexaoServicoChat, ErroRegraDeNEgocioChat {
        throw new ErroRegraDeNEgocioChat("rotaDeLinkDeACessonaofoiDefinida ");
    }

    protected void despachar(RotaMenuOpcoes pRotaMenu) throws ErroConexaoServicoChat, ErroRegraDeNEgocioChat {
        ItfRespostaWebServiceSimples retornoEnvioMenu = FabApiRestIntWhatsappMensagem.MENSAGEM_MENU_ATE_10_OPCOES_ENVIAR.getAcao(getMensagemWhatsapp().getEntrada().getCodigo(), contato.getWaid(),
                pRotaMenu.getComoRotaMenuOpcoes().getMenuWhatsapp()).getResposta();
        if (!retornoEnvioMenu.isSucesso()) {
            throw new ErroConexaoServicoChat("Falha enviando Menu");
        }
    }

    protected void despachar(RotaWebservice pRotaProcessada) throws ErroConexaoServicoChat, ErroRegraDeNEgocioChat {

        RotaWebservice rota = pRotaProcessada.getComoRotaWebService();
        ChamadaHttpSimples chamado = rota.getChamada();
        ItfRespostaWebServiceSimples respostaWS = UtilSBApiRestClient.getRespostaRest(chamado);
        if (!respostaWS.isSucesso()) {
            throw new ErroConexaoServicoChat("Falha comunicaçãndo com serviço " + pRotaProcessada.getTipoRota().getNome());
        }
        JsonObject jsonResposata = respostaWS.getRespostaComoObjetoJson();
        if (rota.getCaminhoJsonMensagemAtendimento() != null) {
            String respostaAtendimento = UtilSBCoreJson.getValorApartirDoCaminho(rota.getCaminhoJsonMensagemAtendimento(), jsonResposata);
            if (rota.getCodigoSalaAtendimento() != null) {
                encaminharMensagemTextoAdministraParaMatrix(respostaAtendimento, AplicacaoWsChat.SERVICO_MATRIX.getSalaByCodigo(respostaAtendimento), AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(rota.getCodigoAtendimento()));
            } else {
                encaminharMensagemTextoAdministraParaMatrix(respostaAtendimento, null, AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(rota.getCodigoAtendimento()));
            }
        }
        if (rota.getCaminhoJsonMensagemContato() != null) {
            String respostaContato = UtilSBCoreJson.getValorApartirDoCaminho(rota.getCaminhoJsonMensagemContato(), jsonResposata);
        }

    }

    protected void despachar(RotaEncaminhamentoSala pRotaEncaminhamento) throws ErroConexaoServicoChat, ErroRegraDeNEgocioChat {

        String reciboEncaminhamentoMatrix = encaminharMensagemParaMatrix(mensagem,
                pRotaEncaminhamento.getSala(),
                usuarioMAtrixContato);
        mensagemEmTransito.setCodigoEncaminhamentoMatrix(reciboEncaminhamentoMatrix);

    }

    public RotaMensagemContato getDadosRotaMensagemProcessada() {
        return dadosRotaMensagemProcessada;
    }

    public MensagemWhatsapp getMensagem() {
        return mensagem;
    }

    @Override
    public MensagemWhatsapp getMensagemWhatsapp() {
        return mensagem;
    }

}
