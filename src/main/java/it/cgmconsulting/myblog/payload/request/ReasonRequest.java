package it.cgmconsulting.myblog.payload.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReasonRequest {

    @NotBlank @Size(max=50, min=5)
    private String reason;
    @NotNull @FutureOrPresent
    private LocalDate startDate;
    @Min(1)
    private int severity;
}
