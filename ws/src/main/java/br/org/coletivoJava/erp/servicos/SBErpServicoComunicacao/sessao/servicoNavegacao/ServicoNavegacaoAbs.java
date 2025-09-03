package br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.sessao.servicoNavegacao;

import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfServicoNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.interfaces.ItfTrilhaNavegacao;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.modelDTO.whatsapp.EntradaNumeroWhatsapp;
import br.org.coletivoJava.erp.servicos.SBErpServicoComunicacao.interpretadormsg.tratamentoErro.ErroFalhaEncaminhando;
import com.super_bits.casanovadigital.servicos.messagens.model.agente.Contato;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.Path;

/**
 *
 * @author salvio
 */
public abstract class ServicoNavegacaoAbs implements ItfServicoNavegacao {

    private final EntradaNumeroWhatsapp entradaWhatsapp;
    private final Class<? extends ItfTrilhaNavegacao> trilhaRaiz;
    private final Class<? extends ItfTrilhaNavegacao>[] trilhasDiponiveis;
    private final Map<String, Class<? extends ItfTrilhaNavegacao>> rotaClasse = new HashMap<>();

    public ServicoNavegacaoAbs(EntradaNumeroWhatsapp pEntrada, Class<? extends ItfTrilhaNavegacao> pTrilhaRaiz, Class<? extends ItfTrilhaNavegacao>[] pTrilhas) {
        entradaWhatsapp = pEntrada;
        trilhaRaiz = pTrilhaRaiz;
        trilhasDiponiveis = pTrilhas;

    }

    private boolean caminhoCompativel(String pCaminho, String pIdentificador) {
        if (pCaminho == null) {
            return false;
        }
        if (pIdentificador.contains("^")) {
            return pCaminho.matches(pIdentificador);

        } else {
            return (pCaminho.equals(pIdentificador));
        }

    }

    @Override
    public void validarServicoNavegacao() throws ErroFalhaEncaminhando {
        for (Class<? extends ItfTrilhaNavegacao> trilha : trilhasDiponiveis) {
            Path anCaminhoRota = trilha.getAnnotation(Path.class);
            String caminhoRota = anCaminhoRota.value();
            rotaClasse.put(caminhoRota, trilha);
        }
        if (trilhaRaiz == null) {
            throw new ErroFalhaEncaminhando("a rota raiz do serviço de navegação não foi definida");
        }
    }

    @Override
    public Class<? extends ItfTrilhaNavegacao> getClasseTrilhaDeNavegacao(Contato pContato, String pCaminho) {
        System.out.println("Pesquisando trilha" + pCaminho);
        System.out.println(pContato);
        if (pCaminho == null) {
            return trilhaRaiz;
        }
        Optional<String> rotaEncontrada = rotaClasse.keySet().stream().filter(rt -> caminhoCompativel(pCaminho, rt)).findFirst();
        if (rotaEncontrada.isPresent()) {
            System.out.println("Rota encontrada");
            return rotaClasse.get(rotaEncontrada.get());
        }
        System.out.println("REtornando rota RAIZ");
        return trilhaRaiz;
    }

    public EntradaNumeroWhatsapp getEntradaWhatsapp() {
        return entradaWhatsapp;
    }

    @Override
    public boolean isRotaExiste(String pRota) {
        Optional<String> rotaEncontrada = rotaClasse.keySet().stream().filter(rt -> caminhoCompativel(pRota, rt)).findFirst();
        return rotaEncontrada.isPresent();
    }

    @Override
    public String getCaminhoTrilhaRaiz() {
        return "menu";
    }

    @Override
    public List<String> getPalavrasParaCaminhoTrilhaRaiz() {
        List<String> palavras = new ArrayList<>();
        palavras.add("menu");
        palavras.add("voltar");
        return palavras;
    }

}
