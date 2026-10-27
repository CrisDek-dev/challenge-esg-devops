package model;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.math.BigDecimal;

// Corpo da requisicao POST /api/leituras (leitura enviada pelo sensor IoT)
@Data
public class LeituraModel {
    @Expose
    private Integer equipamentoId;
    @Expose
    private String dataHora;
    @Expose
    private BigDecimal consumoKwh;
}
