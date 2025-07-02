/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller;

import br.com.casanovadigital.servicos.notificacao.chat.ServicoNotificacaoChat;

import br.com.casanovadigital.servicos.notificacao.chat.controller.salaChat.ApiSalaChatMembros;
import br.com.casanovadigital.servicos.notificacao.contextoChat.matrix.ContextoMatrix;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.UtilMatrixERP;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.FabTipoSalaMatrix;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.SalaMatrxOrg;
import br.org.coletivoJava.fw.ws.restFull.ErroAcessoNegado;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import br.org.coletivoJava.integracoes.matrixChat.FabApiRestIntMatrixChatUsuarios;
import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import br.org.coletivoJava.integracoes.restIntmatrixchat.implementacao.GestaoTokenRestIntmatrixchat;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.ConfigGeral.arquivosConfiguracao.ConfigModulo;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreJson;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringValidador;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.JsonObject;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coletivojava.fw.api.tratamentoErros.ErroPreparandoObjeto;
import org.coletivojava.fw.api.tratamentoErros.FabErro;
import spark.Route;

/**
 *
 * @author salvio
 */
public class ApiSalasChat {

    private static ConfigModulo config = SBCore.getConfigModulo(FabConfigApiMatrixChat.class);

    public static boolean checaConecaoAdmin() {
        GestaoTokenRestIntmatrixchat gestaoToken = (GestaoTokenRestIntmatrixchat) FabApiRestIntMatrixChatUsuarios.USUARIO_OBTER_DADOS.getGestaoToken();
        if (!gestaoToken.isTemTokemAtivo()) {
            gestaoToken.gerarNovoToken();
        }
        return gestaoToken.isTemTokemAtivo();
    }

    public static Route salaPrimeiroContatoGetMembros() {
        return new ApiSalaChatMembros();
    }

