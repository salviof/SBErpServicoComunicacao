package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.logdeMensagens;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;

import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoUsuarioChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Atendente;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.EncaminhamentoMatrixParaWtzp;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.Persistencia.dao.consultaDinamica.ConsultaDinamicaDeEntidade;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCDataHora;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCListasObjeto;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCStringTelefone;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCStringValidador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.contato.CPContato;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.encaminhamentomatrixparawtzp.CPEncaminhamentoMatrixParaWtzp;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtransito.CPMensagemTransito;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtrorigemwhatsapp.CPMensagemTrOrigemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.ContatoWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroCriandoContato;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_ATENDIMENTO;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_ATENDIMENTO_CHAMADO;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_DEBATE_INTERNO_LEAD_CLIENTE;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_VENDAS;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.WTZAP_ATENDIMENTO;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.WTZAP_ATENDIMENTO_GRUPO_CLIENTE;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.WTZAP_VENDAS;
import br.org.coletivoJava.integracoes.matrixChat.FabApiRestIntMatrixChatSalas;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import com.super_bits.modulosSB.SBCore.ConfigGeral.CarameloCode;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCStringFiltros;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import jakarta.json.JsonArray;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.contextocontato.CPContextoContato;
import org.coletivojava.fw.api.tratamentoErros.FabErro;

/**
 *
 * @author salvio
 */
public class RepositorioComunicacaoChat {

    private static List<Contato> ULTIMOS_CONTATOS = Collections.synchronizedList(new ArrayList<>());

    public synchronized Atendente getAtendente(ComoUsuarioChat pUSuarioAtendimento) {

        Atendente atendenteRegistrado = (Atendente) UtilSBPersistencia.getRegistroByJPQL("from " + Atendente.class.getSimpleName() + " where email = '" + pUSuarioAtendimento.getEmail() + "'", Atendente.class);
        if (atendenteRegistrado == null) {
            Atendente atendente = new Atendente();
            atendente.setMatrixID(pUSuarioAtendimento.getCodigoUsuario());
            atendente.setEmail(pUSuarioAtendimento.getEmail());
            atendente.setNome(pUSuarioAtendimento.getNome());
            atendenteRegistrado = UtilSBPersistencia.mergeRegistro(atendente);
        }
        return atendenteRegistrado;
    }

    public synchronized void contextoAtualizar(ContextoContato pContexto) {

        UtilSBPersistencia.mergeRegistro(pContexto);
    }

    public int getQuantidadadeSessoesAbertas() {
        return ULTIMOS_CONTATOS.size();
    }

    public synchronized ContextoContato getContextoContato(EntradaNumeroWhatsapp pEntrada, Contato pContato) {
        EntityManager em = UtilSBPersistencia.getEMPadraoNovo();
        try {
            ContextoContato contexto = (ContextoContato) UtilSBPersistencia.gerarConsultaDeEntidade(ContextoContato.class, em)
                    .addCondicaoManyToOneIgualA(CPContextoContato.contato, pContato)
                    .addcondicaoCampoIgualA(CPContextoContato.codigoentrada, pEntrada.getCodigo()).getPrimeiroRegistro();
            if (contexto != null) {
                return contexto;
            } else {
                ContextoContato novoContext = new ContextoContato();
                novoContext.setContato(pContato);

                novoContext.setDataHoraInicioSessao(new Date());
                novoContext.setCodigoEntrada(pEntrada.getCodigo());

                String nomeContexto = "Ctx:" + novoContext.getCodigoEntrada()
                        + novoContext.getContato().getWaid();
                novoContext.setNomeContexto(nomeContexto);

//novoContext.getCPinst(CPContextoContato.nomecontexto).getValorTextoFormatado();
                novoContext.setDataHoraInicioSessao(new Date());
                return UtilSBPersistencia.mergeRegistro(novoContext);
            }

        } finally {
            UtilSBPersistencia.fecharEM(em);

        }
    }

    public static boolean isContatoNaListaUltimosContatos(EntradaNumeroWhatsapp pEntrada, Contato pContato) {

        return ULTIMOS_CONTATOS.contains(pContato);
    }

