package org.coletivoJava.fw.projetos.erpColetivoJava.implemetation.model.contextocontato;

import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import com.super_bits.modulosSB.SBCore.modulos.objetos.calculos.ValorLogicoCalculoGenerico;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.contextocontato.ValorLogicoContextoContato;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.contextocontato.ValoresLogicosContextoContato;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.campoInstanciado.ItfCampoInstanciado;

@ValorLogicoContextoContato(calculo = ValoresLogicosContextoContato.NOMECONTEXTO)
public class ValorLogicoContextoContatoNomeContexto
        extends
        ValorLogicoCalculoGenerico {

    public ValorLogicoContextoContatoNomeContexto(ItfCampoInstanciado pCampo) {
        super(pCampo);
    }

    @Override
    public Object getValor(Object... pEntidade) {
        if (getContexto().getContato() != null) {
            String nome = "Ctx:" + getContexto().getCodigoEntrada()
                    + getContexto().getContato().getWaid();
            getContexto().setNome(nome);
        } else {
            getContexto().setNome("Contexto temnporário");
        }
        return getContexto().getNome();
    }

    public ContextoContato getContexto() {
        return (ContextoContato) getCampoInst().getObjetoRaizDoAtributo();
    }
}
