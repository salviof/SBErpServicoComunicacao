/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller;

import br.com.casanovadigital.servicos.notificacao.chat.controller.listenerMatrix.ListenerSalaMatrix;
import br.com.casanovadigital.servicos.notificacao.chat.controller.whatsapp.mensagem.MensagemWhatsapp;
import br.com.casanovadigital.servicos.notificacao.contextoChat.matrix.ContextoMatrix;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.ChatMatrixOrgimpl;
import br.org.coletivoJava.fw.erp.implementacao.chat.UtilMatrixERP;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import br.org.coletivoJava.integracoes.matrixChat.FabApiRestIntMatrixChatSalas;
import br.org.coletivoJava.integracoes.matrixChat.FabApiRestIntMatrixSpaces;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringFiltros;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringValidador;
import com.super_bits.modulosSB.SBCore.integracao.libRestClient.WS.conexaoWebServiceClient.ItfRespostaWebServiceSimples;
import jakarta.json.JsonArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivojava.fw.api.tratamentoErros.ErroPreparandoObjeto;

/**
 *
 * @author salvio
 */
public class MapAtendentesMatrixCAsanovadigital {

    public static final String CODIGO_ENTRADA_TELEVENDAS_CASANOVA_BH9755 = "114354588403482";
    public static final String CODIGO_ENRTRADA_GESTAO_CASANOVA_BH9751 = "SEMREGISTRO";

    private static Map<String, List<ItfUsuarioChat>> MAPA_PONTO_ENTRADA_USUARIO_ATENDIMENTO = new HashMap<>();

    public static ItfUsuarioChat getUserAtendimentoByEmail(String pCondigoOrigem, String pEmailAtendimento) {
        carregarUsuarios();
        Optional<ItfUsuarioChat> usuarioIdeal = MAPA_PONTO_ENTRADA_USUARIO_ATENDIMENTO.get(pCondigoOrigem)
                .stream().filter(usr -> !usr.getEmail().isEmpty() && usr.getEmail().contains(pEmailAtendimento))
                .findFirst();
        if (usuarioIdeal.isPresent()) {
            return usuarioIdeal.get();
        }
        return null;
    }

    public static ItfUsuarioChat getUsuarioAtendimentoPadrao(String pCodigoOrigem) {
        carregarUsuarios();
        if (pCodigoOrigem.equals(CODIGO_ENTRADA_TELEVENDAS_CASANOVA_BH9755)) {
            Optional<ItfUsuarioChat> pesquisaUsuario = MAPA_PONTO_ENTRADA_USUARIO_ATENDIMENTO.get(pCodigoOrigem).stream().filter(usr -> usr != null && usr.getEmail().contains("salvio@casanovadigital.com.br")).findFirst();
            if (pesquisaUsuario.isPresent()) {
                return pesquisaUsuario.get();
            }

        }

        if (pCodigoOrigem.equals(CODIGO_ENRTRADA_GESTAO_CASANOVA_BH9751)) {
            Optional<ItfUsuarioChat> pesquisaUsuario = MAPA_PONTO_ENTRADA_USUARIO_ATENDIMENTO.get(pCodigoOrigem).stream().filter(usr -> usr != null && usr.getEmail().contains("patricia@casanovadigital.com.br"))
                    .findFirst();
            if (pesquisaUsuario.isPresent()) {
                pesquisaUsuario.get();
            }
        }
        Optional< ItfUsuarioChat> pesquisaUsuario
                = MAPA_PONTO_ENTRADA_USUARIO_ATENDIMENTO.get(pCodigoOrigem).stream().filter(usr -> usr.getEmail().contains("salvio@casanovadigital.com.br"))
                        .findFirst();
        if (pesquisaUsuario.isPresent()) {
            pesquisaUsuario.get();
        }
        ChatMatrixOrgimpl erpChatService = ContextoMatrix.getChatMatrixService();
        try {
            return erpChatService.getUsuarioByEmail("salvio@casanovadigital.com.br");
        } catch (ErroConexaoServicoChat ex) {
            return null;
        }
    }

