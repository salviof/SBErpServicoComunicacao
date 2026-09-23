/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.salas.ServicoSalaSobDemanda;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos.ListenerSalaMatrix;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.NormalizarMembrosThread;
import br.org.coletivoJava.fw.erp.implementacao.chat.UtilMatrixERP;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.UsuarioChatMatrixOrg;
import com.super_bits.modulosSB.SBCore.ConfigGeral.CarameloCode;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCStringTelefone;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCStringValidador;
import com.super_bits.modulosSB.SBCore.modulos.Mensagens.FabMensagens;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.CHAT_DINAMICO_DE_ENTIDADE;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_ATENDIMENTO;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_ATENDIMENTO_CHAMADO;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_DEBATE_INTERNO_LEAD_CLIENTE;
import static br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix.MATRIX_CHAT_VENDAS;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import org.coletivojava.fw.api.tratamentoErros.ErroPreparandoObjeto;

/**
 *
 * @author salvio
 */
public class UtilAplicacaoWsChatMatrixSalas {

    private static final String TAG_LOG = "[SALA-CONTATO]";

    /**
     * Garante um usuário de contato com telefone, que é o que o alias canônico
     * da sala e a validação do getSalaMatrix exigem.
     *
     * O telefone do usuário Matrix vem do threepid msisdn. Enquanto ele não
     * estiver gravado no Synapse, getTelefone() é nulo, o alias sai como
     * "#nullwv" e o getSalaMatrix recusa a sala com "O telefone do usuário
     * externo não pode ser nulo" - mensagem que se perdia porque o
     * getSalaMatrixPadrao chama getSalaMatrix(null, ...) e o construtor de
     * ErroPreparandoObjeto estoura NullPointerException com objeto nulo.
     *
     * A cópia é deliberada: preencher o objeto do cache faria a comparação de
     * gerarUsuarioContato achar que o telefone já está registrado, e o threepid
     * nunca seria gravado no Synapse.
     */
    private static ComoUsuarioChat comTelefoneGarantido(ComoUsuarioChat pUsuarioContato, Contato pContato)
            throws ErroConexaoServicoChat {

        if (pUsuarioContato == null) {
            throw new ErroConexaoServicoChat("O usuário de chat do contato " + pContato.getWaid()
                    + " não foi encontrado no serviço de chat");
        }
        if (!UtilCRCStringValidador.isNuloOuEmbranco(pUsuarioContato.getTelefone())) {
            return pUsuarioContato;
        }

        String telefone = UtilCRCStringTelefone.gerarNumeroTelefoneInternacional(pContato.getWaid());
        if (UtilCRCStringValidador.isNuloOuEmbranco(telefone)) {
            throw new ErroConexaoServicoChat("O contato " + pContato.getWaid()
                    + " não tem telefone válido para compor a sala de atendimento");
        }

        try {
            CarameloCode.getServicoLogEventos().registrarLogDeEvento(FabMensagens.ALERTA, TAG_LOG
                    + " O usuário " + pUsuarioContato.getCodigoUsuario() + " está sem o threepid de telefone"
                    + " no Synapse. Usando " + telefone + " localmente para a sala poder ser criada;"
                    + " ver as linhas [MTX-USUARIO] para saber por que o threepid não foi gravado.");
        } catch (Throwable t) {
            System.out.println(TAG_LOG + " usuário sem threepid de telefone: " + pUsuarioContato.getCodigoUsuario());
        }

        UsuarioChatMatrixOrg usuarioComTelefone = new UsuarioChatMatrixOrg();
        usuarioComTelefone.setCodigoUsuario(pUsuarioContato.getCodigoUsuario());
        usuarioComTelefone.setNome(pUsuarioContato.getNome());
        usuarioComTelefone.setEmail(pUsuarioContato.getEmail());
        usuarioComTelefone.setTelefone(telefone);
        return usuarioComTelefone;
    }

