package it.cgmconsulting.myblog.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class PostSearchResponse {

    private long id;
    private String image;
    private String title;
    @JsonIgnore
    private String content;
    private String author;
    private LocalDateTime publishedAt;
    private String overview;

}
