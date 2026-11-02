package services;

import io.restassured.response.Response;
import support.ContextoTeste;

public class EquipamentosService extends BaseService {

    private static final String ENDPOINT_EQUIPAMENTOS = "/api/equipamentos";

    public EquipamentosService(ContextoTeste contexto) {
        super(contexto);
    }

    public void consultarEquipamento(String endPoint, int id) {
        String url = String.format("%s/%d", url(endPoint), id);
        Response response = requisicao()
                .when()
                .get(url)
                .then()
                .extract()
                .response();
        contexto.setResponse(response);
    }

    // O endpoint vem do cenario com o marcador {id}, ex.: "/api/equipamentos/{id}/desligar"
    public void alterarEstado(String endPoint, int id) {
        String url = url(endPoint.replace("{id}", String.valueOf(id)));
        Response response = requisicao()
                .when()
                .post(url)
                .then()
                .extract()
                .response();
        contexto.setResponse(response);
    }

    /**
     * Pre-condicao dos cenarios: deixa o equipamento ligado ou desligado antes do teste.
     * Nao altera a resposta que vai ser validada no cenario. Precisa do login de administrador.
     */
    public void garantirEstado(int id, boolean ligado) {
        Response consulta = requisicao()
                .when()
                .get(String.format("%s/%d", url(ENDPOINT_EQUIPAMENTOS), id))
                .then()
                .extract()
                .response();
        if (consulta.jsonPath().getBoolean("ativo") == ligado) {
            return;
        }

        String acao = ligado ? "ligar" : "desligar";
        Response alteracao = requisicao()
                .when()
                .post(String.format("%s/%d/%s", url(ENDPOINT_EQUIPAMENTOS), id, acao))
                .then()
                .extract()
                .response();
        if (alteracao.statusCode() != 200) {
            throw new IllegalStateException("Nao foi possivel " + acao + " o equipamento " + id
                    + ". Status: " + alteracao.statusCode());
        }
    }
}
