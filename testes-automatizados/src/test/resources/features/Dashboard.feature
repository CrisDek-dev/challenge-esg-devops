# language: pt
@regressivo @ambiental @governanca
Funcionalidade: Painel de consumo de energia por setor
  Como diretoria de sustentabilidade (ESG)
  Quero visualizar o consumo de energia agregado por setor
  Para comparar com as metas de consumo e dar transparência aos indicadores ambientais

  @smoke @positivo @contrato
  Cenário: Consultar o consumo de energia por setor
    Quando eu consultar o endpoint "/api/dashboard/consumo-por-setor" com os filtros:
      | filtro     | valor      |
      | dataInicio | 2026-10-01 |
      | dataFim    | 2026-10-31 |
    Então o status code da resposta deve ser 200
    E a resposta deve conter os setores:
      | setor      | limiteMensalKwh |
      | Producao   | 50000           |
      | Escritorio | 8000            |
      | Armazem    | 15000           |
    E que o arquivo de contrato esperado é o "Consumo por setor"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @positivo
  Cenário: Consultar o consumo de um período sem leituras registradas
    Quando eu consultar o endpoint "/api/dashboard/consumo-por-setor" com os filtros:
      | filtro     | valor      |
      | dataInicio | 2000-01-01 |
      | dataFim    | 2000-01-31 |
    Então o status code da resposta deve ser 200
    E todos os setores devem apresentar consumo total igual a 0

  @negativo @contrato
  Cenário: Consultar o consumo informando uma data em formato inválido
    Quando eu consultar o endpoint "/api/dashboard/consumo-por-setor" com os filtros:
      | filtro     | valor      |
      | dataInicio | 31-13-2026 |
    Então o status code da resposta deve ser 400
    E a resposta deve apontar erro de validação no campo "DataInicio"
    E que o arquivo de contrato esperado é o "Erro de validação"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado
