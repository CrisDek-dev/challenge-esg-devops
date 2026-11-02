package services;

import io.restassured.response.Response;
import support.ContextoTeste;

import java.util.Map;

import static io.restassured.RestAssured.given;

// Consultas GET (listagens paginadas, dashboard e health check)
public class ConsultaService extends BaseService {

    public ConsultaService(ContextoTeste contexto) {
        super(contexto);
    }

    public void consultar(String endPoint, Map<String, String> filtros) {
        Response response = requisicao()
                .queryParams(filtros)
                .when()
                .get(url(endPoint))
                .then()
                .extract()
                .response();
        contexto.setResponse(response);
    }

    // O /health responde texto ("Healthy"), por isso nao usa a requisicao JSON padrao
    public void verificarSaude(String endPoint) {
        Response response = given()
                .when()
                .get(url(endPoint))
                .then()
                .extract()
                .response();
        contexto.setResponse(response);
    }
}
