/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.casanovadigital.servicos.notificacao;

/**
 *
 * @author salvio
 */
public class TesteIntegracaoRecepcaoMensagemMatrix {

    private final String mensagemSimples = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Dani Terra\"},\"wa_id\":\"553197140804\"}],\"messages\":[{\"from\":\"553197140804\",\"id\":\"wamid.HBgMNTUzMTk3MTQwODA0FQIAEhgWM0VCMDEzRkNDRDQ5REJBODcxREJBNQA=\",\"timestamp\":\"1691936788\",\"text\":{\"body\":\"Testando, testando, 1, 2, 3\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";

    private final String mensagemSimples2 = " {\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"contacts\":[{\"profile\":{\"name\":\"Salvio Furbino\"},\"wa_id\":\"553184178550\"}],\"messages\":[{\"from\":\"553184178550\",\"id\":\"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhgWM0VCMDNCOUM5REZCN0I0RjY3Nzg1RgA=\",\"timestamp\":\"1691003496\",\"text\":{\"body\":\"Oi uma mensagem simples com icone \\ud83e\\udd18\"},\"type\":\"text\"}]},\"field\":\"messages\"}]}]}";

    private final String mensagemFalha24HorasSemContato = "{\"object\":\"whatsapp_business_account\",\"entry\":[{\"id\":\"114354588403482\",\"changes\":[{\"value\":{\"messaging_product\":\"whatsapp\",\"metadata\":{\"display_phone_number\":\"553121159755\",\"phone_number_id\":\"103007756220088\"},\"statuses\":[{\"id\":\"wamid.HBgMNTUzMTk1MTcxNjA1FQIAERgSODhDRkZDQTVBOUExNTg1Qjc4AA==\",\"status\":\"failed\",\"timestamp\":\"1694545560\",\"recipient_id\":\"553195171605\",\"errors\":[{\"code\":131047,\"title\":\"Re-engagement message\",\"message\":\"Re-engagement message\",\"error_data\":{\"details\":\"Message failed to send because more than 24 hours have passed since the customer last replied to this number.\"},\"href\":\"https:\\/\\/developers.facebook.com\\/docs\\/whatsapp\\/cloud-api\\/support\\/error-codes\\/\"}]}]},\"field\":\"messages\"}]}]}";

//    @Test
    public void testeRecepcaoMensagem() {
        System.out.println("main");
        System.out.println("");
    }
}
