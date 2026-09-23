package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.salas;

import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoChatSalaBean;

/**
 * Trabalho de criação/abertura de uma sala, para ser executado fora da thread
 * que atende a requisição.
 *
 * Não existe um {@link java.util.concurrent.Callable} equivalente porque
 * {@link ErroConexaoServicoChat} estende Throwable, e não Exception.
 *
 * ATENÇÃO: a implementação roda em outra thread, então não pode encostar no
 * EntityManager da requisição. O que precisar de banco deve abrir o seu próprio
 * (é o que os métodos do repositório já fazem).
 *
 * @author salvio
 */
@FunctionalInterface
public interface CriadorDeSala {

    ComoChatSalaBean criar() throws ErroConexaoServicoChat;

}
