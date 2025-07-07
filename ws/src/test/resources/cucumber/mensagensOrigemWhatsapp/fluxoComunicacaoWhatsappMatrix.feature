# language: pt
@FluxoMensagemOrigemWhatsapp
Funcionalidade: Encaminhamento de mensagem do WhatsApp para Matrix e confirmação de leitura

  Como um sistema de integração entre WhatsApp e Matrix
  Quero que uma mensagem enviada por um usuário Contato no WhatsApp seja encaminhada para um usuário Atendimento no Matrix
  E que o usuário Contato seja notificado quando o usuário Atendimento ler a mensagem

  Cenário: Contato envia mensagem pelo WhatsApp, Atendimento recebe e lê, e Contato é notificado
    Dado que o usuário Contato está conectado no WhatsApp
    E o usuário Atendimento está conectado no Matrix
    Quando o usuário Contato envia a mensagem Olá, tudo bem pelo WhatsApp para Atendimento
    Então a mensagem Olá, tudo bem é encaminhada para o usuário Atendimento no Matrix
    Quando o usuário Atendimento lê a mensagem Olá, tudo bem no Matrix
    Então o usuário Contato é notificado da leitura da mensagem no WhatsApp