    public MensagemTrOrigemWhatsapp getMensagemEnviadaPorWhatsappByRegistroMatrix(String pCodigoMensagemMatrix) {
        EntityManager em = UtilSBPersistencia.getEMPadraoNovo();
        try {
            return (MensagemTrOrigemWhatsapp) new ConsultaDinamicaDeEntidade(MensagemTrOrigemWhatsapp.class, em)
                    .addcondicaoCampoIgualA(CPMensagemTrOrigemWhatsapp.codigoencaminhamentomatrix, pCodigoMensagemMatrix).getPrimeiroRegistro();
        } finally {
            UtilSBPersistencia.fecharEM(em);
        }
    }

    public MensagemTrOrigemWhatsapp getMensagemEnviadaPorWhatsappByRegistrWhatsapp(String pCodigoMensagemMatrix) {
        EntityManager em = UtilSBPersistencia.getEMPadraoNovo();
        try {
            return (MensagemTrOrigemWhatsapp) new ConsultaDinamicaDeEntidade(MensagemTrOrigemWhatsapp.class, em)
                    .addcondicaoCampoIgualA(CPMensagemTrOrigemWhatsapp.codigoregistromensagemwhatsapp, pCodigoMensagemMatrix).getPrimeiroRegistro();
        } finally {
            UtilSBPersistencia.fecharEM(em);
        }
    }

    /**
     * Mensagens do WhatsApp que estão na fila de entrega: ainda não tratadas e
     * com o prazo em aberto, em ordem de chegada. O banco é a fonte da verdade
     * da fila, por isso ela sobrevive a restart do serviço.
     *
     * Sobre as condições de data: não existe coluna dedicada de "está na fila",
     * então quem identifica a mensagem enfileirada é o <b>prazo curto</b>. Todo
     * registro nasce com daHoraExpirar de 5 dias (padrão do modelo) e só quem
     * passa pela fila tem esse prazo encurtado para minutos. Sem esse limite
     * superior, entrariam na fila tanto os registros anteriores a esta versão
     * (que ficavam com encaminhado = false para sempre) quanto as mensagens que
     * falharam e serão reentregues pelo próprio WhatsApp. A condição de criação
     * recente fecha o caso de borda do registro antigo que completa 5 dias
     * agora e cairia dentro da janela de prazo.
     *
     * @param pPrazoMaximoDaFila agora + prazo de entrega da fila
     * @param pCriacaoMaisAntigaAceita limite de idade do registro
     */
    public List<MensagemTrOrigemWhatsapp> getMensagensPendentesDeEntrega(Date pPrazoMaximoDaFila,
            Date pCriacaoMaisAntigaAceita) {
        EntityManager em = UtilSBPersistencia.getEMPadraoNovo();
        try {
            return new ConsultaDinamicaDeEntidade(MensagemTrOrigemWhatsapp.class, em)
                    .addCondicaoNegativo(CPMensagemTransito.encaminhado)
                    .addCondicaoDataHoraMaiorOuIgualA(CPMensagemTransito.dahoraexpirar, new Date())
                    .addCondicaoDataHoraMenorOuIgualA(CPMensagemTransito.dahoraexpirar, pPrazoMaximoDaFila)
                    .addCondicaoDataHoraMaiorOuIgualA(CPMensagemTransito.datahoracriacao, pCriacaoMaisAntigaAceita)
                    .setOrdemCampoPersonalizado(CPMensagemTransito.datahoracriacao)
                    .gerarResultados();
        } finally {
            UtilSBPersistencia.fecharEM(em);
        }
    }

    /**
     * Mensagens da fila que estouraram o prazo de entrega, incluindo as que
     * venceram enquanto o serviço estava fora do ar. Depois de avisar o
     * contato, elas têm de ser encerradas para não voltarem no ciclo seguinte.
     *
     * O limite de idade evita dois problemas: puxar registros anteriores a esta
     * versão e avisar o contato sobre uma mensagem velha, que a essa altura só
     * confundiria.
     *
     * @param pCriacaoMaisAntigaAceita limite de idade do registro
     */
    public List<MensagemTrOrigemWhatsapp> getMensagensPendentesComPrazoVencido(Date pCriacaoMaisAntigaAceita) {
        EntityManager em = UtilSBPersistencia.getEMPadraoNovo();
        try {
            return new ConsultaDinamicaDeEntidade(MensagemTrOrigemWhatsapp.class, em)
                    .addCondicaoNegativo(CPMensagemTransito.encaminhado)
                    .addCondicaoDataHoraMenorOuIgualA(CPMensagemTransito.dahoraexpirar, new Date())
                    .addCondicaoDataHoraMaiorOuIgualA(CPMensagemTransito.datahoracriacao, pCriacaoMaisAntigaAceita)
                    .setOrdemCampoPersonalizado(CPMensagemTransito.datahoracriacao)
                    .gerarResultados();
        } finally {
            UtilSBPersistencia.fecharEM(em);
        }
    }

