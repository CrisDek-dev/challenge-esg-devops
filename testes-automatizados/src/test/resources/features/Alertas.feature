# language: pt
@regressivo @ambiental @governanca
Funcionalidade: Gestão dos alertas de consumo excessivo de energia
  Como gestor de energia da empresa
  Quero consultar e resolver os alertas de consumo acima do limite
  Para agir contra o desperdício e manter o registro (rastreabilidade) de cada tratativa

  @negativo
  Cenário: Consultar alertas sem estar autenticado
    Dado que eu não esteja autenticado
    Quando eu consultar o endpoint "/api/alertas" com os filtros:
      | filtro | valor |
      | page   | 1     |
    Então o status code da resposta deve ser 401
    E a resposta deve exigir autenticação do tipo "Bearer"

  @smoke @positivo @contrato
  Cenário: Consultar os alertas de consumo estando autenticado
    Dado que eu esteja autenticado como administrador
    Quando eu consultar o endpoint "/api/alertas" com os filtros:
      | filtro   | valor |
      | page     | 1     |
      | pageSize | 10    |
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter os valores:
      | campo    | valor |
      | page     | 1     |
      | pageSize | 10    |
    E a lista "items" da resposta deve ter no máximo 10 registros
    E que o arquivo de contrato esperado é o "Alertas paginados"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @positivo @contrato
  Cenário: Resolver um alerta de consumo em aberto
    Dado que eu esteja autenticado como administrador
    E que exista um alerta de consumo em aberto
    Quando eu enviar a requisição para o endpoint "/api/alertas/{id}/resolver" de resolução do alerta
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter os valores:
      | campo     | valor |
      | resolvido | true  |
    E a data de resolução do alerta deve estar registrada
    E que o arquivo de contrato esperado é o "Alerta"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @negativo @contrato
  Cenário: Não permitir resolver novamente um alerta já resolvido
    Dado que eu esteja autenticado como administrador
    E que exista um alerta de consumo já resolvido
    Quando eu enviar a requisição para o endpoint "/api/alertas/{id}/resolver" de resolução do alerta
    Então o status code da resposta deve ser 400
    E o corpo de resposta de erro da api deve retornar a mensagem "Alerta ja esta resolvido."
    E que o arquivo de contrato esperado é o "Erro da API"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @negativo
  Cenário: Resolver um alerta inexistente
    Dado que eu esteja autenticado como administrador
    E que o alerta informado seja o de ID 999999
    Quando eu enviar a requisição para o endpoint "/api/alertas/{id}/resolver" de resolução do alerta
    Então o status code da resposta deve ser 404
    E o corpo de resposta de erro da api deve retornar a mensagem "Alerta 999999 nao encontrado."
