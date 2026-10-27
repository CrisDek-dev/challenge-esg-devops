# Testes Automatizados (BDD) - Energy Efficiency API

**Aluno:** Cristofer Ridek Ribeiro Yanachi - **RM:** 561914 - **Turma:** 2TDSOB

Projeto de testes automatizados da **Energy Efficiency API** (ESG - Eficiência Energética e
Sustentabilidade), escrito com **BDD em Gherkin (português)**. Os testes rodam contra a API de
verdade (com SQL Server), validando **status code**, **corpo da resposta (JSON)** e **contrato
(JSON Schema)** de **todos os endpoints**.

| Item | Quantidade |
|------|-----------|
| Funcionalidades (`.feature`) | 6 |
| Cenários (positivos + negativos) | 26 (12 positivos, 14 negativos) |
| Endpoints cobertos | 10 de 10 |
| Contratos JSON Schema | 9 |

---

## Tecnologias

| Ferramenta | Para que serve |
|------------|----------------|
| Java 21 + Maven (com Maven Wrapper) | Linguagem e build. Não precisa instalar o Maven: o `mvnw` baixa sozinho |
| Cucumber 7 (`cucumber-java`, `cucumber-junit`) | Executa os cenários escritos em Gherkin |
| Cucumber PicoContainer | Compartilha o estado do cenário (resposta, token) entre as classes de steps |
| Rest Assured | Faz as requisições HTTP para a API |
| Gson + Lombok | Modelos das requisições (JSON) |
| networknt `json-schema-validator` + `org.json` | Testes de contrato com JSON Schema |
| JUnit 4 | Asserções e execução pela classe `TestRunner` |

---

## Estrutura

```
testes-automatizados/
├── pom.xml                      dependências + surefire executando o TestRunner
├── mvnw / mvnw.cmd              Maven Wrapper (Linux/Mac e Windows)
├── Dockerfile                   imagem que executa os testes em container
└── src/test/
    ├── java/
    │   ├── runner/TestRunner.java     configuração do Cucumber (features, glue, tags, relatórios)
    │   ├── hook/Hook.java             @BeforeAll espera a API subir, @After anexa evidências e religa equipamentos
    │   ├── steps/                     tradução de cada passo Gherkin para Java
    │   ├── services/                  chamadas Rest Assured para a API e validação de contrato
    │   ├── model/                     modelos (DTOs) das requisições e respostas
    │   └── support/                   configuração (URL da API) e contexto do cenário
    └── resources/
        ├── features/                  cenários em Gherkin (# language: pt)
        └── schemas/                   contratos JSON Schema
```

## Cenários por funcionalidade

| Feature | Endpoints | Cenários | Pilar ESG |
|---------|-----------|----------|-----------|
| `Autenticacao.feature` | `POST /api/auth/login` | 4 (1 positivo, 3 negativos) | Governança |
| `Leituras.feature` | `GET /api/leituras`, `POST /api/leituras` | 6 (3 positivos, 3 negativos) | Ambiental |
| `Alertas.feature` | `GET /api/alertas`, `PATCH /api/alertas/{id}/resolver` | 5 (2 positivos, 3 negativos) | Ambiental + Governança |
| `Equipamentos.feature` | `GET /api/equipamentos/{id}`, `POST .../desligar`, `POST .../ligar` | 7 (3 positivos, 4 negativos) | Ambiental + Governança |
| `Dashboard.feature` | `GET /api/dashboard/consumo-por-setor` | 3 (2 positivos, 1 negativo) | Ambiental + Governança |
| `SaudeApi.feature` | `GET /health` | 1 | Disponibilidade |

### Tags

| Tag | Uso |
|-----|-----|
| `@regressivo` | todas as funcionalidades (é a tag que o `TestRunner` executa) |
| `@smoke` | cenários rápidos que provam que a API está de pé |
| `@contrato` | cenários que validam a resposta com JSON Schema |
| `@positivo` / `@negativo` | caminho feliz / tratamento de falhas |
| `@ambiental` / `@governanca` | pilar ESG protegido pelo cenário |
| `@religar-equipamento` | cenários que desligam um equipamento; o Hook religa ao final |