    public EncaminhamentoMatrixParaWtzp getMensagemEnviadaPorMatrixByRegistroWhatsapp(String pCodigoRegistroWhatsapp) {
        EntityManager em = UtilSBPersistencia.getEMPadraoNovo();
        try {
            return (EncaminhamentoMatrixParaWtzp) new ConsultaDinamicaDeEntidade(EncaminhamentoMatrixParaWtzp.class, em)
                    .addcondicaoCampoIgualA(CPEncaminhamentoMatrixParaWtzp.reciboregistroowtzp, pCodigoRegistroWhatsapp).getPrimeiroRegistro();

        } catch (Throwable t) {
            return null;
        } finally {
            UtilSBPersistencia.fecharEM(em);
        }

    }

    public enum TIPO_ACESSO_REPOSITORIO {
        LEITURA, ATUALIZACAO
    }

    public synchronized Contato getContato(ComoUsuarioChat pUsuario) throws ErroConexaoServicoChat, ErroRegraDeNEgocioChat, ErroCriandoContato {
        if (UtilCRCStringValidador.isNuloOuEmbranco(pUsuario.getTelefone())) {
            throw new ErroRegraDeNEgocioChat("Telefone do usuário não foi definido");
        }
        String telefonewtzp = UtilCRCStringTelefone.gerarCeluarWhatasapp(pUsuario.getTelefone());
        Contato contato = getContato(telefonewtzp);
        if (contato == null) {
            return getContato(new ContatoWhatsapp(UtilCRCStringTelefone.gerarCeluarWhatasapp(pUsuario.getTelefone()), pUsuario.getNome()));
        } else {
            return contato;
        }

    }

    public synchronized Contato getContato(String wapId) throws ErroConexaoServicoChat, ErroRegraDeNEgocioChat {
        return operacoesDeRepositorio(TIPO_ACESSO_REPOSITORIO.LEITURA, null, wapId);
    }

    public synchronized Contato getContato(ContatoWhatsapp pContatoRegistro) throws ErroConexaoServicoChat, ErroRegraDeNEgocioChat {
        return operacoesDeRepositorio(TIPO_ACESSO_REPOSITORIO.ATUALIZACAO, pContatoRegistro, null);
    }

    /**
     *
     * Retorna os dados do contato atualizados. Contato
     *
     * @param pTipoAcessos (tipo de acesso q pode ser LEITURA OU ATUALIZAÇÃO
     * @param pContatoRegistro (Obrigatório para atualização)
     * @param wapId (Obrigatŕio para leitura)
     * @return
     */
    private synchronized Contato operacoesDeRepositorio(TIPO_ACESSO_REPOSITORIO pTipoAcessos, ContatoWhatsapp pContatoRegistro, String wapId) throws ErroConexaoServicoChat, ErroRegraDeNEgocioChat {
        switch (pTipoAcessos) {

            case LEITURA:
                if (pContatoRegistro != null || wapId == null) {
                    throw new UnsupportedOperationException("Parametros inválidos para leitura");
                }

                Optional<Contato> pesquisaContato = ULTIMOS_CONTATOS.stream().filter(ct -> ct.getWaid().equals(wapId)).findFirst();
                if (pesquisaContato.isPresent()) {
                    return pesquisaContato.get();
                }
                Contato contato = (Contato) UtilSBPersistencia.getRegistroByJPQL("from " + Contato.class.getSimpleName() + " where " + CPContato.waid + " = '" + wapId + "'", Contato.class);

                if (contato != null) {
                    return contato;
                }
                return null;

            case ATUALIZACAO:
                if (pContatoRegistro == null || wapId != null) {
                    throw new UnsupportedOperationException("Parametros inválidos para atualização");
                }
                return registrarDadosDoContato(pContatoRegistro);

            default:
                throw new AssertionError();
        }
    }

