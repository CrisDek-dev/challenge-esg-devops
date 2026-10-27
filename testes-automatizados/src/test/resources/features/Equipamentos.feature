# language: pt
@regressivo @ambiental @governanca
Funcionalidade: Consulta e controle remoto de equipamentos monitorados
  Como gestor de energia da empresa
  Quero consultar os equipamentos e ligá-los ou desligá-los remotamente
  Para evitar desperdício de energia, permitindo essa ação crítica somente a administradores

  @smoke @positivo @contrato
  Cenário: Consultar os detalhes de um equipamento existente
    Quando eu consultar o equipamento 1 no endpoint "/api/equipamentos"
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter os valores:
      | campo                | valor         |
      | id                   | 1             |
      | nome                 | Compressor 01 |
      | numeroSerie          | CMP-001       |
      | setorNome            | Producao      |
      | potenciaNominalWatts | 15000         |
    E que o arquivo de contrato esperado é o "Equipamento"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @negativo @contrato
  Cenário: Consultar um equipamento inexistente
    Quando eu consultar o equipamento 9999 no endpoint "/api/equipamentos"
    Então o status code da resposta deve ser 404
    E o corpo de resposta de erro da api deve retornar a mensagem "Equipamento 9999 nao encontrado."
    E que o arquivo de contrato esperado é o "Erro da API"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @positivo @contrato @religar-equipamento
  Cenário: Desligar remotamente um equipamento ligado
    Dado que eu esteja autenticado como administrador
    E que o equipamento 4 esteja ligado
    Quando eu enviar a requisição para o endpoint "/api/equipamentos/{id}/desligar" do equipamento 4
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter os valores:
      | campo | valor |
      | id    | 4     |
      | ativo | false |
    E que o arquivo de contrato esperado é o "Equipamento"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @positivo @contrato @religar-equipamento
  Cenário: Ligar remotamente um equipamento desligado
    Dado que eu esteja autenticado como administrador
    E que o equipamento 4 esteja desligado
    Quando eu enviar a requisição para o endpoint "/api/equipamentos/{id}/ligar" do equipamento 4
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter os valores:
      | campo | valor |
      | id    | 4     |
      | ativo | true  |
    E que o arquivo de contrato esperado é o "Equipamento"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @negativo
  Cenário: Não permitir ligar um equipamento que já está ligado
    Dado que eu esteja autenticado como administrador
    E que o equipamento 4 esteja ligado
    Quando eu enviar a requisição para o endpoint "/api/equipamentos/{id}/ligar" do equipamento 4
    Então o status code da resposta deve ser 400
    E o corpo de resposta de erro da api deve retornar a mensagem "Equipamento ja esta ligado."

  @negativo
  Cenário: Não permitir desligar um equipamento sem estar autenticado
    Dado que eu não esteja autenticado
    Quando eu enviar a requisição para o endpoint "/api/equipamentos/{id}/desligar" do equipamento 4
    Então o status code da resposta deve ser 401
    E a resposta deve exigir autenticação do tipo "Bearer"

  @negativo @religar-equipamento
  Cenário: Equipamento desligado não aceita novas leituras de consumo
    Dado que eu esteja autenticado como administrador
    E que o equipamento 4 esteja desligado
    E que eu tenha os seguintes dados da leitura:
      | campo         | valor                |
      | equipamentoId | 4                    |
      | dataHora      | 2026-10-15T10:00:00Z |
      | consumoKwh    | 25                   |
    Quando eu enviar a requisição para o endpoint "/api/leituras" de registro de leitura
    Então o status code da resposta deve ser 400
    E o corpo de resposta de erro da api deve retornar a mensagem "Equipamento esta desligado e nao pode registrar leituras."
