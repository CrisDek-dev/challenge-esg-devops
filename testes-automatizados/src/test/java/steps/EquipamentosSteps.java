package steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import services.EquipamentosService;

public class EquipamentosSteps {

    private final EquipamentosService equipamentosService;

    public EquipamentosSteps(EquipamentosService equipamentosService) {
        this.equipamentosService = equipamentosService;
    }

    @Quando("eu consultar o equipamento {int} no endpoint {string}")
    public void euConsultarOEquipamentoNoEndpoint(int id, String endPoint) {
        equipamentosService.consultarEquipamento(endPoint, id);
    }

    @Dado("que o equipamento {int} esteja ligado")
    public void queOEquipamentoEstejaLigado(int id) {
        equipamentosService.garantirEstado(id, true);
    }

    @Dado("que o equipamento {int} esteja desligado")
    public void queOEquipamentoEstejaDesligado(int id) {
        equipamentosService.garantirEstado(id, false);
    }

    @Quando("eu enviar a requisição para o endpoint {string} do equipamento {int}")
    public void euEnviarARequisicaoParaOEndpointDoEquipamento(String endPoint, int id) {
        equipamentosService.alterarEstado(endPoint, id);
    }
}
