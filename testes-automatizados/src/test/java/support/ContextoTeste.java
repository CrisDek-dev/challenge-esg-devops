package support;

import io.restassured.response.Response;
import lombok.Data;

/**
 * Dados compartilhados durante UM cenario.
 * O PicoContainer (cucumber-picocontainer) cria um objeto novo a cada cenario e
 * entrega o mesmo objeto para todas as classes de steps, services e para o Hook.
 */
@Data
public class ContextoTeste {

    // Ultima resposta recebida da API (validada nos passos "Entao")
    private Response response;

    // Token JWT do usuario logado no cenario (null = sem login)
    private String token;

    // ID do alerta usado nos cenarios de resolucao de alertas
    private Long alertaId;
}
