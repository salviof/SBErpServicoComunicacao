/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller.contatos;

import br.com.casanovadigital.servicos.notificacao.chat.controller.FabSistemasErp;
import br.org.coletivoJava.fw.api.erp.erpintegracao.contextos.ERPIntegracaoSistemasApi;
import br.org.coletivoJava.fw.api.erp.erpintegracao.servico.ItfIntegracaoERP;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.SistemaERPConfiavel;
import br.org.coletivoJava.fw.ws.restFull.ErroConexaoSistemaTerceiro;
import br.org.coletivoJava.fw.ws.restFull.ErroParamentosInvalidos;
import br.org.coletivoJava.fw.ws.restFull.ErroRecursoNaoEncontrado;
import br.org.coletivoJava.fw.ws.restFull.RotaSparkPadrao;
import com.super_bits.modulosSB.SBCore.modulos.TratamentoDeErros.ErroRegraDeNegocio;
import jakarta.json.JsonObject;
import java.util.Map;

/**
 *
 * @author salvio
 */
public class ApiContatosReceberContato extends RotaSparkPadrao {

    private String urlCRM;
    private String jsessionID;
    private String roomID;

    @Override
    public void validarParamentros() throws ErroParamentosInvalidos {
        Map<String, String> cookies = requisicao.cookies();

    }

    @Override
    public String executarRegraDeNegocio() throws ErroRegraDeNegocio, ErroRecursoNaoEncontrado, ErroConexaoSistemaTerceiro {

        return "em desenvolvimento";

    }

    protected void salvarContato(JsonObject pDados) {
        ItfIntegracaoERP erpCRM = ERPIntegracaoSistemasApi.RESTFUL.getImplementacaoDoContexto();
        SistemaERPConfiavel sistemaCRM = FabSistemasErp.CRM_CASANOVA.getRegistro();
        erpCRM.gerarTokenSistemaComoAdmin(sistemaCRM);

    }

}
