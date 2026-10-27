package support;

/**
 * Configuracoes do ambiente que esta sendo testado.
 * Ordem de prioridade: propriedade do Maven (-Dapi.baseUrl=...), variavel de ambiente
 * (API_BASE_URL) e, por ultimo, o valor padrao (API rodando no Docker, porta 8080).
 */
public final class ConfiguracaoApi {

    private ConfiguracaoApi() {
    }

    public static String getBaseUrl() {
        String baseUrl = ler("api.baseUrl", "API_BASE_URL", "http://localhost:8080");
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    // Usuario administrador criado pelo seed da API (dados de teste, nao sao credenciais reais)
    public static String getEmailAdmin() {
        return ler("api.adminEmail", "API_ADMIN_EMAIL", "admin@energia.com");
    }

    public static String getSenhaAdmin() {
        return ler("api.adminSenha", "API_ADMIN_SENHA", "admin@123");
    }

    private static String ler(String propriedade, String variavelAmbiente, String padrao) {
        String valor = System.getProperty(propriedade);
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(variavelAmbiente);
        }
        return (valor == null || valor.isBlank()) ? padrao : valor.trim();
    }
}
