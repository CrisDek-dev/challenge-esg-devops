# language: pt
@regressivo @smoke
Funcionalidade: Verificação de saúde (health check) da API
  Como equipe de operações
  Quero verificar se a API está no ar
  Para garantir a disponibilidade do monitoramento de energia antes de cada entrega

  Cenário: API de eficiência energética disponível
    Quando eu consultar o endpoint "/health" de verificação de saúde
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve ser o texto "Healthy"
