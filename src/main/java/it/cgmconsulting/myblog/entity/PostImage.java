package it.cgmconsulting.myblog.entity;

import it.cgmconsulting.myblog.entity.common.ImagePosition;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString @EqualsAndHashCode
public class PostImage {

    @EmbeddedId
    private PostImageId postImageId;

    @Column(nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private ImagePosition imagePosition;

    // Esempio di costruttore
    // PostImage pi = new PostImage(new PostImageId(post, filename), ImagePosition.VALUE);
}
