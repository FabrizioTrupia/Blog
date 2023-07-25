package it.cgmconsulting.myblog.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostRequest {

    @NotEmpty @Size(max=100, min=2)
    private String title;
    @NotEmpty @Size(max=255, min=10)
    private String overview;
    @NotEmpty @Size(max=20000, min=100)
    private String content;

}
