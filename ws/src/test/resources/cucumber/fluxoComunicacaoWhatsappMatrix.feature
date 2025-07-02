# language: pt
@FluxoOauthInteracaoSistema
Funcionalidade: Testar o encaminhando de mensagens do Matrix para Whatsapp

Contexto: O contato envia uma mensagem para o atendente, e o atendente confirma a leitura, e envia uma confirmação de mensagem para o whatapp

  Cenario: Sistema cliente lista os usuários em servidor apos se autenticar via oauth
    Dado um usuario logado no sitema com chave de acesso configuradas entre cliente e servidor
    Quando o sistema solicita um código de concessão no escopo do sistema
    Então o servidor valida as chaves de acesso assincronas e reconhece o nome de usuário
    E o proprio servidor acessa a url enviando o código de concessao que por sua vez solicita o token
    Quando o sistema solicita a listagem de usuario com um token valido
    Entao o servidor entrega uma lista em Json com os dados de usuário
