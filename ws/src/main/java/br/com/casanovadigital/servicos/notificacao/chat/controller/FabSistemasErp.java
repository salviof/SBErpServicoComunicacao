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

                    sistema.setChavePublica("MIICHzANBgkqhkiG9w0BAQEFAAOCAgwAMIICBwKCAf4AuzIbIt/7LKAe0SaJFQL4najEiw6dz253pMNyBk8iUij/v3aiwN9Es6Pq50ypHIO3zynEjDwpg7stDnY4n2bCbH51tVmsphDCW0owt9rXCbpJexCuygr00oLAa0Us6V55t8bJX0V+ty7Pz1VmFpyEihpipHLiEuoTXhJdZwGoRAqXS4j6WigY4iPSjKMuUYCQZgJYZf0ewWOEpNp9A4nKy0jx/p4Z05tI463pM6JKVV83LlPexV6JtmTdkyjb17uXqjoNuClfRrUDmY5LJvqyYnABAQ+zx/k1KukdKEB2fjiq57H9sceJ+hqHISpXEwhp3+Oc5GX4eVXMLl94zwn8bmDNQHhD5xUx9BE8WTYunjXQakbiDF2XA7H9n/0uKTIXn1O0cXcwIzjizO6Ks5eNOsPcG5XN85P4xDtrgd+Gl5seds6OO2jMkh5XfjKMXVpz1iNNwl2FolbQ0dEv1ZKm+fRuv6u2KbJqPQ+GwfqxOg6mUgAnFEypu7gszCazaHfBRH6CmSSonVcafQE4Icg1ddAfBVuekNd3J9JTAkDJZkn0+qFe0lTRhmw2FxI6alO+i6kz3PsuTPk35FxJB+6PpUly6xFUK7a9KojfNO28XVqbZ++lT1fEFMRg2YS+qG9hR+Mngxcoz3oCsRPQ8lwUUn+P3jaRW0InLqxDu4ECAwEAAQ==");
                    sistema.setUrlRecepcaoCodigo("http://localhost:8080/solicitacaoAuth2Recept/");
                    sistema.setUrlPublicaEndPoint("http://localhost:8080/acoesRestful");
                    sistema.setEmailusuarioAdmin("financeiro@casanovadigital.com.br");
                    MapaSistemasConfiaveis.persistirNovoSistema(sistema);
                }
                break;
            default:
                throw new AssertionError();
        }
        sistema.setId((long) sistema.getHashChavePublica().hashCode());
        return sistema;
    }
}
