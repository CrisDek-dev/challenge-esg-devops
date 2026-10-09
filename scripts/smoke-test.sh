#!/usr/bin/env bash
# Uso: scripts/smoke-test.sh <url-base>   (ex.: http://localhost:8081)
# Verifica se o ambiente recem implantado esta funcional:
#   1) /health responde 200
#   2) login com o usuario seed retorna um token JWT
#   3) rota que le do banco (consumo por setor) responde 200
#   4) rota protegida (/api/alertas) responde 401 sem token e 200 com o token
set -euo pipefail

BASE="${1:?uso: scripts/smoke-test.sh <url-base>}"

echo "==> [1/4] Aguardando /health em ${BASE}"
for i in $(seq 1 40); do
  code=$(curl -s --max-time 5 -o /dev/null -w '%{http_code}' "${BASE}/health" || true)
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

echo "==> [2/4] Login com usuario seed"
login=$(curl -s --max-time 10 -w '\n%{http_code}' -X POST "${BASE}/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@energia.com","senha":"admin@123"}')
login_code=$(echo "$login" | tail -n1)
login_body=$(echo "$login" | sed '$d')
echo "    HTTP ${login_code}"
[ "$login_code" = "200" ] || { echo "    FALHA no login: ${login_body}"; exit 1; }
token=$(echo "$login_body" | jq -r '.token')
[ -n "$token" ] && [ "$token" != "null" ] || { echo "    FALHA: token ausente"; exit 1; }
echo "    token JWT recebido ($(echo -n "$token" | wc -c) caracteres)"

echo "==> [3/4] GET /api/dashboard/consumo-por-setor (le do banco)"
dash_code=$(curl -s --max-time 10 -o /tmp/dashboard.json -w '%{http_code}' \
  "${BASE}/api/dashboard/consumo-por-setor")
echo "    HTTP ${dash_code}"
[ "$dash_code" = "200" ] || { echo "    FALHA: $(cat /tmp/dashboard.json)"; exit 1; }
echo "    resposta:"
jq . /tmp/dashboard.json | head -30

echo "==> [4/4] GET /api/alertas (rota protegida por JWT)"
sem_token=$(curl -s --max-time 10 -o /dev/null -w '%{http_code}' "${BASE}/api/alertas?page=1&pageSize=10")
echo "    sem token: HTTP ${sem_token} (esperado 401)"
[ "$sem_token" = "401" ] || { echo "    FALHA: rota deveria exigir token"; exit 1; }
com_token=$(curl -s --max-time 10 -o /dev/null -w '%{http_code}' \
  -H "Authorization: Bearer ${token}" "${BASE}/api/alertas?page=1&pageSize=10")
echo "    com token: HTTP ${com_token} (esperado 200)"
[ "$com_token" = "200" ] || { echo "    FALHA: token nao foi aceito"; exit 1; }

echo "==> SMOKE TEST OK em ${BASE}"
