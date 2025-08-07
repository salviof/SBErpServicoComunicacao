package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.config;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.AplicacaoWsChat;
import br.org.coletivoJava.fw.api.erp.chat.ErroConexaoServicoChat;
import com.super_bits.modulos.SBAcessosModel.model.GrupoUsuarioSB;
import com.super_bits.modulos.SBAcessosModel.model.UsuarioSB;
import com.super_bits.modulosSB.SBCore.UtilGeral.UtilSBCoreStringBuscaTrecho;
import com.super_bits.modulosSB.SBCore.modulos.Controller.ConfigPermissaoSBCoreAbstrato;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.acoes.ItfAcaoDoSistema;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.permissoes.ItfPermissao;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.permissoes.token.ItfTokenAcessoDinamico;
import com.super_bits.modulosSB.SBCore.modulos.Controller.Interfaces.permissoes.token.ItfTokenRecuperacaoEmail;
import com.super_bits.modulosSB.SBCore.modulos.fabrica.ItfFabricaAcoes;
import com.super_bits.modulosSB.SBCore.modulos.objetos.registro.Interfaces.basico.ItfBeanSimplesSomenteLeitura;
import com.super_bits.modulosSB.SBCore.modulos.objetos.registro.Interfaces.basico.ItfGrupoUsuario;
import com.super_bits.modulosSB.SBCore.modulos.objetos.registro.Interfaces.basico.ItfUsuario;
import com.super_bits.modulosSB.SBCore.modulos.view.menu.ItfMenusDeSessao;
import java.util.ArrayList;
import java.util.List;
import org.coletivojava.fw.api.objetoNativo.view.menu.MenuSBFW;
import org.coletivojava.fw.api.objetoNativo.view.menu.MenusDaSessao;
import com.super_bits.modulosSB.SBCore.modulos.erp.ItfSistemaERP;
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

    private ItfUsuario buildUsuario(ItfSistemaERP pSistema) {
        if (pSistema.getEmailusuarioAdmin() == null) {
            return null;
        }
        UsuarioSB usuarioAdmin = new UsuarioSB();
        usuarioAdmin.setEmail(pSistema.getEmailusuarioAdmin());
        usuarioAdmin.setNome(UtilSBCoreStringBuscaTrecho.getStringAteEncontrarIsto(pSistema.getEmailusuarioAdmin(), "@"));
        usuarioAdmin.setApelido(UtilSBCoreStringBuscaTrecho.getStringAteEncontrarIsto(pSistema.getEmailusuarioAdmin(), "@"));
        // usuarioAdmin.setNomeLongo(UtilSBCoreStringBuscaTrecho.getStringAteEncontrarIsto(pSistema.getEmailusuarioAdmin(), "@"));
        usuarioAdmin.setGrupo(grupoUsuarioPadrao);
        usuarioAdmin.setId((long) pSistema.getEmailusuarioAdmin().hashCode());
        return usuarioAdmin;

    }

    @Override
    public ItfUsuario getUsuarioByEmail(String pEmail) {
        return super.getUsuarioByEmail(pEmail); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public List<ItfUsuario> configuraUsuarios() {
        List<ItfUsuario> usuarios = new ArrayList<>();
        List<ItfSistemaERP> sistemas;
        try {
            sistemas = AplicacaoWsChat.getCentralLogicaProcesasmento().getSistemas();
            for (ItfSistemaERP sistema : sistemas) {
                ItfUsuario usuario = buildUsuario(sistema);
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
    public ItfMenusDeSessao definirMenu(ItfGrupoUsuario pGrupo) {
        return new MenusDaSessao(new MenuSBFW());
    }

    @Override
    public void atualizarInformacoesDePermissoesDoSistema() {

    }

    @Override
    public boolean isAcaoPermitidaUsuarioLogado(ItfAcaoDoSistema acao) {
        return true;
    }

    @Override
    public boolean isAcaoPermitidaUsuario(ItfUsuario pUsuario, ItfAcaoDoSistema acao) {
        return true;
    }

    @Override
    public boolean isPermitidoUsuario(ItfUsuario pUsuario, ItfPermissao pPermissao) {
        return true;
    }

    @Override
    public ItfTokenRecuperacaoEmail gerarTokenRecuperacaoDeSenha(ItfUsuario pUsuario, int pMinutosValidade) {
        return null;
    }

    @Override
    public ItfTokenAcessoDinamico gerarTokenDinamico(ItfFabricaAcoes pAcao, ItfBeanSimplesSomenteLeitura pItem, String pEmail) {
        return null;
    }

    @Override
    public boolean isTokenDinamicoExiste(ItfFabricaAcoes pAcao, ItfBeanSimplesSomenteLeitura pItem, String pEmail) {
        return false;
    }

    @Override
    public void persitirMergePermissoes() {

    }

}
