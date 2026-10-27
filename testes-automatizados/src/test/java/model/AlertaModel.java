package model;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.math.BigDecimal;

// Item da resposta de GET /api/alertas e de PATCH /api/alertas/{id}/resolver
@Data
public class AlertaModel {
    @Expose
    private Long id;
    @Expose
    private Integer setorId;
    @Expose
    private String setorNome;
    @Expose
    private String dataHora;
    @Expose
    private BigDecimal consumoAcumuladoKwh;
    @Expose
    private BigDecimal limiteKwh;
    @Expose
    private Boolean resolvido;
    @Expose
    private String dataResolucao;
}
