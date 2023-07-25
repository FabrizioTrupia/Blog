package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CommentResponse {

    private long id;
    private String comment;
    private String author; // username dell'autore del commento
    private LocalDateTime createdAt;
    private Long parentId;
}
