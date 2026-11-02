package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import support.ConfiguracaoApi;
import support.ContextoTeste;

import static io.restassured.RestAssured.given;

// Classe base dos services: guarda o contexto do cenario e monta as requisicoes
public abstract class BaseService {

    protected final ContextoTeste contexto;

    // Converte os modelos em JSON usando apenas os campos com @Expose
    protected final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    protected BaseService(ContextoTeste contexto) {
        this.contexto = contexto;
    }

    protected String url(String endPoint) {
        return ConfiguracaoApi.getBaseUrl() + endPoint;
    }

    // Requisicao JSON padrao. Se o cenario fez login, envia o token JWT no cabecalho
    protected RequestSpecification requisicao() {
        RequestSpecification requisicao = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
        if (contexto.getToken() != null) {
            requisicao.header("Authorization", "Bearer " + contexto.getToken());
        }
        return requisicao;
    }
}
