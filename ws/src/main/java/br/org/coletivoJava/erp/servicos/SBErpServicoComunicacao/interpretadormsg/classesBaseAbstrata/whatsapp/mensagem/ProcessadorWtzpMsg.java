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
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroCriandoContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroIniciandoTrilha;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.sessao.SessaoDeContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao.TrilhaNavegacaoAbs;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoUsuarioChat;
import br.org.coletivoJava.integracoes.restIntwhatsapp.api.model.menu.MenuWhatsapp;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappMensagem;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.implementacao.ChamadaHttpSimples;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.implementacao.UtilSBApiRestClient;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author salvio
 */
public class ProcessadorWtzpMsg extends ProcessadorSocketWhatsapp implements ItfProcessadorMensagemWhatsapp {

    protected MensagemWhatsapp mensagem;
    private RotaMensagemContato dadosRotaMensagemProcessada;
    private ComoUsuarioChat usuarioMAtrixContato;
    private Contato contato;
    private MensagemTrOrigemWhatsapp mensagemEmTransito;

    @Override
    public void processar() throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento, ErroConexaoServicoChat {
        try {
            contato = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato(mensagem.getContatoOrigem());
            usuarioMAtrixContato = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(contato.getMatrixID());
            if (AplicacaoWsChat.SERVICO_MATRIX.isUmUsuarioAtendimento(usuarioMAtrixContato)) {
                throw new ErroComDevolucaoMensagemUsuario("Usuário de atendimento, entrou em contato para obter atendimento", "Seu número está cadastrado como número de atendimento, entre em contato ");
            }
        } catch (ErroRegraDeNEgocioChat erroRegraDeNegocio) {
            throw new ErroComDevolucaoMensagemUsuario("Falha obtendo usuario contato", "Erro obtendo usuário representante do contato: " + erroRegraDeNegocio.getMessage());
        } catch (ErroConexaoServicoChat tServicoIndisponivel) {
            throw new ErroFalhaEncaminhando("Falha obtendo usuário correspentente ao contato no sistema Matrix, serviço indisponivel" + tServicoIndisponivel.getMessage());
        }
        System.out.println("DEFININDO TRILHA");
        ItfTrilhaNavegacao trilha = null;
        try {
            trilha = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getTrilhaByMensagemWhatasapp(mensagem.getEntrada(), contato, mensagem);
            trilha.registrarInteracao(TrilhaNavegacaoAbs.TIPO_INTERACAO.CONTATO);

            System.out.println("TRILHA DEFINIDA");
        } catch (ErroComDevolucaoMensagemUsuario p) {
            throw p;

        } catch (ErroIniciandoTrilha pErroIniandoTrilha) {
            AplicacaoWsChat.encerrrarSessao(mensagem.getEntrada(), contato.getWaid());
            throw pErroIniandoTrilha.getErroComDevolucao();
        } catch (Throwable t) {
            throw new ErroComDevolucaoMensagemUsuario("Falha de comunicação, com retorno para o usuário" + t.getMessage(), "Falha definindo trilha:"
                    + t.getMessage() + " por favor, entre em contato com nossa equipe, para resolvermos sua demanda, e  relate o horário do erro, para melhorarmos nosso serviço,ligando neste relefone");
        }
        try {
            if (trilha == null) {
                throw new ErroComDevolucaoMensagemUsuario("Falha de comunicação, com retorno para o usuário", "Falha definindo trilha para:" + contato.getNome() + " por favor, entre em contato com nossa equipe, para resolvermos sua demanda, e  relate o horário do erro, para melhorarmos nosso serviço,ligando neste relefone");
            }
            System.out.println(trilha.getCaminhoTrilha());
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

            if (rota.getAcaoPosDispacho() != null) {
                AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.processarAcaoGatilho(rota.getAcaoPosDispacho());
            }
        } catch (ErroConexaoServicoChat ex) {
            throw new ErroFalhaEncaminhando("Falha encaminhando mensagem " + ex.getMessage());
        } catch (ErroRegraDeNEgocioChat ex) {
            throw new ErroComDevolucaoMensagemUsuario("Falha de comunicação, com retorno para o usuário" + ex.getMessage(), "Falha encaminhando mensagem:" + ex.getMessage() + " por favor, entre em contato com nossa equipe, para resolvermos sua demanda, e  relate o horário do erro, para melhorarmos nosso serviço,ligando neste relefone");
        } catch (Throwable t) {
            throw new ErroComDevolucaoMensagemUsuario("Falha de comunicação, com retorno para o usuário" + t.getMessage(), "Falha encaminhando mensagem:" + t.getMessage() + " por favor, entre em contato com nossa equipe, para resolvermos sua demanda, e  relate o horário do erro, para melhorarmos nosso serviço,ligando neste relefone");

        }

    }

    public ProcessadorWtzpMsg(MensagemWhatsapp pMensagem, MensagemTrOrigemWhatsapp pMensagemEmTransito) throws ErroFalhaEncaminhando, ErroComDevolucaoMensagemUsuario, ErroFalhaGerandoSalaAtendimento, ErroFalhaGerandoUsuarioAtendimento {
        mensagem = pMensagem;
        mensagemEmTransito = pMensagemEmTransito;

    }

    protected String enviarMenu(EntradaNumeroWhatsapp pEntrada, MenuWhatsapp pMenu, ComoChatSalaBean pSalaRelatorio, ComoUsuarioChat pContato) throws ErroConexaoServicoChat {

        String recibo;
        try {
            recibo = AplicacaoWsChat.SERVICO_WHATSAPP.enviarMenu(pEntrada, AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato(pContato).getWaid(), pMenu);
            return recibo;
        } catch (ErroRegraDeNEgocioChat | ErroCriandoContato ex) {
            return null;
        }

    }

    protected void despachar(RotaLinkAcesso pRotaMenu) throws ErroConexaoServicoChat, ErroRegraDeNEgocioChat {
        ItfRespostaWebServiceSimples resposta = FabApiRestIntWhatsappMensagem.MENSAGEM_LINK_ENVIAR.getAcao(getMensagemWhatsapp().getEntrada().getCodigo(),
                contato.getWaid(),
                pRotaMenu.getDescricaoLink(),
                pRotaMenu.getNomeAcao(),
                pRotaMenu.getLinkAcao()
        ).getResposta();
        if (!resposta.isSucesso()) {
            throw new ErroConexaoServicoChat("Falha enviando link para o usuário");
        }

    }

    protected void despachar(RotaMenuOpcoes pRotaMenu) throws ErroConexaoServicoChat, ErroRegraDeNEgocioChat {

        ItfRespostaWebServiceSimples retornoEnvioMenu = FabApiRestIntWhatsappMensagem.MENSAGEM_MENU_ATE_10_OPCOES_ENVIAR.getAcao(getMensagemWhatsapp().getEntrada().getCodigo(), contato.getWaid(),
                pRotaMenu.getComoRotaMenuOpcoes().getMenuWhatsapp()).getResposta();
        try {

            SessaoDeContato sessao = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO
                    .getSessaoDoContato(AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContextoContato(getMensagemWhatsapp().getEntrada(), contato));

            if (sessao.getContexto().getSalaUltimaConversa() != null) {
                ComoChatSalaBean sala = AplicacaoWsChat.SERVICO_MATRIX.getSalaByCodigo(sessao.getContexto().getSalaUltimaConversa());
                encaminharMensagemParaMatrix(mensagem, sala, usuarioMAtrixContato);
            }

        } catch (ErroRegraDeNegocio | ErroComDevolucaoMensagemUsuario ex) {

        }

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
