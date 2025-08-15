package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.trilha_navegacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.rotas.RotaMensagemContato;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.MensagemWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos.ListenerSalaMatrix;

import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.UtilMatrixERP;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import java.util.Date;
import java.util.List;
import org.coletivojava.fw.api.tratamentoErros.ErroPreparandoObjeto;

/**
 *
 * @author salvio
 */
public abstract class TrilhaNavegacaoAbs implements ItfTrilhaNavegacao {

    private Long id;

    private ItfTrilhaNavegacao trilhaOrigem;
    private String nome;
    private List<ItfTrilhaNavegacao> trilhasDisponiveis;
    private EntradaNumeroWhatsapp entrada;
    private String caminhoTrilha;
    protected RotaMensagemContato rotaAtual;
    private ContextoContato contextoDeSessao;

    public TrilhaNavegacaoAbs(ContextoContato pContato, ItfTrilhaNavegacao pTrilhaOrigem, EntradaNumeroWhatsapp pEntrada, String pCaminhoTrilha) {
        entrada = pEntrada;
        trilhaOrigem = pTrilhaOrigem;
        caminhoTrilha = pCaminhoTrilha;
        contextoDeSessao = pContato;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ItfTrilhaNavegacao getTrilhaOrigem() {
        return trilhaOrigem;
    }

    public List<ItfTrilhaNavegacao> getTrilhasDisponiveis() {
        return trilhasDisponiveis;
    }

    public EntradaNumeroWhatsapp getEntrada() {
        return entrada;
    }

    public String getCaminhoTrilha() {
        return caminhoTrilha;
    }

    protected ItfChatSalaBean gerarSala(EntradaNumeroWhatsapp pEntrada, FabTipoSalaMatrix pTipoSala, Contato pContato, ItfUsuarioChat pUsuarioAtendimento) throws ErroConexaoServicoChat {

        ItfUsuarioChat UsuarioContato;
        try {
            UsuarioContato = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(pContato.getMatrixID());
            ItfChatSalaBean salaIdeal = pTipoSala
                    .getSalaMatrixPadrao(pUsuarioAtendimento,
                            UsuarioContato);

            String apelido = UtilMatrixERP.gerarAliasSalaIDCanonicoUsuarioWhatsapp(UsuarioContato, pTipoSala.getSlug());
            ItfChatSalaBean salaRelacionada = AplicacaoWsChat.SERVICO_MATRIX.getSalaCriandoSeNaoExistir(salaIdeal, apelido);
            if (!AplicacaoWsChat.SERVICO_MATRIX.isSalaEscutaDefinida()) {
                AplicacaoWsChat.SERVICO_MATRIX.registrarClasseDeEscutaSalas(ListenerSalaMatrix.class);
            }
            AplicacaoWsChat.SERVICO_MATRIX.salaAbrirSessao(salaRelacionada);
            return (ItfChatSalaBean) salaRelacionada;

        } catch (ErroPreparandoObjeto ex) {
            throw new ErroConexaoServicoChat("Falha defininido sala de atendimento matrix" + ex.getMessage());
        }

    }

    @Override
    public Class<? extends ItfTrilhaNavegacao> getClasseDesvioDeTrilha(MensagemWhatsapp p) throws ErroComDevolucaoMensagemUsuario {
        if (p.getPayloadRespostaProgramada() != null && !p.getPayloadRespostaProgramada().isEmpty()) {

            ItfServicoNavegacao servicoNavegacao = AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getServicoNavegacao(entrada);
            Class classe = servicoNavegacao.getClasseTrilhaDeNavegacao(getContextoDeSessao().getContato(), p.getPayloadRespostaProgramada());
            return classe;

        }
        return null;
    }

    public RotaMensagemContato getRotaAtual() {
        return rotaAtual;
    }

    public ContextoContato getContextoDeSessao() {
        return contextoDeSessao;
    }

    @Override
    public void finalizarSesaso() {
        contextoDeSessao.setDataHoraFinalSessao(new Date());

    }

}
