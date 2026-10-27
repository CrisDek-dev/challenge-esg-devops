package support;

import io.restassured.response.Response;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Estado compartilhado durante UM cenario.
 * O PicoContainer (cucumber-picocontainer) cria uma instancia nova a cada cenario e
 * entrega a mesma instancia para todas as classes de steps, services e hooks.
 */
@Data
public class ContextoTeste {

    // Ultima resposta recebida da API (validada pelos passos "Entao")
    private Response response;

    // Descricao da ultima requisicao (ex.: "POST http://localhost:8080/api/leituras"), usada no relatorio
    private String ultimaRequisicao;

    // Token JWT do usuario autenticado no cenario (null = sem autenticacao)
    private String token;

    // ID do alerta usado nos cenarios de resolucao de alertas
    private Long alertaId;

    // Equipamentos desligados durante o cenario, que o Hook religa ao final
    private Set<Integer> equipamentosParaReligar = new LinkedHashSet<>();
}
