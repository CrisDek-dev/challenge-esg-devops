package services;

import io.restassured.response.Response;
import support.ContextoTeste;

import java.util.Map;

import static io.restassured.RestAssured.given;

// Consultas GET genericas (listagens paginadas, dashboard e health check)
public class ConsultaService extends BaseService {

    public ConsultaService(ContextoTeste contexto) {
        super(contexto);
    }

    public void consultar(String endPoint, Map<String, String> filtros) {
        String url = url(endPoint);
        Response response = requisicao()
                .queryParams(filtros)
                .when()
                .get(url)
                .then()
                .extract()
                .response();
        registrarResposta("GET", url + montarQueryString(filtros), response);
    }

    // O /health responde texto puro ("Healthy"), por isso nao usa a requisicao JSON padrao
    public void verificarSaude(String endPoint) {
        String url = url(endPoint);
        Response response = given()
                .when()
                .get(url)
                .then()
                .extract()
                .response();
        registrarResposta("GET", url, response);
    }

    private String montarQueryString(Map<String, String> filtros) {
        if (filtros.isEmpty()) {
            return "";
        }
        StringBuilder query = new StringBuilder("?");
        filtros.forEach((chave, valor) -> query.append(query.length() > 1 ? "&" : "").append(chave).append('=').append(valor));
        return query.toString();
    }
}
