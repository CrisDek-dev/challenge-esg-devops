package services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.LoginModel;
import support.ContextoTeste;

import static io.restassured.RestAssured.given;

public class AutenticacaoService extends BaseService {

    private static final String ENDPOINT_LOGIN = "/api/auth/login";

    // Usuario criado pelo seed da API (dados de teste)
    private static final String EMAIL_ADMIN = "admin@energia.com";
    private static final String SENHA_ADMIN = "admin@123";

    final LoginModel loginModel = new LoginModel();

    public AutenticacaoService(ContextoTeste contexto) {
        super(contexto);
    }

    public void setFieldsLogin(String field, String value) {
        switch (field) {
            case "email" -> loginModel.setEmail(value);
            case "senha" -> loginModel.setSenha(value);
            default -> throw new IllegalStateException("Campo inesperado: " + field);
        }
    }

    public void login(String endPoint) {
        contexto.setResponse(enviarLogin(url(endPoint), loginModel));
    }

    // Faz login como administrador e guarda o token JWT para as proximas requisicoes do cenario
    public void autenticarComoAdministrador() {
        LoginModel admin = new LoginModel();
        admin.setEmail(EMAIL_ADMIN);
        admin.setSenha(SENHA_ADMIN);

        Response response = enviarLogin(url(ENDPOINT_LOGIN), admin);
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Nao foi possivel autenticar o administrador. Status: "
                    + response.statusCode() + " - " + response.asString());
        }
        contexto.setToken(response.jsonPath().getString("token"));
    }

    public void removerAutenticacao() {
        contexto.setToken(null);
    }

    private Response enviarLogin(String url, LoginModel login) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gson.toJson(login))
                .when()
                .post(url)
                .then()
                .extract()
                .response();
    }
}
