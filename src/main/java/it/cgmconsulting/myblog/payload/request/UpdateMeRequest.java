package it.cgmconsulting.myblog.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateMeRequest {
    @Email @NotBlank
    private String email;
    private String bio;
}
