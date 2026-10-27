package model;

import com.google.gson.annotations.Expose;
import lombok.Data;

// Corpo de erro padrao da API (application/problem+json), gerado pelo middleware de excecoes
@Data
public class ErroApiModel {
    @Expose
    private String type;
    @Expose
    private String title;
    @Expose
    private Integer status;
    @Expose
    private String detail;
    @Expose
    private String instance;
}
