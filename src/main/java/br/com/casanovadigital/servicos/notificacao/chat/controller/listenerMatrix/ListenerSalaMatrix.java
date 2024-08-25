package br.com.casanovadigital.servicos.notificacao.chat.controller.listenerMatrix;

import br.com.casanovadigital.servicos.notificacao.chat.controller.MapAtendentesMatrixCAsanovadigital;
import br.com.casanovadigital.servicos.notificacao.chat.controller.UtilAgenciaContatos;
import br.com.casanovadigital.servicos.notificacao.chat.controller.gestaoMensagens.MotorControleDeMensagens;
import br.com.casanovadigital.servicos.notificacao.contextoChat.matrix.ContextoMatrix;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfChatSalaBean;
import br.org.coletivoJava.fw.api.erp.chat.model.ItfUsuarioChat;
import br.org.coletivoJava.fw.erp.implementacao.chat.ChatMatrixOrgimpl;
import br.org.coletivoJava.fw.erp.implementacao.chat.model.model.EscutaSalaMatrixAbst;
import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringValidador;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;

/**
 *
 * @author salvio
 */
public class ListenerSalaMatrix extends EscutaSalaMatrixAbst {

    public ListenerSalaMatrix(ItfChatSalaBean pSala) {
        super(pSala);

        String codigoEntrada = MapAtendentesMatrixCAsanovadigital.getPontodeEntradaByApelidoUnicoSala(pSala.getApelido());
        if (codigoEntrada == null) {
            System.out.println("ATENÇÃO SLUG DE SALA NÃO ENCONTRADO" + pSala.getNome());
        }

    }

    @Override
    public void eventoMensagem(RoomEvent pEvento) {
        try {
            String enviadopor = pEvento.getSender();
            String usuarioAdmin = SBCore.getConfigModulo(FabConfigApiMatrixChat.class).getPropriedade(FabConfigApiMatrixChat.USUARIO_ADMIN);
            if (pEvento.getSender().toLowerCase().contains(usuarioAdmin.toLowerCase())) {
                return;
            }
            ItfUsuarioChat usuario = ContextoMatrix.getChatMatrixService().getUsuarioByCodigo(pEvento.getSender());

            if (!UtilSBCoreStringValidador.isNuloOuEmbranco(usuario.getEmail())) {
                System.out.println("Evento é do tipo mensagem, iniciando tratamento");
                System.out.println("Evento é do tipo mensagem, iniciando tratamento, evento Matrix enviado por" + enviadopor);
                MotorControleDeMensagens.executarEventoSalaMatrix(pEvento);
            }

        } catch (Throwable t) {
            System.out.println("Falha processando evento de mensagem" + pEvento.getSender());
        }

    }

    @Override
    public void eventoLeitura(RoomEvent pEvento) {
        MotorControleDeMensagens.executarEventoSalaMatrix(pEvento);
    }

    @Override
    public void eventoDigitando(RoomEvent pEvento) {
        System.out.println("Evento digitalndo, nada a fazer por enquanto");
        MotorControleDeMensagens.executarEventoSalaMatrix(pEvento);
    }

}
