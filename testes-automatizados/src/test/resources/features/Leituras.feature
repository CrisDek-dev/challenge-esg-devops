# language: pt
@regressivo @ambiental
Funcionalidade: Registro e consulta de leituras de consumo de energia
  Como sistema de monitoramento IoT da empresa
  Quero registrar e consultar as leituras de consumo dos equipamentos
  Para acompanhar o gasto de energia e identificar desperdícios

  @smoke @positivo @contrato
  Cenário: Consultar as leituras de consumo com paginação
    Quando eu consultar o endpoint "/api/leituras" com os filtros:
      | filtro   | valor |
      | page     | 1     |
      | pageSize | 5     |
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter os valores:
      | campo    | valor |
      | page     | 1     |
      | pageSize | 5     |
    E a lista "items" da resposta deve ter no máximo 5 registros
    E que o arquivo de contrato esperado é o "Leituras paginadas"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @positivo @contrato
  Cenário: Registrar a leitura de consumo de um equipamento ligado
    Dado que eu esteja autenticado como administrador
    E que o equipamento 2 esteja ligado
    E que eu tenha os seguintes dados da leitura:
      | campo         | valor                |
      | equipamentoId | 2                    |
      | dataHora      | 2026-10-15T10:00:00Z |
      | consumoKwh    | 150.75               |
    Quando eu enviar a requisição para o endpoint "/api/leituras" de registro de leitura
    Então o status code da resposta deve ser 201
    E o corpo da resposta deve conter os valores:
      | campo           | valor      |
      | equipamentoId   | 2          |
      | equipamentoNome | Esteira 01 |
      | setorNome       | Producao   |
      | consumoKwh      | 150.75     |
    E que o arquivo de contrato esperado é o "Leitura registrada"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @positivo
  Cenário: Leitura que ultrapassa o limite mensal do setor gera alerta de consumo
    Dado que eu esteja autenticado como administrador
    E que o equipamento 3 esteja ligado
    E que eu tenha os seguintes dados da leitura:
      | campo         | valor                |
      | equipamentoId | 3                    |
      | dataHora      | 2026-10-20T08:00:00Z |
      | consumoKwh    | 9000                 |
    Quando eu enviar a requisição para o endpoint "/api/leituras" de registro de leitura
    Então o status code da resposta deve ser 201
    E deve existir um alerta de consumo em aberto para o setor "Escritorio"

  @negativo
  Cenário: Registrar leitura sem estar autenticado
    Dado que eu não esteja autenticado
    E que eu tenha os seguintes dados da leitura:
      | campo         | valor                |
      | equipamentoId | 2                    |
      | dataHora      | 2026-10-15T10:00:00Z |
      | consumoKwh    | 10                   |
    Quando eu enviar a requisição para o endpoint "/api/leituras" de registro de leitura
    Então o status code da resposta deve ser 401
    E a resposta deve exigir autenticação do tipo "Bearer"

  @negativo @contrato
  Cenário: Registrar leitura com consumo negativo
    Dado que eu esteja autenticado como administrador
    E que eu tenha os seguintes dados da leitura:
      | campo         | valor                |
      | equipamentoId | 2                    |
      | dataHora      | 2026-10-15T10:00:00Z |
      | consumoKwh    | -5                   |
    Quando eu enviar a requisição para o endpoint "/api/leituras" de registro de leitura
    Então o status code da resposta deve ser 400
    E a resposta deve apontar erro de validação no campo "ConsumoKwh"
    E que o arquivo de contrato esperado é o "Erro de validação"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @negativo @contrato
  Cenário: Registrar leitura para um equipamento inexistente
    Dado que eu esteja autenticado como administrador
    E que eu tenha os seguintes dados da leitura:
      | campo         | valor                |
      | equipamentoId | 9999                 |
      | dataHora      | 2026-10-15T10:00:00Z |
      | consumoKwh    | 10                   |
    Quando eu enviar a requisição para o endpoint "/api/leituras" de registro de leitura
    Então o status code da resposta deve ser 404
    E o corpo de resposta de erro da api deve retornar a mensagem "Equipamento 9999 nao encontrado."
    E que o arquivo de contrato esperado é o "Erro da API"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado
