package com.super_bits.casanovadigital.servicos.messagens.model.mensagem;

import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoCampo;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoObjetoSB;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.campo.FabTipoAtributoObjeto;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 *
 * @author salvio
 */
@Entity
@InfoObjetoSB(tags = "Mensagem pelo Matrix", plural = "Mensagens disparadas pelo Matrix")
public class MensagemTrOrigemMatrix extends MensagemTransito {

    public MensagemTrOrigemMatrix() {
        setTipoOrigem(FabTipoOrigem.MATRIX);
    }

    @Column(length = 5000)
    @InfoCampo(tipo = FabTipoAtributoObjeto.TEXTO_SIMPLES)
    private String jsonMensagemOriginal;

    @InfoCampo(tipo = FabTipoAtributoObjeto.OBJETO_DE_UMA_LISTA)
    @OneToMany(mappedBy = "mensagem")
    private List<EncaminhamentoMatrixParaWtzp> encaminhamentos;

    @InfoCampo(tipo = FabTipoAtributoObjeto.TEXTO_SIMPLES)
    private String codigoReciboMensagemMatrix;

    public String getCodigoReciboMensagemMatrix() {
        return codigoReciboMensagemMatrix;
    }

    public void setCodigoReciboMensagemMatrix(String codigoReciboMensagemMatrix) {
        this.codigoReciboMensagemMatrix = codigoReciboMensagemMatrix;
    }

    public String getJsonMensagemOriginal() {
        return jsonMensagemOriginal;
    }

    public void setJsonMensagemOriginal(String jsonMensagemOriginal) {
        this.jsonMensagemOriginal = jsonMensagemOriginal;
    }

    public List<EncaminhamentoMatrixParaWtzp> getEncaminhamentos() {
        return encaminhamentos;
    }

    public void setEncaminhamentos(List<EncaminhamentoMatrixParaWtzp> encaminhamentos) {
        this.encaminhamentos = encaminhamentos;
    }

}
