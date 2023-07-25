package it.cgmconsulting.myblog.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ChangePwdRequest {

    @NotBlank
    private String oldPassword;
    @Pattern(regexp = "^[a-zA-Z0-9]{6,10}$",
            message="La password può contenere solo caratteri maiuscoli e minuscoli e numeri. La lunghezza deve essere compresa tra 6 e 10 caratteri")
    private String newPassword1;
    private String newPassword2;

}
