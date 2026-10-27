package services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.response.Response;
import model.AlertaModel;
import model.LeituraModel;
import support.ContextoTeste;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class AlertasService extends BaseService {

    private static final String ENDPOINT_ALERTAS = "/api/alertas";
    private static final String ENDPOINT_LEITURAS = "/api/leituras";

    // Ar condicionado central (setor Escritorio, limite de 8.000 kWh/mes)
    private static final int EQUIPAMENTO_ESCRITORIO = 3;
    // Consumo acima do limite do setor: a API gera um alerta automaticamente
    private static final BigDecimal CONSUMO_ACIMA_DO_LIMITE = new BigDecimal("9000");

    private final EquipamentosService equipamentosService;
    private final Gson leitorJson = new Gson();

    public AlertasService(ContextoTeste contexto, EquipamentosService equipamentosService) {
        super(contexto);
        this.equipamentosService = equipamentosService;
    }

    public void definirAlerta(long id) {
        contexto.setAlertaId(id);
    }

    /**
     * Pre-condicao: usa um alerta aberto existente ou, se nao houver, registra uma leitura
     * acima do limite do setor Escritorio para que a propria API gere o alerta.
     */
    public void garantirAlertaAberto() {
        List<AlertaModel> abertos = listarAlertas(false);
        if (abertos.isEmpty()) {
            gerarAlertaPorConsumoExcessivo();
            abertos = listarAlertas(false);
        }
        if (abertos.isEmpty()) {
            throw new IllegalStateException("A API nao gerou alerta apos uma leitura acima do limite do setor.");
        }
        contexto.setAlertaId(abertos.get(0).getId());
    }

    // Pre-condicao: usa um alerta ja resolvido ou resolve um alerta aberto
    public void garantirAlertaResolvido() {
        List<AlertaModel> resolvidos = listarAlertas(true);
        if (!resolvidos.isEmpty()) {
            contexto.setAlertaId(resolvidos.get(0).getId());
            return;
        }
        garantirAlertaAberto();
        Response resolucao = requisicao()
                .when()
                .patch(url(ENDPOINT_ALERTAS + "/" + contexto.getAlertaId() + "/resolver"))
                .then()
                .extract()
                .response();
        if (resolucao.statusCode() != 200) {
            throw new IllegalStateException("Nao foi possivel resolver o alerta " + contexto.getAlertaId()
                    + ". Status: " + resolucao.statusCode());
        }
    }

    public void resolverAlerta(String endPoint) {
        String url = url(endPoint.replace("{id}", String.valueOf(contexto.getAlertaId())));
        Response response = requisicao()
                .when()
                .patch(url)
                .then()
                .extract()
                .response();
        registrarResposta("PATCH", url, response);
    }

    public boolean existeAlertaAbertoParaSetor(String setor) {
        return listarAlertas(false).stream().anyMatch(alerta -> setor.equals(alerta.getSetorNome()));
    }

    private List<AlertaModel> listarAlertas(boolean resolvido) {
        Response response = requisicao()
                .queryParam("resolvido", resolvido)
                .queryParam("pageSize", 100)
                .when()
                .get(url(ENDPOINT_ALERTAS))
                .then()
                .extract()
                .response();
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Falha ao listar alertas. Status: " + response.statusCode());
        }
        JsonObject corpo = JsonParser.parseString(response.asString()).getAsJsonObject();
        return Arrays.asList(leitorJson.fromJson(corpo.get("items"), AlertaModel[].class));
    }

    private void gerarAlertaPorConsumoExcessivo() {
        equipamentosService.garantirEstado(EQUIPAMENTO_ESCRITORIO, true);

        LeituraModel leitura = new LeituraModel();
        leitura.setEquipamentoId(EQUIPAMENTO_ESCRITORIO);
        leitura.setDataHora(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        leitura.setConsumoKwh(CONSUMO_ACIMA_DO_LIMITE);

        Response response = requisicao()
                .body(gson.toJson(leitura))
                .when()
                .post(url(ENDPOINT_LEITURAS))
                .then()
                .extract()
                .response();
        if (response.statusCode() != 201) {
            throw new IllegalStateException("Falha ao registrar leitura para gerar alerta. Status: "
                    + response.statusCode() + " - " + response.asString());
        }
    }
}
