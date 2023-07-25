package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class PostDetailResponse {

    private long id;
    private String image;
    private String title;
    private String content;
    private String author;
    private LocalDateTime publishedAt;
    private double average; // media dei voti ricevuti

}
