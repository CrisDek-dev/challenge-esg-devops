#!/usr/bin/env bash
# Uso: scripts/deploy.sh <staging|production>
# Sobe (ou atualiza) a stack do ambiente informado usando docker compose.
# Variaveis obrigatorias: API_IMAGE, SA_PASSWORD, JWT_SECRET_KEY
set -euo pipefail

ENV_NAME="${1:?uso: scripts/deploy.sh <staging|production>}"
ENV_FILE="env/${ENV_NAME}.env"
PROJECT="esg-${ENV_NAME}"

: "${API_IMAGE:?API_IMAGE nao definida}"
: "${SA_PASSWORD:?SA_PASSWORD nao definida}"
: "${JWT_SECRET_KEY:?JWT_SECRET_KEY nao definida}"
[ -f "$ENV_FILE" ] || { echo "Arquivo $ENV_FILE nao encontrado"; exit 1; }

set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a
export API_IMAGE SA_PASSWORD JWT_SECRET_KEY

echo "==> Deploy do ambiente: ${ENV_NAME}"
echo "==> Imagem: ${API_IMAGE}"
echo "==> Porta publicada: ${API_PORT}"

docker compose -p "$PROJECT" -f docker-compose.yml pull api
docker compose -p "$PROJECT" -f docker-compose.yml up -d --no-build

echo "==> Containers em execucao:"
docker compose -p "$PROJECT" -f docker-compose.yml ps