---

## Como executar

Pré-requisito para rodar fora do Docker: **JDK 21 ou superior** (`java -version`).
A URL da API é configurável (nessa ordem de prioridade):

1. `-Dapi.baseUrl=http://...` no comando do Maven;
2. variável de ambiente `API_BASE_URL`;
3. padrão `http://localhost:8080` (API no Docker).

### Opção 1 - Tudo em Docker (API + banco + testes)

Na **raiz do repositório**, com o `.env` criado (`copy .env.example .env`):

```
docker compose -f docker-compose.yml -f docker-compose.testes.yml up --build --exit-code-from testes
```

O compose sobe o SQL Server, a API e o container de testes (este `Dockerfile`). Quando os testes
terminam, tudo para e o código de saída é o dos testes. O relatório fica em
`testes-automatizados/target/cucumber-reports.html`. Para limpar:

```
docker compose -f docker-compose.yml -f docker-compose.testes.yml down -v
```

### Opção 2 - API no Docker, testes na máquina

```
docker compose up -d --build          (na raiz do repositório)
cd testes-automatizados
mvnw.cmd clean test                   (Windows)
./mvnw clean test                     (Linux/Mac/Git Bash)
```

### Opção 3 - Sem Docker (Windows)

1. Rodar `executar-api.bat` na raiz (API em http://localhost:5000, usando o SQL Server local);
2. Rodar `executar-testes-bdd.bat` na raiz. Ele executa `mvnw.cmd clean test` apontando para a
   porta 5000 e abre o relatório HTML no final.

Ou manualmente: `mvnw.cmd clean test -Dapi.baseUrl=http://localhost:5000`

### Opção 4 - IntelliJ

Abrir a pasta `testes-automatizados` como projeto Maven (plugins **Cucumber for Java** e
**Lombok**) e executar a classe `runner/TestRunner` (ou um cenário direto pelo ícone de play no
`.feature`). Se a API estiver na porta 5000, adicionar `-Dapi.baseUrl=http://localhost:5000` em
*Run > Edit Configurations > VM options*.

### Executar só uma parte (tags)

```
mvnw.cmd clean test -Dcucumber.filter.tags="@smoke"
mvnw.cmd clean test -Dcucumber.filter.tags="@contrato and @negativo"
```

### Opção 5 - Pipeline (GitHub Actions)

O job **Testes BDD (API)** do `.github/workflows/ci-cd.yml` roda a cada push na `main`:
sobe API + SQL Server com `docker compose`, executa `./mvnw clean test`, executa de novo pelo
container de testes (`docker-compose.testes.yml`), publica o resumo na aba *Summary* e o
relatório HTML como artefato `relatorio-testes-bdd`. A imagem Docker e os deploys só rodam se
esse job passar.

---

## Relatórios gerados (`target/`)

| Arquivo | Conteúdo |
|---------|----------|
| `cucumber-reports.html` | relatório visual, com a requisição e a resposta da API anexadas em cada cenário |
| `cucumber.json` | resultado em JSON (usado no resumo do pipeline) |
| `cucumber-junit.xml` | resultado no formato JUnit |

## Como os testes ficam independentes

- Cada cenário faz o próprio login (`Dado que eu esteja autenticado como administrador`).
- Pré-condições deixam os dados no estado certo antes da ação: `que o equipamento 4 esteja
  ligado`, `que exista um alerta de consumo em aberto` (se não houver, registra uma leitura acima
  do limite do setor para a própria API gerar o alerta).
- Cenários que desligam equipamentos têm a tag `@religar-equipamento`; um `@After` do `Hook`
  religa o equipamento no final, mesmo se o cenário falhar.
- O `@BeforeAll` espera o `/health` responder (até 3 minutos), já que no Docker a API aguarda o
  banco e aplica as migrations antes de ficar disponível.

Com isso a suíte pode ser executada várias vezes seguidas, em qualquer ordem, no mesmo banco.
