package support;

public class ConfiguracaoApi {

    /**
     * Endereco da API testada. Pode ser trocado sem mexer no codigo:
     * - no Maven: mvn clean test -Dapi.baseUrl=http://localhost:5000
     * - por variavel de ambiente: API_BASE_URL (usada no Docker)
     * Se nada for informado, usa a API rodando no Docker (porta 8080).
     */
    public static String getBaseUrl() {
        String baseUrl = System.getProperty("api.baseUrl");
        if (baseUrl == null) {
            baseUrl = System.getenv("API_BASE_URL");
        }
        if (baseUrl == null) {
            baseUrl = "http://localhost:8080";
        }
        return baseUrl;
    }
}
