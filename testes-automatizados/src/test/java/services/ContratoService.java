package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.json.JSONObject;
import org.json.JSONTokener;
import support.ContextoTeste;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Testes de contrato: valida a resposta da API contra um JSON Schema (pasta src/test/resources/schemas).
 */
public class ContratoService {

    private static final String SCHEMAS_PATH = "/schemas/";

    private final ContextoTeste contexto;
    private final ObjectMapper mapper = new ObjectMapper();
    private JSONObject jsonSchema;

    public ContratoService(ContextoTeste contexto) {
        this.contexto = contexto;
    }

    public void setContract(String contract) throws IOException {
        String arquivo = switch (contract) {
            case "Login bem-sucedido" -> "login-bem-sucedido.json";
            case "Leitura registrada" -> "leitura-registrada.json";
            case "Leituras paginadas" -> "leituras-paginadas.json";
            case "Alerta" -> "alerta.json";
            case "Alertas paginados" -> "alertas-paginados.json";
            case "Equipamento" -> "equipamento.json";
            case "Consumo por setor" -> "consumo-por-setor.json";
            case "Erro da API" -> "erro-api.json";
            case "Erro de validação" -> "erro-validacao.json";
            default -> throw new IllegalStateException("Contrato inesperado: " + contract);
        };
        jsonSchema = loadJsonFromFile(SCHEMAS_PATH + arquivo);
    }

    public Set<ValidationMessage> validateResponseAgainstSchema() throws IOException {
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        JsonSchema schema = schemaFactory.getSchema(jsonSchema.toString());
        // readTree aceita tanto objeto quanto lista (o dashboard responde uma lista)
        JsonNode jsonResponseNode = mapper.readTree(contexto.getResponse().getBody().asString());
        return schema.validate(jsonResponseNode);
    }

    // Le o arquivo do classpath, funcionando igual na IDE, no Maven e no Docker
    private JSONObject loadJsonFromFile(String filePath) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException("Arquivo de contrato nao encontrado: " + filePath);
            }
            return new JSONObject(new JSONTokener(inputStream));
        }
    }
}
