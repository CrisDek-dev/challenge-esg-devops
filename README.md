# Challenge ESG - API .NET 8

Atividade da disciplina de Advanced Business Development with .NET.

**Aluno:** Cristofer Ridek Ribeiro Yanachi
**RM:** 561914
**Turma:** 2TDSOB

Tema escolhido: Eficiencia Energetica e Sustentabilidade (tema 1 da lista).

## Sobre o projeto

API REST em ASP.NET Core 8 para monitoramento de consumo de energia em
ambiente industrial. Cada setor da empresa (producao, escritorio, armazem)
tem equipamentos com sensores IoT enviando leituras de consumo. A API
processa essas leituras, gera alertas quando o setor passa do limite mensal
de kWh, permite desligar/ligar equipamentos remotamente (acao restrita a
administradores) e entrega um relatorio consolidado de consumo por setor.

## Stack

- .NET 8 / ASP.NET Core Web API
- Entity Framework Core 8 + SQL Server
- JWT Bearer + roles
- xUnit + Microsoft.AspNetCore.Mvc.Testing
- Swagger / Swashbuckle
- Docker

## Como rodar

Pre-requisito: .NET 8 SDK. SQL Server local ou via Docker.

### 1) Duplo clique nos .bat (Windows)

- `executar-api.bat` - restaura, compila, sobe a API e abre o Swagger no navegador.
- `executar-testes.bat` - roda os testes xUnit.

### 2) Visual Studio

Abrir `EnergyEfficiency.sln` e apertar F5.

### 3) Linha de comando

```
cd EnergyEfficiency.Api
dotnet run
```

API em http://localhost:5000.

### 4) Docker Compose

```
docker compose up --build
```

Sobe API e container do SQL Server. API em http://localhost:8080.

## Login

Usuario admin criado automaticamente no primeiro startup (seed):

```
Email: admin@energia.com
Senha: admin@123
Papel: ADMIN
```

No Swagger, fazer login em `/api/auth/login`, copiar o token e colar no
botao `Authorize`.

## Endpoints

| Metodo | Rota                                | Auth       | Funcao |
|--------|-------------------------------------|------------|--------|
| POST   | /api/auth/login                     | publico    | Login, retorna JWT |
| GET    | /api/leituras                       | publico    | Lista leituras (paginado, com filtros) |
| POST   | /api/leituras                       | JWT        | Registra leitura e dispara alerta se passar do limite |
| GET    | /api/alertas                        | JWT        | Lista alertas (paginado) |
| PATCH  | /api/alertas/{id}/resolver          | ADMIN      | Marca alerta como resolvido |
| GET    | /api/dashboard/consumo-por-setor    | publico    | Relatorio agregado por setor |
| GET    | /api/equipamentos/{id}              | publico    | Detalhes do equipamento |
| POST   | /api/equipamentos/{id}/desligar     | ADMIN      | Desliga equipamento (simula IoT) |
| POST   | /api/equipamentos/{id}/ligar        | ADMIN      | Liga equipamento |

## Fluxo de teste

Depois de logar no Swagger:

1. POST `/api/leituras` com:
   ```json
   { "equipamentoId": 1, "dataHora": "2026-06-18T10:00:00Z", "consumoKwh": 60000 }
   ```
   Equipamento 1 e do setor Producao (limite 50000 kWh). Como passou, a
   resposta vem com `gerouAlerta: true`.

2. GET `/api/alertas` mostra o alerta criado.

3. POST `/api/equipamentos/1/desligar`.

4. Tentar registrar nova leitura nesse equipamento retorna 400.

5. PATCH `/api/alertas/1/resolver`.

## Estrutura do projeto

```
EnergyEfficiency.Api/
  Controllers/        - endpoints HTTP
  Services/           - regras de negocio
  Repositories/       - acesso a dados via EF Core
  Models/             - entidades (Setor, Equipamento, LeituraConsumo, AlertaConsumo, Usuario)
  ViewModels/         - DTOs de entrada e saida
  Data/               - DbContext e Seeder
  Migrations/         - migrations EF Core
  Security/           - geracao do JWT
  Middleware/         - tratamento global de excecoes
  Helpers/            - paginacao
  Exceptions/         - excecoes de dominio
  Program.cs
  Dockerfile

EnergyEfficiency.Tests/
  Controllers/        - 1 teste de integracao por controller
  Infrastructure/     - WebApplicationFactory com banco InMemory

docker-compose.yml
insomnia/             - collection com todos os endpoints
```

Camadas Controllers/Services/Repositories + ViewModels como aproximacao do MVVM pedido no enunciado.

## Testes

Cada controller tem um teste xUnit validando status 200, no formato do
exemplo do enunciado. Os testes usam `WebApplicationFactory` com banco
InMemory.

```
dotnet test
```

Os 5 testes passam.

## Migrations

A migration inicial fica em `EnergyEfficiency.Api/Migrations/`. A aplicacao
roda `Database.MigrateAsync()` no startup, entao o banco e criado na
primeira execucao.

Para criar nova migration:

```
cd EnergyEfficiency.Api
dotnet ef migrations add NomeDaMigration
dotnet ef database update
```

## Performance

Listagens usam `AsNoTracking()` e paginacao server-side (`Skip`/`Take` direto
no banco). Foram criados indices compostos em `LeituraConsumo (EquipamentoId, DataHora)`
e `AlertaConsumo (SetorId, Resolvido)` pra acelerar as queries mais comuns,
mais chaves unicas em Email, Nome do setor e NumeroSerie do equipamento.
