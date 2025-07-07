/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.chat.logdeMensagens;

import br.com.casanovadigital.servicos.chat.legado.chat.controller.whatsapp.contato.ContatoWhatsapp;
import br.org.coletivoJava.integracoes.whatsapp.FabApiRestIntWhatsappPerfil;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.Persistencia.dao.UtilSBPersistencia;
import com.super_bits.modulosSB.Persistencia.dao.consultaDinamica.ConsultaDinamicaDeEntidade;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringTelefone;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.contato.CPContato;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtrorigemmatrix.CPMensagemTrOrigemMatrix;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtrorigemwhatsapp.CPMensagemTrOrigemWhatsapp;

/**
 *
 * @author salvio
 */
public class RepositorioComunicacaoChat {

    private static List<Contato> ULTIMOS_CONTATOS = new ArrayList<>();

    public MensagemTrOrigemWhatsapp getMensagemEnviadaPorWhatsappByRegistroMatrix(String pCodigoMensagemMatrix) {
        return (MensagemTrOrigemWhatsapp) new ConsultaDinamicaDeEntidade(MensagemTrOrigemWhatsapp.class)
                .addcondicaoCampoIgualA(CPMensagemTrOrigemWhatsapp.codigoencaminhamentomatrix, pCodigoMensagemMatrix).getPrimeiroRegistro();
    }

    public MensagemTrOrigemMatrix getMensagemEnviadaPorMatrixByRegistroWhatsapp(String pCodigoMensagemMAtrix) {
        return (MensagemTrOrigemMatrix) new ConsultaDinamicaDeEntidade(MensagemTrOrigemMatrix.class)
                .addcondicaoCampoIgualA(CPMensagemTrOrigemMatrix.codigoencaminhamentowhatsapp, pCodigoMensagemMAtrix).getPrimeiroRegistro();
    }

    public enum TIPO_ACESSO_REPOSITORIO {
        LEITURA, ATUALIZACAO
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
    public synchronized Contato operacoesDeRepositorio(TIPO_ACESSO_REPOSITORIO pTipoAcessos, ContatoWhatsapp pContatoRegistro, String wapId) {
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

    private synchronized Contato registrarDadosDoContato(ContatoWhatsapp pContato) {
        Optional<Contato> pesquisaContato = ULTIMOS_CONTATOS.stream().filter(ct -> ct.getWaid().equals(pContato.getWa_id())).findFirst();
        if (pesquisaContato.isPresent()) {
            return registraUltimoContato(pesquisaContato.get());
        }
        Contato contato = (Contato) UtilSBPersistencia.getRegistroByJPQL("from " + Contato.class.getSimpleName() + " where " + CPContato.waid + " = '" + pContato.getWa_id() + "'", Contato.class);
        if (contato == null) {
            contato = new Contato();
            contato.setNome(pContato.getNome());
            contato.setWaid(pContato.getWa_id());
            contato.setTelefone(UtilSBCoreStringTelefone.gerarCeluarInternacional(pContato.getWa_id()));
            return registraUltimoContato(UtilSBPersistencia.mergeRegistro(contato));
        } else {
            return registraUltimoContato(contato);
        }
    }

    //ATENÇÃO NUNCA CHAMAR ESSE METODO FORA DO registrarDadosDoContato, POIS PODE COMPROMETER A INCOMPATIBLIDIDADE DE CHAMADAS ASSINCRONAS DO ArrayList
    //Bloquear o chamado via stacktrace é CARO, e unificar o código deixa muito complexo.
    private synchronized static Contato registraUltimoContato(Contato novoContato) {
        // Verifica se o contato já existe na lista (comparando por id ou outro campo identificador)
        int indexExistente = -1;
        for (int i = 0; i < ULTIMOS_CONTATOS.size(); i++) {
            if (ULTIMOS_CONTATOS.get(i).equals(novoContato)) { // Assumindo equals() bem implementado
                indexExistente = i;
                break;
            }
        }

        if (indexExistente != -1) {
            // Se já existe, remove o contato para mover ao topo depois
            ULTIMOS_CONTATOS.remove(indexExistente);
        }

        // Adiciona no topo
        ULTIMOS_CONTATOS.add(0, novoContato);

        // Se ultrapassou 10, remove o último
        if (ULTIMOS_CONTATOS.size() > 10) {
            ULTIMOS_CONTATOS.remove(ULTIMOS_CONTATOS.size() - 1);
        }
        return novoContato;
    }

}
