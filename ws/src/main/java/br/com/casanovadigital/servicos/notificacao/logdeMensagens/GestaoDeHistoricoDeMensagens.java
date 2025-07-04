/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.logdeMensagens;

import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemMatrix;
import com.super_bits.casanovadigital.servicos.messagens.model.mensagem.MensagemTrOrigemWhatsapp;
import com.super_bits.modulosSB.Persistencia.dao.consultaDinamica.ConsultaDinamicaDeEntidade;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtrorigemmatrix.CPMensagemTrOrigemMatrix;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.mensagemtrorigemwhatsapp.CPMensagemTrOrigemWhatsapp;

/**
 *
 * @author salvio
 */
public class GestaoDeHistoricoDeMensagens {

    public MensagemTrOrigemWhatsapp getMensagemEnviadaPorWhatsappByRegistroMatrix(String pCodigoMensagemMatrix) {
        return (MensagemTrOrigemWhatsapp) new ConsultaDinamicaDeEntidade(MensagemTrOrigemWhatsapp.class)
                .addcondicaoCampoIgualA(CPMensagemTrOrigemWhatsapp.codigoreciboentregawhatsapp, pCodigoMensagemMatrix).getPrimeiroRegistro();
    }

    public MensagemTrOrigemMatrix getMensagemEnviadaPorMatrixByRegistroWhatsapp(String pCodigoMensagemMAtrix) {
        return (MensagemTrOrigemMatrix) new ConsultaDinamicaDeEntidade(MensagemTrOrigemMatrix.class)
                .addcondicaoCampoIgualA(CPMensagemTrOrigemMatrix.codigoencaminhamentowhatsapp, pCodigoMensagemMAtrix).getPrimeiroRegistro();
    }

}
