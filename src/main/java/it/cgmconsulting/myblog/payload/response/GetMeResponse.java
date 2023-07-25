package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GetMeResponse {

    private long id;
    private String username;
    private String email;
    private String bio;
    private LocalDate createdAt; // yyyy-MM-dd
    private String filename;
    private String filetype;
    private byte[] data;

}
