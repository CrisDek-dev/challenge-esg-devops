package steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import org.junit.Assert;
import services.AutenticacaoService;
import support.ContextoTeste;

import java.util.List;
import java.util.Map;

public class AutenticacaoSteps {

    // Formato de um JWT: cabecalho.conteudo.assinatura (Base64 URL)
    private static final String FORMATO_JWT = "^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$";

    private final ContextoTeste contexto;
    private final AutenticacaoService autenticacaoService;

    public AutenticacaoSteps(ContextoTeste contexto, AutenticacaoService autenticacaoService) {
        this.contexto = contexto;
        this.autenticacaoService = autenticacaoService;
    }

    @Dado("que eu tenha os seguintes dados de login:")
    public void queEuTenhaOsSeguintesDadosDeLogin(List<Map<String, String>> rows) {
        for (Map<String, String> columns : rows) {
            autenticacaoService.setFieldsLogin(columns.get("campo"), columns.get("valor"));
        }
    }

    @Quando("eu enviar a requisição para o endpoint {string} de autenticação")
    public void euEnviarARequisicaoParaOEndpointDeAutenticacao(String endPoint) {
        autenticacaoService.login(endPoint);
    }

    @E("a resposta deve conter um token JWT")
    public void aRespostaDeveConterUmTokenJwt() {
        String token = contexto.getResponse().jsonPath().getString("token");
        Assert.assertNotNull("A resposta nao trouxe o token", token);
        Assert.assertTrue("O token nao esta no formato JWT", token.matches(FORMATO_JWT));
    }

    @Dado("que eu esteja autenticado como administrador")
    public void queEuEstejaAutenticadoComoAdministrador() {
        autenticacaoService.autenticarComoAdministrador();
    }

    @Dado("que eu não esteja autenticado")
    public void queEuNaoEstejaAutenticado() {
        autenticacaoService.removerAutenticacao();
    }
}
