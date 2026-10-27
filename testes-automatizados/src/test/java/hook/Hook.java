package hook;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import services.AutenticacaoService;
import services.EquipamentosService;
import support.ConfiguracaoApi;
import support.ContextoTeste;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Hook {

    // Tempo maximo esperando a API subir (no Docker ela espera o SQL Server e aplica as migrations)
    private static final int ESPERA_MAXIMA_SEGUNDOS = 180;

    private final ContextoTeste contexto;
    private final AutenticacaoService autenticacaoService;
    private final EquipamentosService equipamentosService;

    public Hook(ContextoTeste contexto, AutenticacaoService autenticacaoService, EquipamentosService equipamentosService) {
        this.contexto = contexto;
        this.autenticacaoService = autenticacaoService;
        this.equipamentosService = equipamentosService;
    }

    @BeforeAll
    public static void aguardarApiDisponivel() throws InterruptedException {
        String healthUrl = ConfiguracaoApi.getBaseUrl() + "/health";
        System.out.println("Ambiente testado: " + ConfiguracaoApi.getBaseUrl());
        System.out.println("Aguardando a API responder em " + healthUrl + " ...");

        long limite = System.currentTimeMillis() + ESPERA_MAXIMA_SEGUNDOS * 1000L;
        while (System.currentTimeMillis() < limite) {
            try {
                if (RestAssured.get(healthUrl).statusCode() == 200) {
                    System.out.println("API disponivel. Iniciando os cenarios.");
                    return;
                }
            } catch (Exception e) {
                // API ainda subindo: tenta novamente
            }
            Thread.sleep(3000);
        }
        throw new IllegalStateException("A API nao respondeu em " + healthUrl + " apos " + ESPERA_MAXIMA_SEGUNDOS
                + " segundos. Verifique se ela esta rodando (veja o README).");
    }

    @AfterAll
    public static void finalizarExecucao() {
        System.out.println("Execucao finalizada. Relatorio: target/cucumber-reports.html");
    }

    @Before
    public void iniciarCenario(Scenario scenario) {
        System.out.println("Iniciando cenario: " + scenario.getName());
    }

    /**
     * Cenarios com a tag @religar-equipamento desligam um equipamento.
     * Ao final, o equipamento e religado para nao afetar os demais cenarios (testes independentes).
     */
    @After(value = "@religar-equipamento", order = 10)
    public void religarEquipamentos() {
        if (contexto.getEquipamentosParaReligar().isEmpty()) {
            return;
        }
        if (contexto.getToken() == null) {
            autenticacaoService.autenticarComoAdministrador();
        }
        for (Integer id : new ArrayList<>(contexto.getEquipamentosParaReligar())) {
            equipamentosService.garantirEstado(id, true);
            System.out.println("Equipamento " + id + " religado apos o cenario.");
        }
    }

    // Anexa a ultima requisicao/resposta no relatorio HTML (evidencia de execucao)
    @After(order = 0)
    public void finalizarCenario(Scenario scenario) {
        Response response = contexto.getResponse();
        if (response != null) {
            String evidencia = "Requisicao: " + contexto.getUltimaRequisicao()
                    + "\nStatus: " + response.statusCode()
                    + "\n\n" + formatarCorpo(response);
            scenario.attach(evidencia.getBytes(StandardCharsets.UTF_8), "text/plain", "Resposta da API");
        }
        System.out.println("Cenario finalizado: " + scenario.getName() + " -> " + scenario.getStatus());
    }

    private String formatarCorpo(Response response) {
        String corpo = response.asString();
        if (corpo == null || corpo.isBlank()) {
            return "(sem corpo)";
        }
        try {
            return response.jsonPath().prettify();
        } catch (Exception e) {
            return corpo;
        }
    }
}
