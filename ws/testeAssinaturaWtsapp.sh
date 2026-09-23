#!/usr/bin/env bash
#
# Teste do webhook do WhatsApp com payload real capturado do log.
# Espera HTTP 200 e corpo "OK".
#
# Uso:
#   ./testar-webhook-whatsapp.sh
#   ./testar-webhook-whatsapp.sh http://localhost:8667
#   ./testar-webhook-whatsapp.sh http://localhost:8667 /caminho/outro-payload.json
#
set -euo pipefail

SERVIDOR="${1:-http://localhost:8667}"
ENDPOINT="${SERVIDOR%/}/api/v1/whatsapp/recepcao/notificacao"
PAYLOAD_EXTERNO="${2:-}"

PROP="/home/superBits/desenvolvedor/configModuloTestes/agenciaForms/modulos/ApiWhatsapp/ApiWhatsapp.prop"
CHAVE="CHAVE_APP_ASSINATURA_MSG"

# Payload real capturado do log da aplicacao.
# Os acentos vem escapados (\u00e1), entao o arquivo e ASCII puro.
PAYLOAD_PADRAO='{"object":"whatsapp_business_account","entry":[{"id":"114354588403482","changes":[{"value":{"messaging_product":"whatsapp","metadata":{"display_phone_number":"553121159755","phone_number_id":"103007756220088"},"contacts":[{"profile":{"name":"S\u00e1lvio Furbino"},"wa_id":"553184178550","user_id":"BR.4416800325220367"}],"messages":[{"from":"553184178550","from_user_id":"BR.4416800325220367","id":"wamid.HBgMNTUzMTg0MTc4NTUwFQIAEhgWM0VCMDM0RkZGMDVGODdEMkY1NzA5NgA=","timestamp":"1788875624","text":{"body":"testeeee"},"type":"text"}]},"field":"messages"}]}]}'

PAYLOAD="$(mktemp /tmp/payload-wtzp-XXXXXX.json)"
RESPOSTA="$(mktemp /tmp/resposta-wtzp-XXXXXX)"
trap 'rm -f "$PAYLOAD" "$RESPOSTA"' EXIT

# ---------------------------------------------------------------- 1. segredo

if [[ ! -r "$PROP" ]]; then
    echo "ERRO: arquivo de configuracao nao encontrado ou sem permissao de leitura:" >&2
    echo "      $PROP" >&2
    exit 1
fi

APP_SECRET="$(grep "^${CHAVE}=" "$PROP" | head -n1 | cut -d= -f2- | tr -d ' \r')"

if [[ -z "$APP_SECRET" ]]; then
    echo "ERRO: chave ${CHAVE} nao encontrada (ou vazia) em ${PROP}" >&2
    exit 1
fi

echo "App Secret carregado: ${#APP_SECRET} caracteres"

# ---------------------------------------------------------------- 2. payload

if [[ -n "$PAYLOAD_EXTERNO" ]]; then
    if [[ ! -r "$PAYLOAD_EXTERNO" ]]; then
        echo "ERRO: payload informado nao pode ser lido: $PAYLOAD_EXTERNO" >&2
        exit 1
    fi
    cat "$PAYLOAD_EXTERNO" > "$PAYLOAD"
    echo "Payload: $PAYLOAD_EXTERNO"
else
    # printf '%s' escreve sem newline final, igual ao corpo que a Meta envia.
    printf '%s' "$PAYLOAD_PADRAO" > "$PAYLOAD"
    echo "Payload: interno (wamid.HBgMNTUz...)"
fi

echo "Bytes do corpo: $(wc -c < "$PAYLOAD")"

# ------------------------------------------------------------- 3. assinatura

ASSINATURA="$(openssl dgst -sha256 -hmac "$APP_SECRET" "$PAYLOAD" | awk '{print $NF}')"

if [[ -z "$ASSINATURA" ]]; then
    echo "ERRO: falha calculando HMAC com openssl" >&2
    exit 1
fi

echo "Assinatura: sha256=${ASSINATURA:0:12}..."
echo "Enviando para: ${ENDPOINT}"
echo

# ---------------------------------------------------------------- 4. request

STATUS="$(curl -s -o "$RESPOSTA" -w '%{http_code}' \
    -X POST "$ENDPOINT" \
    -H 'Content-Type: application/json' \
    -H "X-Hub-Signature-256: sha256=${ASSINATURA}" \
    --data-binary @"$PAYLOAD")"

echo "HTTP ${STATUS}"
echo "--- corpo da resposta ---"
cat "$RESPOSTA"
echo
echo "-------------------------"
echo

# --------------------------------------------------------------- 5. veredito

case "$STATUS" in
    200)
        echo "OK: assinatura aceita e processamento concluiu."
        echo "Confira o registro no banco:"
        echo "  SELECT id, encaminhado FROM MensagemTrOrigemWhatsapp"
        echo "  WHERE codigoRegistroMensagemWhatsapp LIKE 'wamid.HBgMNTUz%';"
        ;;
    400)
        echo "FALHOU na validacao de assinatura. Causas provaveis:"
        echo "  - App Secret do .prop diferente do que a aplicacao le em runtime"
        echo "  - payload alterado depois da captura (byte a byte precisa bater)"
        ;;
    500)
        echo "Passou da assinatura e quebrou no processamento."
        echo "Veja o stacktrace no log da aplicacao."
        ;;
    000)
        echo "Nao conectou. A aplicacao esta rodando em ${SERVIDOR}?"
        ;;
    *)
        echo "Status inesperado. Veja o log da aplicacao."
        ;;
esac

