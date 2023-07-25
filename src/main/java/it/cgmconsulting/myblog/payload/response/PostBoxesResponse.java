package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PostBoxesResponse {

    private long id;
    private String image;
    private String author;
    private LocalDateTime publishedAt;
    private String title;
    private String overview;
    private Set<String> categories;

    public PostBoxesResponse(long id, String image, String author, LocalDateTime publishedAt, String title, String overview) {
        this.id = id;
        this.image = image;
        this.author = author;
        this.publishedAt = publishedAt;
        this.title = title;
        this.overview = overview;
    }
}
