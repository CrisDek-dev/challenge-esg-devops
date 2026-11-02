package hook;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import services.AutenticacaoService;
import services.EquipamentosService;
import support.ConfiguracaoApi;
import support.ContextoTeste;

public class Hook {

    private final ContextoTeste contexto;
    private final AutenticacaoService autenticacaoService;
    private final EquipamentosService equipamentosService;

    public Hook(ContextoTeste contexto, AutenticacaoService autenticacaoService, EquipamentosService equipamentosService) {
        this.contexto = contexto;
        this.autenticacaoService = autenticacaoService;
        this.equipamentosService = equipamentosService;
    }

    @BeforeAll
    public static void setUpBeforeClass() throws InterruptedException {
        System.out.println("Testando a API em: " + ConfiguracaoApi.getBaseUrl());
        aguardarApi();
    }

    @AfterAll
    public static void tearDownAfterClass() {
        System.out.println("Fim dos testes. Relatorio em target/cucumber-reports.html");
    }

    @Before
    public void setUp(Scenario scenario) {
        System.out.println("Iniciando o cenario: " + scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        // Guarda a resposta da API no relatorio HTML como evidencia do teste
        if (contexto.getResponse() != null) {
            scenario.attach(contexto.getResponse().asPrettyString(), "text/plain", "Resposta da API");
        }
        System.out.println("Cenario finalizado: " + scenario.getName() + " - " + scenario.getStatus());
    }

    // Os cenarios com essa tag desligam o equipamento 4, entao ele e religado no final
    @After("@religar-equipamento")
    public void religarEquipamento() {
        autenticacaoService.autenticarComoAdministrador();
        equipamentosService.garantirEstado(4, true);
    }

    // No Docker a API demora alguns segundos para subir, entao espera o /health responder (ate 2 minutos)
    private static void aguardarApi() throws InterruptedException {
        for (int tentativa = 1; tentativa <= 40; tentativa++) {
            try {
                if (RestAssured.get(ConfiguracaoApi.getBaseUrl() + "/health").statusCode() == 200) {
                    return;
                }
            } catch (Exception e) {
                System.out.println("A API ainda nao respondeu, tentando de novo...");
            }
            Thread.sleep(3000);
        }
        throw new IllegalStateException("A API nao respondeu. Verifique se ela esta rodando (veja o README).");
    }
}
