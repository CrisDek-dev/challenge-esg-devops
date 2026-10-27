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
        registrarResposta("GET", url, response);
    }

    /**
     * Envia a acao de ligar/desligar. O endpoint vem do cenario com o marcador {id},
     * ex.: "/api/equipamentos/{id}/desligar".
     */
    public void alterarEstado(String endPoint, int id) {
        String url = url(endPoint.replace("{id}", String.valueOf(id)));
        Response response = requisicao()
                .when()
                .post(url)
                .then()
                .extract()
                .response();
        registrarResposta("POST", url, response);

        if (endPoint.endsWith("/desligar") && response.statusCode() == 200) {
            contexto.getEquipamentosParaReligar().add(id);
        }
    }

    /**
     * Pre-condicao dos cenarios: deixa o equipamento no estado desejado (ligado ou desligado)
     * sem alterar a resposta que sera validada no cenario. Exige um token de administrador no contexto.
     */
    public void garantirEstado(int id, boolean ligado) {
        Response consulta = requisicao()
                .when()
                .get(String.format("%s/%d", url(ENDPOINT_EQUIPAMENTOS), id))
                .then()
                .extract()
                .response();
        if (consulta.statusCode() != 200) {
            throw new IllegalStateException("Equipamento " + id + " nao encontrado. Status: " + consulta.statusCode());
        }
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
                    + ". Status: " + alteracao.statusCode() + " - " + alteracao.asString());
        }
        if (!ligado) {
            contexto.getEquipamentosParaReligar().add(id);
        }
    }
}