    public static Route salaPrimeiroContatoNovaPost() {

        return new RotaSparkPadrao() {

            private String conteudo;
            private JsonObject jsonCorpoRequisicao;
            private String emailUsuario;
            private String nomeUsuario;
            private String telefoneUsuario;
            private String emailRef;

            @Override
            public void validarParamentros() throws ErroParamentosInvalidos {
                if (false) {
                    String chaveacessoConfig = config.getPropriedade(FabConfigApiMatrixChat.SEGREDO);

                    String chaveAcesso = requisicao.headers("chaveAcesso");
                    if (UtilSBCoreStringValidador.isNuloOuEmbranco(chaveAcesso)) {
                        throw new ErroParamentosInvalidos("Chave de acesso não foi identificada");
                    }
                    if (!chaveAcesso.equals(chaveacessoConfig)) {
                        throw new ErroParamentosInvalidos("Chave de acesso inválida");
                    }
                }
                conteudo = requisicao.body();
                if (UtilSBCoreStringValidador.isNuloOuEmbranco(conteudo)) {
                    throw new ErroParamentosInvalidos("Corpo da requisição não enviado");
                }
                jsonCorpoRequisicao = UtilSBCoreJson.getJsonObjectByTexto(conteudo);
                if (jsonCorpoRequisicao == null) {
                    throw new ErroParamentosInvalidos("formato json do cormpo não reconhecido");
                }

                emailUsuario = UtilSBCoreJson.getValorApartirDoCaminho("leadEmail", jsonCorpoRequisicao);
                if (UtilSBCoreStringValidador.isNuloOuEmbranco(emailUsuario)) {
                    throw new ErroParamentosInvalidos("leadEmail não enviado");
                }
                nomeUsuario = UtilSBCoreJson.getValorApartirDoCaminho("leadNome", jsonCorpoRequisicao);
                if (UtilSBCoreStringValidador.isNuloOuEmbranco(nomeUsuario)) {
                    throw new ErroParamentosInvalidos("leadNome não enviado");
                }

                telefoneUsuario = UtilSBCoreJson.getValorApartirDoCaminho("leadTelefone", jsonCorpoRequisicao);
                if (UtilSBCoreStringValidador.isNuloOuEmbranco(telefoneUsuario)) {
                    throw new ErroParamentosInvalidos("leadTelefone não enviado");
                }

                emailRef = UtilSBCoreJson.getValorApartirDoCaminho("refEmail", jsonCorpoRequisicao);

                if (UtilSBCoreStringValidador.isNuloOuEmbranco(emailRef)) {
                    throw new ErroParamentosInvalidos("refEmail não enviado");
                }
            }

            @Override
            public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {

                if (!checaConecaoAdmin()) {
                    throw new ErroConexaoSistemaTerceiro("Falha conectando com Matrixchat");
                }

                ItfUsuarioChat usuarioREpresentante;
                try {
                    usuarioREpresentante = ContextoMatrix.getChatMatrixService().getUsuarioByEmail(emailRef);
                    if (usuarioREpresentante == null) {
                        throw new ErroRecursoNaoEncontrado("refEmail: " + emailRef + " não encontrado");

                    }

                    ItfUsuarioChat usuarioNovoContato = ContextoMatrix.getChatMatrixService().getUsuarioByEmail(emailUsuario);

                    if (usuarioNovoContato != null) {

                        System.out.println("Usuário já cadastrado" + usuarioNovoContato.getEmail());
                        System.out.println(usuarioNovoContato.getCodigoUsuario());
                    } else {

                        if (usuarioNovoContato == null) {
                            System.out.println("Cadastrando novo usuário");
                            usuarioNovoContato = UtilMatrixERP.gerarUsuarioUnicoByEmail(nomeUsuario, emailUsuario, telefoneUsuario);
                            usuarioNovoContato = ContextoMatrix.getChatMatrixService().usuarioCriar(usuarioNovoContato);
                            if (usuarioNovoContato != null) {
                                System.out.println("Usuário criado");
                                System.out.println(usuarioNovoContato.getCodigoUsuario());
                            }
                        }
                    }
                    SalaMatrxOrg sala = UtilMatrixERP.gerarSala(FabTipoSalaMatrix.MATRIX_CHAT_ATENDIMENTO, usuarioREpresentante, usuarioREpresentante, usuarioNovoContato);
                    ItfChatSalaBean salaCriada = ContextoMatrix.getChatMatrixService().getSalaCriandoSeNaoExistir(sala);
                    if (salaCriada != null) {
                        System.out.println("Sala criada com sucesso");
                        System.out.println(salaCriada.getCodigoChat());
                    }
                    return salaCriada.getCodigoChat();
                } catch (ErroConexaoServicoChat ex) {
                    throw new ErroConexaoSistemaTerceiro("Conexão com matrix falhou" + ex.getMessage());

                } catch (ErroPreparandoObjeto ex) {
                    throw new ErroRecursoNaoEncontrado("Erro criando dados da sala a apartir de mensagem de wtzap" + ex.getMessage());
                }

            }
        };

    }