    private synchronized Contato registrarDadosDoContato(ContatoWhatsapp pContato) throws ErroConexaoServicoChat, ErroRegraDeNEgocioChat {
        EntityManager em = UtilSBPersistencia.getEMPadraoNovo();
        try {
            Optional<Contato> pesquisaContato = ULTIMOS_CONTATOS.stream()
                    .filter(ct -> ct.getWaid().equals(pContato.getWa_id())).findFirst();

            if (pesquisaContato.isPresent()) {
                return registraUltimoContato(pesquisaContato.get());
            }
            Contato contato = (Contato) UtilSBPersistencia.getRegistroByJPQL("from " + Contato.class.getSimpleName() + " where " + CPContato.waid + " = '" + pContato.getWa_id() + "' and tipoPessoa='" + Contato.class.getSimpleName() + "'", Contato.class, em);
            if (contato == null) {
                contato = new Contato();
                contato.setNome(pContato.getNome());
                contato.setWaid(pContato.getWa_id());

                ComoUsuarioChat usuarioContatoChat = AplicacaoWsChat.SERVICO_MATRIX.gerarUsuarioContato(pContato.getNome(), UtilCRCStringTelefone.gerarNumeroTelefoneInternacional(pContato.getWa_id()));
                contato.setMatrixID(exigirUsuarioDoContato(usuarioContatoChat, pContato).getCodigoUsuario());
                contato.setDataHoraUltimaInteracao(new Date());
                contato.setTelefone(UtilCRCStringTelefone.gerarNumeroTelefoneInternacional(pContato.getWa_id()));
                contato = UtilSBPersistencia.mergeRegistro(contato, em);
                if (contato == null) {
                    throw new ErroConexaoServicoChat("Falha persistindo contato no banco de dados");
                }
                return registraUltimoContato(contato);
            } else {

                contato.setNome(pContato.getNome());
                contato.setWaid(pContato.getWa_id());

                ComoUsuarioChat usuarioContatoChat = AplicacaoWsChat.SERVICO_MATRIX.gerarUsuarioContato(pContato.getNome(), UtilCRCStringTelefone.gerarNumeroTelefoneInternacional(pContato.getWa_id()));
                contato.setMatrixID(exigirUsuarioDoContato(usuarioContatoChat, pContato).getCodigoUsuario());
                contato.setDataHoraUltimaInteracao(new Date());
                contato.setTelefone(UtilCRCStringTelefone.gerarNumeroTelefoneInternacional(pContato.getWa_id()));
                contato = UtilSBPersistencia.mergeRegistro(contato, em);

                return registraUltimoContato(contato);
            }
        } finally {
            UtilSBPersistencia.fecharEM(em);
        }
    }

    /**
     * O usuário Matrix do contato é obrigatório: sem ele não existe remetente
     * para levar a mensagem até a sala do atendimento.
     *
     * O gerarUsuarioContato pode devolver nulo quando o Synapse recusa a
     * criação ou a atualização. Antes o nulo seguia adiante e estourava
     * NullPointerException na linha seguinte - exceção não declarada, que
     * escapava de todo o tratamento de erro e terminava em 500 sem nenhuma
     * mensagem para o contato. Lançar ErroRegraDeNEgocioChat faz o
     * ProcessadorWtzpMsg convertê-la em ErroComDevolucaoMensagemUsuario, e aí o
     * contato é avisado de que a mensagem não foi entregue.
     */
    private ComoUsuarioChat exigirUsuarioDoContato(ComoUsuarioChat pUsuarioContatoChat, ContatoWhatsapp pContato)
            throws ErroRegraDeNEgocioChat {
        if (pUsuarioContatoChat == null || pUsuarioContatoChat.getCodigoUsuario() == null) {
            // O detalhe técnico fica no log: a mensagem desta exceção é
            // concatenada no texto que o contato recebe pelo WhatsApp.
            try {
                CarameloCode.getServicoLogEventos().registrarLogDeEvento(FabMensagens.ERRO,
                        "[REPO-CONTATO] O serviço de chat não devolveu usuário válido para o contato "
                        + pContato.getNome() + " (" + pContato.getWa_id() + ")."
                        + " Ver as linhas [MTX-USUARIO] e [MTX-TOKEN] para a resposta do Synapse."
                        + " A mensagem não foi entregue e o contato será avisado.");
            } catch (Throwable t) {
                System.out.println("[REPO-CONTATO] Sem usuário de chat para " + pContato.getWa_id());
            }
            throw new ErroRegraDeNEgocioChat("não foi possível registrar seu atendimento no momento");
        }
        return pUsuarioContatoChat;
    }

