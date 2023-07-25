package it.cgmconsulting.myblog.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReportingRequest {

    private long commentId;
    @NotBlank @Size(min=5, max=50)
    private String reason;
    private String note;
}
