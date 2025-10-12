package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.logdeMensagens;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;

import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Atendente;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.EncaminhamentoMatrixParaWtzp;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.Persistencia.dao.consultaDinamica.ConsultaDinamicaDeEntidade;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreDataHora;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreListasObjeto;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringTelefone;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringValidador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.contato.CPContato;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.encaminhamentomatrixparawtzp.CPEncaminhamentoMatrixParaWtzp;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtrorigemwhatsapp.CPMensagemTrOrigemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.ContatoWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroCriandoContato;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
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
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringFiltros;
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

    public synchronized Atendente getAtendente(ItfUsuarioChat pUSuarioAtendimento) {

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

    public synchronized Contato getContato(ItfUsuarioChat pUsuario) throws ErroConexaoServicoChat, ErroRegraDeNEgocioChat, ErroCriandoContato {
        if (UtilSBCoreStringValidador.isNuloOuEmbranco(pUsuario.getTelefone())) {
            throw new ErroRegraDeNEgocioChat("Telefone do usuário não foi definido");
        }
        String telefonewtzp = UtilSBCoreStringTelefone.gerarCeluarWhatasapp(pUsuario.getTelefone());
        Contato contato = getContato(telefonewtzp);
        if (contato == null) {
            return getContato(new ContatoWhatsapp(UtilSBCoreStringTelefone.gerarCeluarWhatasapp(pUsuario.getTelefone()), pUsuario.getNome()));
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

                ItfUsuarioChat usuarioContatoChat = AplicacaoWsChat.SERVICO_MATRIX.gerarUsuarioContato(pContato.getNome(), UtilSBCoreStringTelefone.gerarCeluarInternacional(pContato.getWa_id()));
                contato.setMatrixID(usuarioContatoChat.getCodigoUsuario());
                contato.setDataHoraUltimaInteracao(new Date());
                contato.setTelefone(UtilSBCoreStringTelefone.gerarCeluarInternacional(pContato.getWa_id()));
                contato = UtilSBPersistencia.mergeRegistro(contato, em);
                if (contato == null) {
                    throw new ErroConexaoServicoChat("Falha persistindo contato no banco de dados");
                }
                return registraUltimoContato(contato);
            } else {

                contato.setNome(pContato.getNome());
                contato.setWaid(pContato.getWa_id());

                ItfUsuarioChat usuarioContatoChat = AplicacaoWsChat.SERVICO_MATRIX.gerarUsuarioContato(pContato.getNome(), UtilSBCoreStringTelefone.gerarCeluarInternacional(pContato.getWa_id()));
                contato.setMatrixID(usuarioContatoChat.getCodigoUsuario());
                contato.setDataHoraUltimaInteracao(new Date());
                contato.setTelefone(UtilSBCoreStringTelefone.gerarCeluarInternacional(pContato.getWa_id()));
                contato = UtilSBPersistencia.mergeRegistro(contato, em);

                return registraUltimoContato(contato);
            }
        } finally {
            UtilSBPersistencia.fecharEM(em);
        }
    }

    //ATENÇÃO NUNCA CHAMAR ESSE METODO FORA DO registrarDadosDoContato, POIS PODE COMPROMETER A INCOMPATIBLIDIDADE DE CHAMADAS ASSINCRONAS DO ArrayList
    //Bloquear o chamado via stacktrace é CARO, e unificar o código deixa muito complexo.
    private synchronized static Contato registraUltimoContato(Contato novoContato) {

        if (novoContato.getDataHoraUltimaInteracao() != null) {
            if (UtilSBCoreDataHora.intervaloTempoHoras(novoContato.getDataHoraUltimaInteracao(), new Date()) > 1) {
                novoContato.setDataHoraUltimaInteracao(new Date());
                novoContato = UtilSBPersistencia.mergeRegistro(novoContato);
            } else {
                novoContato.setDataHoraUltimaInteracao(new Date());
            }

        }

        UtilSBCoreListasObjeto.listaLimitadaDeObjetos(ULTIMOS_CONTATOS, novoContato, 20);
        return novoContato;
    }

    public ItfUsuarioChat getUsuarioWhatsappPricipalLeadBySala(ItfChatSalaBean pSala) {
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
                    telefone = UtilSBCoreStringFiltros.filtrarApenasNumeros(apelidoOficial.get());
                    break;
                case WTZAP_VENDAS:
                    telefone = UtilSBCoreStringFiltros.filtrarApenasNumeros(apelidoOficial.get());
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

            ItfUsuarioChat usr;
            try {
                if (!UtilSBCoreStringValidador.isNuloOuEmbranco(telefone)) {
                    usr = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByTelefone(telefone);
                    if (usr != null) {
                        getContato(usr);
                        return usr;
                    }
                }

            } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat | ErroCriandoContato ex) {
                SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha obtendo usuario de whatsapp do lead", ex);
            }
            for (ItfUsuarioChat usuario : pSala.getUsuarios()) {

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