    private static void carregarUsuarios() {
        if (MAPA_PONTO_ENTRADA_USUARIO_ATENDIMENTO.isEmpty()) {
            System.out.println("Carregando usuários");
            ChatMatrixOrgimpl erpChatService = ContextoMatrix.getChatMatrixService();
            try {
                MAPA_PONTO_ENTRADA_USUARIO_ATENDIMENTO.put(CODIGO_ENTRADA_TELEVENDAS_CASANOVA_BH9755, new ArrayList<>());
                MAPA_PONTO_ENTRADA_USUARIO_ATENDIMENTO.put(CODIGO_ENRTRADA_GESTAO_CASANOVA_BH9751, new ArrayList<>());
                System.out.println("Obtendo usuario Wagner");
                ItfUsuarioChat wagner = erpChatService.getUsuarioByEmail("wagner@casanovadigital.com.br");
                if (wagner == null) {
                    MAPA_PONTO_ENTRADA_USUARIO_ATENDIMENTO.get(CODIGO_ENTRADA_TELEVENDAS_CASANOVA_BH9755).add(wagner);
                }

                ItfUsuarioChat salvio = erpChatService.getUsuarioByEmail("salvio@casanovadigital.com.br");
                if (salvio != null) {
                    MAPA_PONTO_ENTRADA_USUARIO_ATENDIMENTO.get(CODIGO_ENTRADA_TELEVENDAS_CASANOVA_BH9755).add(salvio);
                }

                ItfUsuarioChat patricia = erpChatService.getUsuarioByEmail("patricia@casanovadigital.com.br");

                ItfUsuarioChat beatriz = erpChatService.getUsuarioByEmail("beatriz@casanovadigital.com.br");

                if (patricia != null) {
                    MAPA_PONTO_ENTRADA_USUARIO_ATENDIMENTO.get(CODIGO_ENRTRADA_GESTAO_CASANOVA_BH9751).add(patricia);
                }
                if (beatriz != null) {
                    MAPA_PONTO_ENTRADA_USUARIO_ATENDIMENTO.get(CODIGO_ENRTRADA_GESTAO_CASANOVA_BH9751).add(beatriz);
                }

            } catch (ErroConexaoServicoChat ex) {
                Logger.getLogger(MapAtendentesMatrixCAsanovadigital.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static String getPontodeEntradaByApelidoUnicoSala(String pApelido) {
        carregarUsuarios();
        FabTipoSalaMatrix tipoSala = FabTipoSalaMatrix.getTipoByAlias(pApelido);
        switch (tipoSala) {

            case WTZAP_ATENDIMENTO:
                return CODIGO_ENRTRADA_GESTAO_CASANOVA_BH9751;
            case WTZAP_VENDAS:
                return CODIGO_ENTRADA_TELEVENDAS_CASANOVA_BH9755;

            case WTZAP_ATENDIMENTO_GRUPO_CLIENTE:
                return CODIGO_ENRTRADA_GESTAO_CASANOVA_BH9751;

            default:
                System.out.println("SLUG NÃO ENCONTRADO PARA NOME DA SALA " + pApelido);
                return null;
        }

    }

    public static ItfUsuarioChat getUsuarioWhatsappLead(ItfChatSalaBean pSala) {
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
                usr = ContextoMatrix.getChatMatrixService().getUsuarioByTelefone(telefone);
                if (usr != null) {
                    return usr;
                }
            }

        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(ListenerSalaMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (ItfUsuarioChat usuario : pSala.getUsuarios()) {

            if (usuario.getTelefone() != null) {
                if (usuario.getTelefone().length() >= 8) {
                    String finalTelefone = usuario.getTelefone().substring(usuario.getTelefone().length() - 8, usuario.getTelefone().length());
                    if (pSala.getApelido().contains(finalTelefone)) {
                        return usuario;
                    }
                }
            }

            if (usuario.getEmail() != null) {
                if (!usuario.getEmail().contains("casanovadigital")) {
                    return usuario;
                }
            }
            if (usuario.getEmail() == null) {
                return usuario;
            }

        }
        return null;
    }

    public static ItfUsuarioChat getUsuarioAtendimento(String pCodigoOrigem, MensagemWhatsapp pPacote) {
        carregarUsuarios();
        System.out.println("PEsquisando usuario atendente em origem: " + pCodigoOrigem);
        System.out.println("Nome origem=" + pPacote.getNome());
        ItfUsuarioChat pUsuarioAtendimento = UtilAgenciaContatos.getContatoAtendimentoByTelefone(pCodigoOrigem, pPacote.getTelefone());
        if (pUsuarioAtendimento != null) {
            return pUsuarioAtendimento;
        }
        return MapAtendentesMatrixCAsanovadigital.getUsuarioAtendimentoPadrao(pCodigoOrigem);
    }

    private static Map<String, String> MAPA_ALIAS_CODIGO_SALA = new HashMap<>();
}
