package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroCriandoContato;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroRegraDeNEgocioChat;
import br.org.coletivoJava.fw.api.erp.chat.model.ComoUsuarioChat;
import br.org.coletivoJava.integracoes.matrixChat.config.FabConfigApiMatrixChat;
import com.super_bits.modulos.SBAcessosModel.model.GrupoUsuarioSB;
import com.super_bits.modulos.SBAcessosModel.model.UsuarioSB;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilCRCStringBuscaTrecho;
import com.super_bits.modulosSB.SBCore.modulos.Controller.ConfigPermissaoSBCoreAbstrato;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.acoes.ComoAcaoDoSistema;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.permissoes.ErroDadosDeContatoUsuarioNaoEncontrado;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.permissoes.ItfPermissao;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.permissoes.token.ComoTokenAcessoBasico;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.permissoes.token.ItfTokenAcessoDinamico;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.permissoes.token.ItfTokenRecuperacaoEmail;
import com.super_bits.modulosSB.SBCore.modulos.erp.FabTipoAgenteOrganizacao;
import com.super_bits.modulosSB.SBCore.modulos.fabrica.ComoFabricaAcoes;
import com.super_bits.modulosSB.SBCore.modulos.objetos.entidade.basico.ComoEntidadeSimplesSomenteLeitura;
import com.super_bits.modulosSB.SBCore.modulos.objetos.entidade.basico.ComoGrupoUsuario;
import com.super_bits.modulosSB.SBCore.modulos.objetos.entidade.basico.ComoUsuario;

import java.util.ArrayList;
import java.util.List;
import org.coletivojava.fw.api.objetoNativo.view.menu.MenuSBFW;
import org.coletivojava.fw.api.objetoNativo.view.menu.MenusDaSessao;
import com.super_bits.modulosSB.SBCore.modulos.erp.ItfSistemaERP;
import com.super_bits.modulosSB.SBCore.modulos.objetos.entidade.contato.ComoContatoHumano;
import com.super_bits.modulosSB.SBCore.modulos.view.menu.ComoMenusDeSessao;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author salvio
 */
public class ServicoDeUsuariosEPermicoesComunicacao extends ConfigPermissaoSBCoreAbstrato {

    private static GrupoUsuarioSB grupoUsuarioPadrao = new GrupoUsuarioSB();

    public ServicoDeUsuariosEPermicoesComunicacao() {
        super(new Class[]{});
        grupoUsuarioPadrao.setId((long) ("Grupo Usuarios Remotos Credenciados".hashCode()));
        grupoUsuarioPadrao.setNome("Grupo Usuarios Remotos Credenciados");
    }

    @Override
    public List<ItfPermissao> configuraPermissoes() {
        return new ArrayList<>();
    }

    private ComoUsuario buildUsuario(ItfSistemaERP pSistema) {
        if (pSistema.getEmailusuarioAdmin() == null) {
            return null;
        }
        UsuarioSB usuarioAdmin = new UsuarioSB();
        usuarioAdmin.setEmail(pSistema.getEmailusuarioAdmin());
        usuarioAdmin.setNome(UtilCRCStringBuscaTrecho.getStringAteEncontrarIsto(pSistema.getEmailusuarioAdmin(), "@"));
        usuarioAdmin.setApelido(UtilCRCStringBuscaTrecho.getStringAteEncontrarIsto(pSistema.getEmailusuarioAdmin(), "@"));
        // usuarioAdmin.setNomeLongo(UtilCRCStringBuscaTrecho.getStringAteEncontrarIsto(pSistema.getEmailusuarioAdmin(), "@"));
        usuarioAdmin.setGrupo(grupoUsuarioPadrao);
        usuarioAdmin.setId((long) pSistema.getEmailusuarioAdmin().hashCode());
        return usuarioAdmin;

    }

