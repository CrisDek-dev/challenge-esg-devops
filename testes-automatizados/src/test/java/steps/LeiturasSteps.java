package steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import org.junit.Assert;
import services.AlertasService;
import services.LeiturasService;

import java.util.List;
import java.util.Map;

public class LeiturasSteps {

    private final LeiturasService leiturasService;
    private final AlertasService alertasService;

    public LeiturasSteps(LeiturasService leiturasService, AlertasService alertasService) {
        this.leiturasService = leiturasService;
        this.alertasService = alertasService;
    }

    @Dado("que eu tenha os seguintes dados da leitura:")
    public void queEuTenhaOsSeguintesDadosDaLeitura(List<Map<String, String>> rows) {
        for (Map<String, String> columns : rows) {
            leiturasService.setFieldsLeitura(columns.get("campo"), columns.get("valor"));
        }
    }

    @Quando("eu enviar a requisição para o endpoint {string} de registro de leitura")
    public void euEnviarARequisicaoParaOEndpointDeRegistroDeLeitura(String endPoint) {
        leiturasService.registrarLeitura(endPoint);
    }

    @E("deve existir um alerta de consumo em aberto para o setor {string}")
    public void deveExistirUmAlertaDeConsumoEmAbertoParaOSetor(String setor) {
        Assert.assertTrue("Nenhum alerta em aberto encontrado para o setor " + setor,
                alertasService.existeAlertaAbertoParaSetor(setor));
    }
}
