# Imagem que executa os testes automatizados (BDD) contra a API.
# O endereco da API vem da variavel API_BASE_URL (padrao: servico "api" do docker-compose).
FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /testes

# Baixa as dependencias primeiro: essa camada fica em cache enquanto o pom.xml nao mudar
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src

ENV API_BASE_URL=http://api:8080

# Sem "clean": a pasta target pode estar montada como volume para guardar o relatorio no host
CMD ["mvn", "-B", "test"]
