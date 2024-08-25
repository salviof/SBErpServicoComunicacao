/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao.chat.controller;

import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.MapaSistemasConfiaveis;
import br.org.coletivoJava.fw.erp.implementacao.erpintegracao.model.SistemaERPConfiavel;
import com.super_bits.modulosSB.SBCore.ConfigGeral.SBCore;
import com.super_bits.modulosSB.SBCore.modulos.fabrica.ItfFabrica;

/**
 *
 * @author salvio
 */
public enum FabSistemasErp implements ItfFabrica {

    CRM_CASANOVA,
    FATURA_CASANOVA;

    @Override
    public SistemaERPConfiavel getRegistro() {
        SistemaERPConfiavel sistema = new SistemaERPConfiavel();
        switch (this) {

            case CRM_CASANOVA:

                if (SBCore.isEmModoProducao()) {
                    sistema.setDominio("crm.casanovadigital.com.br");

                    sistema.setChavePublica("MIICHzANBgkqhkiG9w0BAQEFAAOCAgwAMIICBwKCAf4AqE2zYXZ8pzNSEjyWcj9UJXe3BQnyr3ru7F5Czm/XsArB3F6IHxuWPiXnUsOssZPO+RyyXIIuJaoQDzWjxJMWbHqKygasYCRI0+yOvNWHBqiyu8KPqwLpTkXjTUyN/w0tQZlW59XD/mk56GK5wRKNJdhguL0QhbHZbE4gDYvVbFPpnrWF0qgqAAAlzrwGSYtqfTgHjBJe5FwlGXNtMhrEERZ7FigBgOcAE42aoZ1vC+YBd9bXADlNfPfAYVrOObGIAxRsUB3ZSPjvsR9VKikw+Q4jfJjZ18W18+bynrpMNbeiafIRPgoTTn4S1yXTIgCJ2E9kvRiK3j21pkJ6ngPEkGg222ODxTT4wHTxcSOW8dGY/PIpWpleNh8Rb2BuaaBKRW4yRylt5ydo+GDTVSxx9NKvFAwtS0XvK+5CSfrdKNsVrAHeuL0xGTs763AhmpcqLnRu4EWiSdWG9vE0mad4HeOreUtmR9osouE+E20P8QFJoAlGQDKQ8/oSADpTt2kB77Wl5KPWSFoP7YhKQEIO462Co/LXo0eK57189ck7OF3hdUfW0/JJSrMFHU87pqE9jvl8vivzORydTqCdgWqArF+vZdf+OjWGhAks/3WeEKtYmXNsrGhObKwCUdcpabR0/vS2wKCzUbq3czvng2+mEYTf8986b/L6ibzZjOUCAwEAAQ==");
                    sistema.setUrlRecepcaoCodigo("https://fatura.casanovadigital.com.br/solicitacaoAuth2Recept/");
                    sistema.setUrlPublicaEndPoint("https://fatura.casanovadigital.com.br/acoesRestful");
                    sistema.setEmailusuarioAdmin("financeiro@casanovadigital.com.br");
                    MapaSistemasConfiaveis.persistirNovoSistema(sistema);
                } else {
                    sistema.setDominio("localhost");

                    sistema.setChavePublica("MIICHzANBgkqhkiG9w0BAQEFAAOCAgwAMIICBwKCAf4AjZr4eWzuNVsheDpB7C3Z8rwlaPJGA5MHtX057VpzpZjzvGsd2i6tmAfGmUQ4Aa1vlqvThgsc31ST55+QOGy1TQkfE3OUirPfLJFhoKeSPN2JgbElVQL6HfSrXuFASApgzMjKR/lPQkbuAFTBD5MbpV6ZKh1Gi/qtG7DVV4fhQzWdqG5OfpvN0VghTksKaDuYioaEeUUytUpGI+dCo4fDKuR2uzN5pFaNoG50lfo/4qHo3Jzqr0Ob5MUAgcC7j4vgofikIAulFzLsiWeXOESD5TpjVfzy4Q59FGD+krNIgqX6hY4HQoqt+4xLjmNn9HhJyM58LSn+tlDaHkg6xdk++Rj/nk0GF9qHkJpoQo5nl3Y6rOfq/qLbPOd7uhdEpRYPzuZsSHj3ym5nwauhJAv3HFJIjGuL41JEML0mGJKMgWF/d2agDE4rKzuZMGolMRE+uCA3jx0RJAQCGm16G+ZTcI5hVAM5IsWJoQ0/rVFfVvBJprvlcPv0PMcqhLD0NuGTyhrL2faIomnn+3/gp+kV/0tnZZJp/dpQTWD3Ev7ux01+RnYrjL5ZHn1RMiPg4J2R9kYcjwaEQmq3bFJ6Q5JFMNkD7GRrma0TTqubOIWad0nCmW2xB0aquZtSamMCZ9PX0Iim958P9WSfxXusobWeEEfJjS/Q42cMT1ZIAAcCAwEAAQ==");
                    sistema.setUrlRecepcaoCodigo("http://localhost:8080/solicitacaoAuth2Recept/");
                    sistema.setUrlPublicaEndPoint("http://localhost:8080/acoesRestful");
                    sistema.setEmailusuarioAdmin("financeiro@casanovadigital.com.br");
                    MapaSistemasConfiaveis.persistirNovoSistema(sistema);
                }
                break;
            case FATURA_CASANOVA:
                if (SBCore.isEmModoProducao()) {
                    sistema.setDominio("fatura.casanovadigital.com.br");

                    sistema.setChavePublica("MIICHzANBgkqhkiG9w0BAQEFAAOCAgwAMIICBwKCAf4AqE2zYXZ8pzNSEjyWcj9UJXe3BQnyr3ru7F5Czm/XsArB3F6IHxuWPiXnUsOssZPO+RyyXIIuJaoQDzWjxJMWbHqKygasYCRI0+yOvNWHBqiyu8KPqwLpTkXjTUyN/w0tQZlW59XD/mk56GK5wRKNJdhguL0QhbHZbE4gDYvVbFPpnrWF0qgqAAAlzrwGSYtqfTgHjBJe5FwlGXNtMhrEERZ7FigBgOcAE42aoZ1vC+YBd9bXADlNfPfAYVrOObGIAxRsUB3ZSPjvsR9VKikw+Q4jfJjZ18W18+bynrpMNbeiafIRPgoTTn4S1yXTIgCJ2E9kvRiK3j21pkJ6ngPEkGg222ODxTT4wHTxcSOW8dGY/PIpWpleNh8Rb2BuaaBKRW4yRylt5ydo+GDTVSxx9NKvFAwtS0XvK+5CSfrdKNsVrAHeuL0xGTs763AhmpcqLnRu4EWiSdWG9vE0mad4HeOreUtmR9osouE+E20P8QFJoAlGQDKQ8/oSADpTt2kB77Wl5KPWSFoP7YhKQEIO462Co/LXo0eK57189ck7OF3hdUfW0/JJSrMFHU87pqE9jvl8vivzORydTqCdgWqArF+vZdf+OjWGhAks/3WeEKtYmXNsrGhObKwCUdcpabR0/vS2wKCzUbq3czvng2+mEYTf8986b/L6ibzZjOUCAwEAAQ==");
                    sistema.setUrlRecepcaoCodigo("https://fatura.casanovadigital.com.br/solicitacaoAuth2Recept/");
                    sistema.setUrlPublicaEndPoint("https://fatura.casanovadigital.com.br/acoesRestful");
                    sistema.setEmailusuarioAdmin("financeiro@casanovadigital.com.br");
                    MapaSistemasConfiaveis.persistirNovoSistema(sistema);
                } else {
                    sistema.setDominio("localhost");

                    sistema.setChavePublica("MIICHzANBgkqhkiG9w0BAQEFAAOCAgwAMIICBwKCAf4AjZr4eWzuNVsheDpB7C3Z8rwlaPJGA5MHtX057VpzpZjzvGsd2i6tmAfGmUQ4Aa1vlqvThgsc31ST55+QOGy1TQkfE3OUirPfLJFhoKeSPN2JgbElVQL6HfSrXuFASApgzMjKR/lPQkbuAFTBD5MbpV6ZKh1Gi/qtG7DVV4fhQzWdqG5OfpvN0VghTksKaDuYioaEeUUytUpGI+dCo4fDKuR2uzN5pFaNoG50lfo/4qHo3Jzqr0Ob5MUAgcC7j4vgofikIAulFzLsiWeXOESD5TpjVfzy4Q59FGD+krNIgqX6hY4HQoqt+4xLjmNn9HhJyM58LSn+tlDaHkg6xdk++Rj/nk0GF9qHkJpoQo5nl3Y6rOfq/qLbPOd7uhdEpRYPzuZsSHj3ym5nwauhJAv3HFJIjGuL41JEML0mGJKMgWF/d2agDE4rKzuZMGolMRE+uCA3jx0RJAQCGm16G+ZTcI5hVAM5IsWJoQ0/rVFfVvBJprvlcPv0PMcqhLD0NuGTyhrL2faIomnn+3/gp+kV/0tnZZJp/dpQTWD3Ev7ux01+RnYrjL5ZHn1RMiPg4J2R9kYcjwaEQmq3bFJ6Q5JFMNkD7GRrma0TTqubOIWad0nCmW2xB0aquZtSamMCZ9PX0Iim958P9WSfxXusobWeEEfJjS/Q42cMT1ZIAAcCAwEAAQ==");
                    sistema.setUrlRecepcaoCodigo("http://localhost:8080/solicitacaoAuth2Recept/");
                    sistema.setUrlPublicaEndPoint("http://localhost:8080/acoesRestful");
                    sistema.setEmailusuarioAdmin("financeiro@casanovadigital.com.br");
                    MapaSistemasConfiaveis.persistirNovoSistema(sistema);
                }
                break;
            default:
                throw new AssertionError();
        }

        return sistema;
    }
}
