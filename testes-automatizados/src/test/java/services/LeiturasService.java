package services;

import io.restassured.response.Response;
import model.LeituraModel;
import support.ContextoTeste;

import java.math.BigDecimal;

public class LeiturasService extends BaseService {

    final LeituraModel leituraModel = new LeituraModel();

    public LeiturasService(ContextoTeste contexto) {
        super(contexto);
    }

    public void setFieldsLeitura(String field, String value) {
        switch (field) {
            case "equipamentoId" -> leituraModel.setEquipamentoId(Integer.parseInt(value));
            case "dataHora" -> leituraModel.setDataHora(value);
            case "consumoKwh" -> leituraModel.setConsumoKwh(new BigDecimal(value));
            default -> throw new IllegalStateException("Campo inesperado: " + field);
        }
    }

    public void registrarLeitura(String endPoint) {
        String url = url(endPoint);
        String bodyToSend = gson.toJson(leituraModel);
        Response response = requisicao()
                .body(bodyToSend)
                .when()
                .post(url)
                .then()
                .extract()
                .response();
        registrarResposta("POST", url, response);
    }
}
