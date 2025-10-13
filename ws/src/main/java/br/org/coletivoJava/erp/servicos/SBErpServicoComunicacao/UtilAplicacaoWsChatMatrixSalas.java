/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos.ListenerSalaMatrix;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.NormalizarMembrosThread;
import br.org.coletivoJava.fw.erp.implementacao.chat.UtilMatrixERP;
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

    public static ItfChatSalaBean gerarSala(EntradaNumeroWhatsapp pEntrada, FabTipoSalaMatrix pTipoSala, Contato pContato, ItfUsuarioChat pUsuarioAtendimento,
            boolean pRemoverOutrosUsuarios
    ) throws ErroConexaoServicoChat {

        switch (pTipoSala) {
            case CHAT_DINAMICO_DE_ENTIDADE:
            case MATRIX_CHAT_ATENDIMENTO_CHAMADO:
            case MATRIX_CHAT_VENDAS:
            case MATRIX_CHAT_ATENDIMENTO:
            case MATRIX_CHAT_DEBATE_INTERNO_LEAD_CLIENTE:

                throw new ErroConexaoServicoChat("tipo de sala não é compatível com estes parametros");
        }

        ItfUsuarioChat usuarioContato;
        try {
            usuarioContato = AplicacaoWsChat.SERVICO_MATRIX.getUsuarioByCodigo(pContato.getMatrixID());

            ItfChatSalaBean salaIdeal = pTipoSala
                    .getSalaMatrixPadrao(pUsuarioAtendimento,
                            usuarioContato);

            String apelido = UtilMatrixERP.gerarAliasSalaIDCanonicoUsuarioWhatsapp(usuarioContato, pTipoSala.getSlug());
            ItfChatSalaBean salaRelacionada = AplicacaoWsChat.SERVICO_MATRIX.getSalaCriandoSeNaoExistir(salaIdeal, apelido);
            NormalizarMembrosThread normalizarMembros = new NormalizarMembrosThread(AplicacaoWsChat.SERVICO_MATRIX, salaRelacionada.getCodigoChat(), salaIdeal.getUsuarios(), salaRelacionada.getUsuarios(), pRemoverOutrosUsuarios);
            normalizarMembros.start();
            AplicacaoWsChat.SERVICO_MATRIX.salaTornarMembroAdmin(salaIdeal, pUsuarioAtendimento.getCodigoUsuario());
            if (!AplicacaoWsChat.SERVICO_MATRIX.isSalaEscutaDefinida()) {
                AplicacaoWsChat.SERVICO_MATRIX.registrarClasseDeEscutaSalas(ListenerSalaMatrix.class
                );
            }

            AplicacaoWsChat.SERVICO_MATRIX.salaAbrirSessao(salaRelacionada);
            return (ItfChatSalaBean) salaRelacionada;

        } catch (ErroPreparandoObjeto ex) {
            throw new ErroConexaoServicoChat("Falha defininido sala de atendimento matrix" + ex.getMessage());
        }

    }

}
