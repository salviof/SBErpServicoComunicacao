/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.servicoServer.escutas.matrix.monitorDeEventos;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroComDevolucaoMensagemUsuario;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroCriandoContato;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComandoDeAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.model.ErroComandoAtendimentoInvalido;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfListenerEventoComandoAtendimento;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringFiltros;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salvio
 */
public class ListenerComandosPadrao implements ItfListenerEventoComandoAtendimento {

    enum TIPO_COMANDO {
        NOVA_ROTA,
        AGENDA_CONSULTOR,
        AGENDA_ATENDIMENTO,
        AREA_CLIENTE,
        SALA_VENDAS,
        SALA_ATENDIMENTO;

        public String getComando() {

            switch (this) {

                case NOVA_ROTA:
                    return "rota";

                case AGENDA_CONSULTOR:
                    return "agenda venda";
                case AGENDA_ATENDIMENTO:

                    return "agenda atendimento";
                case AREA_CLIENTE:
                    return "linkCliente";
                case SALA_VENDAS:
                    return "vendas";

                case SALA_ATENDIMENTO:
                    return "atendimento";

                default:
                    throw new AssertionError();
            }

        }

        public String getDescricao() {
            String descricao = "";
            switch (this) {
                case NOVA_ROTA:
                    descricao = "Para enviar o Cliente para uma nova rota";
                    break;
                case AGENDA_CONSULTOR:
                    descricao = "Envia um link para acesso direto ao agendamento de um horário com o consultor";
                    break;
                case AGENDA_ATENDIMENTO:
                    descricao = "Envia um link para acesso direto ao agendamento de um horário com o Gestor de Sucesso";
                    break;
                case AREA_CLIENTE:
                    descricao = "Envia um link para acesso direto a área do cliente";
                    break;
                case SALA_VENDAS:
                    descricao = "Redireciona o whatsapp dos contatos para o canal de vendas";
                    break;
                case SALA_ATENDIMENTO:
                    descricao = "Redireciona o whatsapp dos contatos para o canal de atendimento";
                    break;

                default:
                    throw new AssertionError();
            }
            return descricao;
        }

        public List<String> getParametros() {
            List<String> parametros = new ArrayList<>();

            switch (this) {
                case NOVA_ROTA:
                    parametros.add("caminho da rota (Ex: chamado.123)");
                    break;
                case AGENDA_CONSULTOR:
                    break;
                case AGENDA_ATENDIMENTO:
                    break;
                case AREA_CLIENTE:
                    break;
                case SALA_VENDAS:
                    break;
                case SALA_ATENDIMENTO:
                    break;

                default:
                    throw new AssertionError();
            }
            return parametros;
        }

        public static TIPO_COMANDO getTipo(String pCampo) {
            if (pCampo == null) {
                return null;
            }
            for (TIPO_COMANDO cmd : TIPO_COMANDO.values()) {
                if (cmd.getComando().equals(pCampo)) {
                    return cmd;
                }
            }
            return null;
        }
    }

    @Override
    public void processarComando(ComandoDeAtendimento pComando) throws ErroComandoAtendimentoInvalido {

        System.out.println(pComando.getEvento().getContent().toString());

        try {

            if (pComando.getEvento().getRoom_id() != null && !pComando.getEvento().getRoom_id().isEmpty()) {

                TIPO_COMANDO comando = TIPO_COMANDO.getTipo(pComando.getComando());

                ItfChatSalaBean sala;

                sala = AplicacaoWsChat.SERVICO_MATRIX.getSalaByCodigo(pComando.getEvento().getRoom_id());

                if (comando == null) {
                    AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(sala, "Comando " + pComando.getTextoCompleto() + " não foi encontrado");
                    StringBuilder help = new StringBuilder();
                    for (TIPO_COMANDO cmd : TIPO_COMANDO.values()) {

                        help.append(UtilSBCoreStringFiltros.getRpad(cmd.getComando(), 20, " "));
                        //   help.append(UtilSBCoreStringFiltros.getRpad(cmd.getComando(), 5, " "));
                        if (!cmd.getParametros().isEmpty()) {
                            help.append("Parametros:");
                            int i = 1;
                            for (String pr : cmd.getParametros()) {

                                help.append(i);
                                help.append(UtilSBCoreStringFiltros.getRpad(pr, 20, " "));
                                i++;
                            }
                            help.append("\n");
                        }

                        String descricao = UtilSBCoreStringFiltros.quebrarStringEmLinhas(cmd.getDescricao(), 40);
                        help.append("\n");
                        help.append(descricao);
                        help.append("\n\n");
                    }
                    AplicacaoWsChat.SERVICO_MATRIX.salaEnviarMesagem(sala, help.toString());
                    return;
                }

                List<ItfUsuarioChat> contatos = new ArrayList<>();
                for (ItfUsuarioChat usr : sala.getUsuarios()) {
                    if (AplicacaoWsChat.SERVICO_MATRIX.isUmUsuarioContato(usr)) {
                        contatos.add(usr);
                    }
                }
                if (pComando.getNovaRota() != null) {
                    for (ItfUsuarioChat pUsuario : contatos) {
                        Contato ct;
                        switch (comando) {
                            case NOVA_ROTA:
                                EntradaNumeroWhatsapp entrada;
                                entrada = AplicacaoWsChat.getCentralLogicaProcesasmento().getEntradaBySala(sala.getApelido());
                                try {
                                    ct = AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato(pUsuario);
                                } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat | ErroCriandoContato ex) {
                                    throw new ErroComandoAtendimentoInvalido(ex.getMessage());
                                }
                                if (ct != null) {
                                    try {
                                        AplicacaoWsChat.GESTAO_SERVICO_NAVEGACAO.getTrilhaByComandoMatrix(entrada, ct, pComando);
                                    } catch (ErroComDevolucaoMensagemUsuario ex) {

                                    }
                                }
                                break;
                            case AGENDA_CONSULTOR:

                                break;
                            case AGENDA_ATENDIMENTO:
                                break;
                            case AREA_CLIENTE:
                                break;
                            case SALA_VENDAS:
                                break;
                            case SALA_ATENDIMENTO:
                                break;
                            default:
                                throw new AssertionError();
                        }

                    }
                }

            }
        } catch (Throwable ex) {
            throw new ErroComandoAtendimentoInvalido(" Erro processando comando" + ex.getMessage());
        }

    }

}
