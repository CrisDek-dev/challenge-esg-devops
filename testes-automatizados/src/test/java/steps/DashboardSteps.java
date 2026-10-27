package steps;

import io.cucumber.java.pt.E;
import org.junit.Assert;
import support.ContextoTeste;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardSteps {

    private final ContextoTeste contexto;

    public DashboardSteps(ContextoTeste contexto) {
        this.contexto = contexto;
    }

    @E("a resposta deve conter os setores:")
    public void aRespostaDeveConterOsSetores(List<Map<String, String>> rows) {
        List<Map<String, Object>> setores = contexto.getResponse().jsonPath().getList("$");
        for (Map<String, String> columns : rows) {
            String nome = columns.get("setor");
            Map<String, Object> setor = setores.stream()
                    .filter(item -> nome.equals(item.get("setorNome")))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Setor '" + nome + "' nao encontrado na resposta"));

            BigDecimal limiteEsperado = new BigDecimal(columns.get("limiteMensalKwh"));
            BigDecimal limiteAtual = new BigDecimal(setor.get("limiteMensalKwh").toString());
            Assert.assertEquals("Limite mensal do setor " + nome + " (atual: " + limiteAtual + ")",
                    0, limiteEsperado.compareTo(limiteAtual));
        }
    }

    @E("todos os setores devem apresentar consumo total igual a {int}")
    public void todosOsSetoresDevemApresentarConsumoTotalIgualA(int consumo) {
        List<Object> consumos = contexto.getResponse().jsonPath().getList("consumoTotalKwh");
        Assert.assertFalse("O dashboard nao retornou nenhum setor", consumos.isEmpty());
        for (Object valor : consumos) {
            Assert.assertEquals("Consumo total inesperado: " + valor,
                    0, BigDecimal.valueOf(consumo).compareTo(new BigDecimal(valor.toString())));
        }
    }
}
