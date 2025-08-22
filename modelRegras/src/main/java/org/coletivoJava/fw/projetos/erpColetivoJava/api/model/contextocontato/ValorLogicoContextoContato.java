package org.coletivoJava.fw.projetos.erpColetivoJava.api.model.contextocontato;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import org.coletivoJava.fw.projetos.erpColetivoJava.api.model.contextocontato.ValoresLogicosContextoContato;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.ContextoContato;
import com.super_bits.modulosSB.SBCore.modulos.objetos.InfoCampos.anotacoes.InfoReferenciaEntidade;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@InfoReferenciaEntidade(tipoObjeto = ContextoContato.class)
public @interface ValorLogicoContextoContato {

	ValoresLogicosContextoContato calculo();
}