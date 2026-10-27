package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import support.ConfiguracaoApi;
import support.ContextoTeste;

import static io.restassured.RestAssured.given;

/**
 * Base dos services: monta as requisicoes Rest Assured e guarda a resposta no contexto do cenario.
 */
public abstract class BaseService {

    protected final ContextoTeste contexto;

    // Serializa apenas os campos anotados com @Expose
    protected final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    protected BaseService(ContextoTeste contexto) {
        this.contexto = contexto;
    }

    protected String url(String endPoint) {
        return ConfiguracaoApi.getBaseUrl() + endPoint;
    }

    /**
     * Requisicao JSON padrao. Se o cenario estiver autenticado, envia o token JWT no cabecalho Authorization.
     */
    protected RequestSpecification requisicao() {
        RequestSpecification requisicao = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
        if (contexto.getToken() != null) {
            requisicao.header("Authorization", "Bearer " + contexto.getToken());
        }
        return requisicao;
    }

    // Guarda a resposta para os passos de validacao (status code, corpo e contrato)
    protected void registrarResposta(String metodo, String url, Response response) {
        contexto.setUltimaRequisicao(metodo + " " + url);
        contexto.setResponse(response);
    }
}
