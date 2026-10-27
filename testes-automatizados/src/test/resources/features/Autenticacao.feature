# language: pt
@regressivo @governanca
Funcionalidade: Autenticação de usuários na API de eficiência energética
  Como gestor de energia da empresa
  Quero me autenticar na API
  Para que somente pessoas autorizadas operem o monitoramento de consumo (governança e compliance)

  @smoke @positivo @contrato
  Cenário: Login bem-sucedido com credenciais válidas
    Dado que eu tenha os seguintes dados de login:
      | campo | valor             |
      | email | admin@energia.com |
      | senha | admin@123         |
    Quando eu enviar a requisição para o endpoint "/api/auth/login" de autenticação
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter os valores:
      | campo | valor         |
      | nome  | Administrador |
      | papel | ADMIN         |
    E a resposta deve conter um token JWT
    E que o arquivo de contrato esperado é o "Login bem-sucedido"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @negativo @contrato
  Cenário: Login sem sucesso ao informar a senha incorreta
    Dado que eu tenha os seguintes dados de login:
      | campo | valor             |
      | email | admin@energia.com |
      | senha | senhaErrada1      |
    Quando eu enviar a requisição para o endpoint "/api/auth/login" de autenticação
    Então o status code da resposta deve ser 400
    E o corpo de resposta de erro da api deve retornar a mensagem "Email ou senha invalidos."
    E que o arquivo de contrato esperado é o "Erro da API"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

  @negativo @contrato
  Esquema do Cenário: Login rejeitado por dados em formato inválido (<situacao>)
    Dado que eu tenha os seguintes dados de login:
      | campo | valor   |
      | email | <email> |
      | senha | <senha> |
    Quando eu enviar a requisição para o endpoint "/api/auth/login" de autenticação
    Então o status code da resposta deve ser 400
    E a resposta deve apontar erro de validação no campo "<campoComErro>"
    E que o arquivo de contrato esperado é o "Erro de validação"
    E a resposta da requisição deve estar em conformidade com o contrato selecionado

    Exemplos:
      | situacao                      | email             | senha     | campoComErro |
      | e-mail sem arroba             | admin-energia.com | admin@123 | Email        |
      | senha com menos de 6 dígitos  | admin@energia.com | 123       | Senha        |