    //ATENÇÃO NUNCA CHAMAR ESSE METODO FORA DO registrarDadosDoContato, POIS PODE COMPROMETER A INCOMPATIBLIDIDADE DE CHAMADAS ASSINCRONAS DO ArrayList
    //Bloquear o chamado via stacktrace é CARO, e unificar o código deixa muito complexo.
    private synchronized static Contato registraUltimoContato(Contato novoContato) {

        if (novoContato.getDataHoraUltimaInteracao() != null) {
            if (UtilCRCDataHora.intervaloTempoHoras(novoContato.getDataHoraUltimaInteracao(), new Date()) > 1) {
                novoContato.setDataHoraUltimaInteracao(new Date());
                novoContato = UtilSBPersistencia.mergeRegistro(novoContato);
            } else {
                novoContato.setDataHoraUltimaInteracao(new Date());
            }

        }

        UtilCRCListasObjeto.listaLimitadaDeObjetos(ULTIMOS_CONTATOS, novoContato, 20);
        return novoContato;
    }

    public ComoUsuarioChat getUsuarioWhatsappPricipalLeadBySala(ComoChatSalaBean pSala) {
        try {
            if (pSala == null) {
                return null;
            }
            ItfRespostaWebServiceSimples resp = FabApiRestIntMatrixChatSalas.SALA_ALIASES.getAcao(pSala.getCodigoChat()).getResposta();
            String telefone = null;
            JsonArray apelidos = resp.getRespostaComoObjetoJson().getJsonArray("aliases");
            Optional<String> apelidoOficial = apelidos.stream().map(ap -> ap.toString().replace("\"", ""))
                    .filter(apelido -> FabTipoSalaMatrix.getTipoByAlias(apelido) != null).findFirst();

            if (!apelidoOficial.isPresent()) {
                return null;
            }
            FabTipoSalaMatrix tipoSala = FabTipoSalaMatrix.getTipoByAlias(apelidoOficial.get());

            switch (tipoSala) {

                case WTZAP_ATENDIMENTO:
                    telefone = UtilCRCStringFiltros.filtrarApenasNumeros(apelidoOficial.get());
                    break;
                case WTZAP_VENDAS:
                    telefone = UtilCRCStringFiltros.filtrarApenasNumeros(apelidoOficial.get());
                    break;
                case WTZAP_ATENDIMENTO_GRUPO_CLIENTE:
                    break;
                case MATRIX_CHAT_VENDAS:
                    break;
                case MATRIX_CHAT_ATENDIMENTO:
                    break;
                case MATRIX_CHAT_ATENDIMENTO_CHAMADO:
                    break;
                case MATRIX_CHAT_DEBATE_INTERNO_LEAD_CLIENTE:
                    break;
                default:
                    throw new AssertionError();
            }

            ComoUsuarioChat usr;
            try {
                if (!UtilCRCStringValidador.isNuloOuEmbranco(telefone)) {
                    usr = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByTelefone(telefone);
                    if (usr != null) {
                        getContato(usr);
                        return usr;
                    }
                }

            } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat | ErroCriandoContato ex) {
                SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha obtendo usuario de whatsapp do lead", ex);
            }
            for (ComoUsuarioChat usuario : pSala.getUsuarios()) {

                if (usuario.getTelefone() != null) {
                    if (usuario.getTelefone().length() >= 8) {
                        String finalTelefone = usuario.getTelefone().substring(usuario.getTelefone().length() - 8, usuario.getTelefone().length());
                        if (pSala.getApelido().contains(finalTelefone)) {
                            try {
                                getContato(usuario);
                            } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat | ErroCriandoContato ex) {
                                Logger.getLogger(RepositorioComunicacaoChat.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return usuario;
                        }
                    }
                }

                if (usuario.getEmail() != null) {
                    if (!usuario.getEmail().contains("casanovadigital")) {
                        try {
                            getContato(usuario);
                        } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat | ErroCriandoContato ex) {
                            Logger.getLogger(RepositorioComunicacaoChat.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return usuario;
                    }
                }
                if (usuario.getEmail() == null) {
                    try {
                        getContato(usuario);
                    } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat | ErroCriandoContato ex) {
                        Logger.getLogger(RepositorioComunicacaoChat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return usuario;
                }

            }
        } catch (Throwable t) {
            SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha identificando usuario padrao da asala " + pSala.getApelido(), t);
            return null;
        }
        return null;
    }

}
