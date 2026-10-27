package steps;

import com.google.gson.Gson;
import com.networknt.schema.ValidationMessage;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import model.ErroApiModel;
import org.junit.Assert;
import services.ConsultaService;
import services.ContratoService;
import support.ContextoTeste;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Passos usados por todas as funcionalidades: consultas GET, status code,
 * validacao do corpo (JSON) e teste de contrato (JSON Schema).
 */
public class ComumSteps {

    private final ContextoTeste contexto;
    private final ConsultaService consultaService;
    private final ContratoService contratoService;

    public ComumSteps(ContextoTeste contexto, ConsultaService consultaService, ContratoService contratoService) {
        this.contexto = contexto;
        this.consultaService = consultaService;
        this.contratoService = contratoService;
    }

    @Quando("eu consultar o endpoint {string} com os filtros:")
    public void euConsultarOEndpointComOsFiltros(String endPoint, List<Map<String, String>> rows) {
        Map<String, String> filtros = new LinkedHashMap<>();
        for (Map<String, String> columns : rows) {
            filtros.put(columns.get("filtro"), columns.get("valor"));
        }
        consultaService.consultar(endPoint, filtros);
    }

    @Quando("eu consultar o endpoint {string} de verificação de saúde")
    public void euConsultarOEndpointDeVerificacaoDeSaude(String endPoint) {
        consultaService.verificarSaude(endPoint);
    }

    @Então("o status code da resposta deve ser {int}")
    public void oStatusCodeDaRespostaDeveSer(int statusCode) {
        Assert.assertEquals("Status code inesperado. Corpo da resposta: " + resposta().asString(),
                statusCode, resposta().statusCode());
    }

    @E("o corpo da resposta deve conter os valores:")
    public void oCorpoDaRespostaDeveConterOsValores(List<Map<String, String>> rows) {
        for (Map<String, String> columns : rows) {
            String campo = columns.get("campo");
            String esperado = columns.get("valor");
            Object atual = resposta().jsonPath().get(campo);
            assertValorIgual(campo, esperado, atual);
        }
    }

    @E("o corpo de resposta de erro da api deve retornar a mensagem {string}")
    public void oCorpoDeRespostaDeErroDaApiDeveRetornarAMensagem(String message) {
        ErroApiModel erro = new Gson().fromJson(resposta().asString(), ErroApiModel.class);
        Assert.assertEquals(message, erro.getDetail());
    }

    @E("a resposta deve apontar erro de validação no campo {string}")
    public void aRespostaDeveApontarErroDeValidacaoNoCampo(String campo) {
        Map<String, Object> erros = resposta().jsonPath().getMap("errors");
        Assert.assertNotNull("A resposta nao trouxe a lista de erros de validacao", erros);
        boolean campoComErro = erros.keySet().stream().anyMatch(chave -> chave.equalsIgnoreCase(campo));
        Assert.assertTrue("Campo '" + campo + "' nao apareceu nos erros: " + erros.keySet(), campoComErro);
    }

    @E("a resposta deve exigir autenticação do tipo {string}")
    public void aRespostaDeveExigirAutenticacaoDoTipo(String tipo) {
        String cabecalho = resposta().getHeader("WWW-Authenticate");
        Assert.assertNotNull("A resposta nao trouxe o cabecalho WWW-Authenticate", cabecalho);
        Assert.assertTrue("Cabecalho WWW-Authenticate inesperado: " + cabecalho, cabecalho.startsWith(tipo));
    }

    @E("a lista {string} da resposta deve ter no máximo {int} registros")
    public void aListaDaRespostaDeveTerNoMaximoRegistros(String campo, int maximo) {
        List<Object> lista = resposta().jsonPath().getList(campo);
        Assert.assertNotNull("A resposta nao trouxe a lista '" + campo + "'", lista);
        Assert.assertTrue("A lista '" + campo + "' tem " + lista.size() + " registros (maximo " + maximo + ")",
                lista.size() <= maximo);
    }

    @E("o corpo da resposta deve ser o texto {string}")
    public void oCorpoDaRespostaDeveSerOTexto(String texto) {
        Assert.assertEquals(texto, resposta().asString().trim());
    }

    @E("que o arquivo de contrato esperado é o {string}")
    public void queOArquivoDeContratoEsperadoEO(String contract) throws IOException {
        contratoService.setContract(contract);
    }

    @E("a resposta da requisição deve estar em conformidade com o contrato selecionado")
    public void aRespostaDaRequisicaoDeveEstarEmConformidadeComOContratoSelecionado() throws IOException {
        Set<ValidationMessage> validateResponse = contratoService.validateResponseAgainstSchema();
        Assert.assertTrue("O contrato está inválido. Erros encontrados: " + validateResponse, validateResponse.isEmpty());
    }

    private Response resposta() {
        Assert.assertNotNull("Nenhuma requisicao foi enviada neste cenario", contexto.getResponse());
        return contexto.getResponse();
    }

    // Numeros sao comparados pelo valor (15000 == 15000.00); o restante como texto
    private void assertValorIgual(String campo, String esperado, Object atual) {
        Assert.assertNotNull("Campo '" + campo + "' nao encontrado na resposta", atual);
        if (atual instanceof Number) {
            Assert.assertEquals("Campo '" + campo + "' com valor diferente (atual: " + atual + ")",
                    0, new BigDecimal(esperado).compareTo(new BigDecimal(atual.toString())));
        } else {
            Assert.assertEquals("Campo '" + campo + "' com valor diferente", esperado, String.valueOf(atual));
        }
    }
}
