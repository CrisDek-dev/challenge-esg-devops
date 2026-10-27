package steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import org.junit.Assert;
import services.AlertasService;
import support.ContextoTeste;

public class AlertasSteps {

    private final ContextoTeste contexto;
    private final AlertasService alertasService;

    public AlertasSteps(ContextoTeste contexto, AlertasService alertasService) {
        this.contexto = contexto;
        this.alertasService = alertasService;
    }

    @Dado("que exista um alerta de consumo em aberto")
    public void queExistaUmAlertaDeConsumoEmAberto() {
        alertasService.garantirAlertaAberto();
    }

    @Dado("que exista um alerta de consumo já resolvido")
    public void queExistaUmAlertaDeConsumoJaResolvido() {
        alertasService.garantirAlertaResolvido();
    }

    @Dado("que o alerta informado seja o de ID {long}")
    public void queOAlertaInformadoSejaODeId(long id) {
        alertasService.definirAlerta(id);
    }

    @Quando("eu enviar a requisição para o endpoint {string} de resolução do alerta")
    public void euEnviarARequisicaoParaOEndpointDeResolucaoDoAlerta(String endPoint) {
        alertasService.resolverAlerta(endPoint);
    }

    // Governanca: a resolucao do alerta precisa ficar registrada (rastreabilidade)
    @E("a data de resolução do alerta deve estar registrada")
    public void aDataDeResolucaoDoAlertaDeveEstarRegistrada() {
        String dataResolucao = contexto.getResponse().jsonPath().getString("dataResolucao");
        Assert.assertNotNull("O alerta foi resolvido sem data de resolucao", dataResolucao);
        Assert.assertFalse("O alerta foi resolvido sem data de resolucao", dataResolucao.isBlank());
    }
}