    public static Route salaPrimeiroContatoGet() {

        return new RotaSparkPadrao() {
            private String salaBatiPapo;

            @Override
            public void validarParamentros() throws ErroParamentosInvalidos {
                if (requisicao.splat().length != 1) {
                    throw new ErroParamentosInvalidos("Sala não encontrada");

                }
                salaBatiPapo = requisicao.splat()[0];
            }

            @Override
            public void validarPermissao() throws ErroAcessoNegado {

            }

            @Override
            public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {
                System.out.println("Pesquisando sala" + salaBatiPapo);
                ItfChatSalaBean conversa;
                try {
                    conversa = ContextoMatrix.getChatMatrixService().getSalaByCodigo(salaBatiPapo);
                } catch (ErroConexaoServicoChat ex) {
                    throw new ErroConexaoSistemaTerceiro("Falha obtendo dados da sala");
                }
                if (conversa != null) {
                    System.out.println("Sala encontrada");
                    System.out.println(conversa.getCodigoChat());

                    System.out.println(conversa.getUsuarios());
                } else {
                    //UtilSBCoreJsonRest
                    throw new ErroRecursoNaoEncontrado("Sala não encontrada");

                }
                System.out.println("Usuários da sala:");
                for (ItfUsuarioChat usuario : conversa.getUsuarios()) {
                    System.out.println("__________");
                    System.out.println(usuario.getNome());
                    System.out.println(usuario.getEmail());
                    System.out.println(usuario.getEmailPrincipal());
                }
                Optional<ItfUsuarioChat> usuarioClientePesquisa = conversa.getUsuarios().stream()
                        .filter(usr -> usr.getEmailPrincipal() != null && !usr.getEmailPrincipal().contains("casanovadigital")).findFirst();

                if (!usuarioClientePesquisa.isPresent()) {
                    throw new ErroRecursoNaoEncontrado("Usuários não encotrados");
                }
                ItfUsuarioChat usuarioCliente = usuarioClientePesquisa.get();
                System.out.println("Autenticando com usuário");
                System.out.println(usuarioCliente.getCodigoUsuario());

                if (!ContextoMatrix.getChatMatrixService().tokenGestaoEfetuarLogin(usuarioCliente)) {

                    try {

                        System.out.println("Falha ao autenticar com o usuário");
                        System.out.println("Atualizando senha do usuário");
                        System.out.println(usuarioCliente.getCodigoUsuario());
                        String senha = UtilMatrixERP.gerarSenha(usuarioCliente);
                        if (ContextoMatrix.getChatMatrixService().usuarioAtualizarSenha(usuarioCliente.getCodigoUsuario(), senha)) {
                            System.out.println("Senha trocada com sucesso");
                        } else {
                            System.out.println("Houve um erro ao alterar a senha");
                        }

                        if (!ContextoMatrix.getChatMatrixService().tokenGestaoEfetuarLogin(usuarioCliente, senha)) {
                            System.out.println("O login foi aceito após atualização da senha");
                        } else {
                            System.out.println("O login foi não foi aceito mesmo após a atualização de senha");
                            resposta.redirect("/Pagina não existe");
                            return "";
                        }

                    } catch (Throwable t) {
                        SBCore.RelatarErro(FabErro.SOLICITAR_REPARO, "Falha gerando token" + t.getMessage(), t);
                        return "Erro gerando token " + t.getMessage();
                    }

                }
                System.out.println("Usuario autenticado, atualizando cookies");
                // response.cookie("cndMatrixSessao", gestaoToken.getToken());
                GestaoTokenRestIntmatrixchat gestaoTokenUsuario = (GestaoTokenRestIntmatrixchat) FabApiRestIntMatrixChatUsuarios.USUARIO_OBTER_DADOS.getGestaoToken(usuarioCliente);
                resposta.cookie("/", "cndMatrixSessao", gestaoTokenUsuario.getToken(), 3600, true);
                resposta.cookie("/", "cndMatrixSala", conversa.getCodigoChat(), 3600, true);//ie("cndMatrixSala", salaBatipabo);
                resposta.cookie("/", "cndMatrixusuario", usuarioCliente.getCodigoUsuario(), 3600, true);//ie("cndMatrixSala", salaBatipabo);
                System.out.println("Atualizando página");
                return "<!DOCTYPE html>\n"
                        + "<html>\n"
                        + "<head>\n"
                        + "    <meta charset=\"utf-8\"/>\n"
                        + "    <title>Meu Redirect</title>\n"
                        + "\n"
                        + "    <meta http-equiv=\"refresh\" content=\"0; URL='/salaAtendimento.html'\"/>\n"
                        + "</head>\n"
                        + "<body>\n"
                        + "    <p>\n"
                        + "        Aguarde:\n"
                        + "    </p>\n"
                        + "</body>\n"
                        + "</html>";

            }
        };

    }

}
