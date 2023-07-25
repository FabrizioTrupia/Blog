package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class UploadFileResponse {

    private String imageDestination; // PRE, HDR, CON
    private String filename;
    private String message;
    private String error;
}