    /**
     * Ponto único de criação de sala das trilhas: as duas sobrecargas de
     * TrilhaNavegacaoAbs.gerarSala e os gerarSalaAtendimentoPadrao dos serviços
     * de navegação passam por aqui.
     *
     * A criação em si roda fora da thread da requisição, com prazo de espera
     * (ver {@link ServicoSalaSobDemanda}). Se a sala não ficar pronta no prazo,
     * este método lança ErroConexaoServicoChat, mas a criação continua: quem
     * atende a requisição consulta
     * {@link ServicoSalaSobDemanda#isSalaEmCriacaoParaContato(java.lang.String)}
     * para saber que é espera, e não falha definitiva.
     */
    public static ComoChatSalaBean gerarSala(final EntradaNumeroWhatsapp pEntrada, final FabTipoSalaMatrix pTipoSala,
            final Contato pContato, final ComoUsuarioChat pUsuarioAtendimento,
            final boolean pRemoverOutrosUsuarios
    ) throws ErroConexaoServicoChat {

        switch (pTipoSala) {
            case CHAT_DINAMICO_DE_ENTIDADE:
            case MATRIX_CHAT_ATENDIMENTO_CHAMADO:
            case MATRIX_CHAT_VENDAS:
            case MATRIX_CHAT_ATENDIMENTO:
            case MATRIX_CHAT_DEBATE_INTERNO_LEAD_CLIENTE:

                throw new ErroConexaoServicoChat("tipo de sala não é compatível com estes parametros");
        }

        // O usuário do contato já é cacheado, e o apelido canônico é a chave de
        // deduplicação: precisa ser resolvido antes de submeter o trabalho.
        final ComoUsuarioChat usuarioContato = comTelefoneGarantido(
                AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(pContato.getMatrixID()), pContato);
        final String apelido = UtilMatrixERP.gerarAliasSalaIDCanonicoUsuarioWhatsapp(usuarioContato, pTipoSala.getSlug());

        return ServicoSalaSobDemanda.getSalaComPrazo(apelido, pContato.getWaid(),
                () -> criarSalaAgora(pTipoSala, usuarioContato, pUsuarioAtendimento, apelido, pRemoverOutrosUsuarios));

    }

    /**
     * Corpo original do gerarSala, agora executado numa thread do
     * {@link ServicoSalaSobDemanda}. Não pode encostar no EntityManager da
     * requisição.
     */
    private static ComoChatSalaBean criarSalaAgora(FabTipoSalaMatrix pTipoSala, ComoUsuarioChat pUsuarioContato,
            ComoUsuarioChat pUsuarioAtendimento, String pApelido, boolean pRemoverOutrosUsuarios)
            throws ErroConexaoServicoChat {

        try {
            ComoChatSalaBean salaIdeal = pTipoSala
                    .getSalaMatrixPadrao(pUsuarioAtendimento,
                            pUsuarioContato);

            ComoChatSalaBean salaRelacionada = AplicacaoWsChat.SERVICO_MATRIX.getSalaCriandoSeNaoExistir(salaIdeal, pApelido);
            NormalizarMembrosThread normalizarMembros = new NormalizarMembrosThread(AplicacaoWsChat.SERVICO_MATRIX, salaRelacionada.getCodigoChat(), salaIdeal.getUsuarios(), salaRelacionada.getUsuarios(), pRemoverOutrosUsuarios);
            normalizarMembros.start();
            AplicacaoWsChat.SERVICO_MATRIX.salaTornarMembroAdmin(salaIdeal, pUsuarioAtendimento.getCodigoUsuario());
            if (!AplicacaoWsChat.SERVICO_MATRIX.isSalaEscutaDefinida()) {
                AplicacaoWsChat.SERVICO_MATRIX.registrarClasseDeEscutaSalas(ListenerSalaMatrix.class
                );
            }

            AplicacaoWsChat.SERVICO_MATRIX.salaAbrirSessao(salaRelacionada);
            return (ComoChatSalaBean) salaRelacionada;

        } catch (ErroPreparandoObjeto ex) {
            throw new ErroConexaoServicoChat("Falha defininido sala de atendimento matrix" + ex.getMessage());
        }

    }

}
