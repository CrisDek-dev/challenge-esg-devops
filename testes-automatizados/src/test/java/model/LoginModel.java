package model;

import com.google.gson.annotations.Expose;
import lombok.Data;

// Corpo da requisicao POST /api/auth/login
@Data
public class LoginModel {
    @Expose
    private String email;
    @Expose
    private String senha;
}
