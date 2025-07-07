#language: pt
@FluxoMensagemOrigemMAtrix
Funcionalidade: Testar o encaminhando de mensagens do Matrix para Whatsapp

Contexto: O contato envia uma mensagem para o atendente, e o atendente confirma a leitura, e envia uma confirmação de mensagem para o whatapp

Cenario: Sistema cliente lista os usuários em servidor apos se autenticar via oauth
Dado uma mensagem recebida no servidor de escuta do whatsapp
Quando a mensagem é encaminhada para o matrix
E lida pelo usuário
Então uma confirmação de leitura é enviada para o Whatsapp
