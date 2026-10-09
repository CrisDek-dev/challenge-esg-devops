#!/usr/bin/env bash
# Uso: scripts/smoke-test.sh <url-base>   (ex.: http://localhost:8081)
# Verifica se o ambiente recem implantado esta funcional:
#   1) /health responde 200
#   2) login com o usuario seed retorna um token JWT
#   3) endpoint protegido responde 200 com o token
set -euo pipefail

BASE="${1:?uso: scripts/smoke-test.sh <url-base>}"

echo "==> [1/3] Aguardando /health em ${BASE}"
for i in $(seq 1 40); do
  code=$(curl -s -o /dev/null -w '%{http_code}' "${BASE}/health" || true)
  if [ "$code" = "200" ]; then
    echo "    /health respondeu 200 (tentativa ${i})"
    break
  fi
  if [ "$i" = "40" ]; then
    echo "    FALHA: /health nao respondeu 200 (ultimo codigo: ${code})"
    exit 1
  fi
  sleep 3
done

echo "==> [2/3] Login com usuario seed"
login=$(curl -s -w '\n%{http_code}' -X POST "${BASE}/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@energia.com","senha":"admin@123"}')
login_code=$(echo "$login" | tail -n1)
login_body=$(echo "$login" | sed '$d')
echo "    HTTP ${login_code}"
[ "$login_code" = "200" ] || { echo "    FALHA no login: ${login_body}"; exit 1; }
token=$(echo "$login_body" | jq -r '.token')
[ -n "$token" ] && [ "$token" != "null" ] || { echo "    FALHA: token ausente"; exit 1; }
echo "    token JWT recebido ($(echo -n "$token" | wc -c) caracteres)"

echo "==> [3/3] GET /api/dashboard/consumo-por-setor autenticado"
dash_code=$(curl -s -o /tmp/dashboard.json -w '%{http_code}' \
  -H "Authorization: Bearer ${token}" "${BASE}/api/dashboard/consumo-por-setor")
echo "    HTTP ${dash_code}"
[ "$dash_code" = "200" ] || { echo "    FALHA: $(cat /tmp/dashboard.json)"; exit 1; }
echo "    resposta:"
jq . /tmp/dashboard.json | head -30

echo "==> SMOKE TEST OK em ${BASE}"