    @Override
    public ComoUsuario getUsuarioByEmail(String pEmail) {
        return super.getUsuarioByEmail(pEmail); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public List<ComoUsuario> configuraUsuarios() {
        List<ComoUsuario> usuarios = new ArrayList<>();
        List<ItfSistemaERP> sistemas;
        try {
            sistemas = AplicacaoWsChat.getCentralLogicaProcesasmento().getSistemas();
            for (ItfSistemaERP sistema : sistemas) {
                ComoUsuario usuario = buildUsuario(sistema);
                if (usuario != null) {
                    usuarios.add(usuario);
                }
            }
        } catch (ErroConexaoServicoChat ex) {
            Logger.getLogger(ServicoDeUsuariosEPermicoesComunicacao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return usuarios;
    }

    @Override
    public ComoMenusDeSessao definirMenu(ComoGrupoUsuario pGrupo) {
        return new MenusDaSessao(new MenuSBFW());
    }

    @Override
    public void atualizarInformacoesDePermissoesDoSistema() {

    }

    @Override
    public boolean isAcaoPermitidaUsuarioLogado(ComoAcaoDoSistema acao) {
        return true;
    }

    @Override
    public boolean isAcaoPermitidaUsuario(ComoUsuario pUsuario, ComoAcaoDoSistema acao) {
        return true;
    }

    @Override
    public boolean isPermitidoUsuario(ComoUsuario pUsuario, ItfPermissao pPermissao) {
        return true;
    }

    @Override
    public ItfTokenRecuperacaoEmail gerarTokenRecuperacaoDeSenha(ComoUsuario pUsuario, int pMinutosValidade) {
        return null;
    }

    @Override
    public ItfTokenAcessoDinamico gerarTokenDinamico(ComoFabricaAcoes pAcao, ComoEntidadeSimplesSomenteLeitura pItem, String pEmail) {
        return null;
    }

    @Override
    public boolean isTokenDinamicoExiste(ComoFabricaAcoes pAcao, ComoEntidadeSimplesSomenteLeitura pItem, String pEmail) {
        return false;
    }

    @Override
    public void persitirMergePermissoes() {

    }

    @Override
    public FabTipoAgenteOrganizacao getTipoAgente(ComoUsuario pUsuario) {
        if (pUsuario.getEmail() != null && pUsuario.getEmail().contains(FabConfigApiMatrixChat.DOMINIO_FEDERADO.getValorParametroSistema())) {
            return FabTipoAgenteOrganizacao.ATENDIMENTO;
        }
        if (pUsuario.getTelefone() != null && !pUsuario.getTelefone().isEmpty()) {
            return FabTipoAgenteOrganizacao.CLIENTE;
        }
        return FabTipoAgenteOrganizacao.ATENDIMENTO;
    }

    @Override
    public ComoContatoHumano getContatoDoUsuario(ComoUsuario pUsuairo) throws ErroDadosDeContatoUsuarioNaoEncontrado {
        if (pUsuairo instanceof ComoUsuarioChat) {
            throw new ErroDadosDeContatoUsuarioNaoEncontrado("O tipo de contato não é compativel " + ComoUsuarioChat.class.getSimpleName());
        }
        switch (getTipoAgente(pUsuairo).getTipoCanal()) {
            case INTERNO:
                return (ComoContatoHumano) AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getAtendente((ComoUsuarioChat) pUsuairo);

            case REDES_SOCIAIS: {
                try {
                    return (ComoContatoHumano) AplicacaoWsChat.REPOSITORIO_COMUNICACAO_CHAT.getContato((ComoUsuarioChat) pUsuairo);
                } catch (ErroConexaoServicoChat | ErroRegraDeNEgocioChat | ErroCriandoContato ex) {
                    throw new ErroDadosDeContatoUsuarioNaoEncontrado(ex.getMessage());
                }
            }

            default:
                throw new AssertionError();
        }
    }

    @Override
    public ComoTokenAcessoBasico getTokenAcessoEntreSistemas(String token) {
        return null;
    }

}
