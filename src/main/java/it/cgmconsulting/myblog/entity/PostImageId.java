package it.cgmconsulting.myblog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode @AllArgsConstructor @ToString
public class PostImageId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "post_id" , nullable = false)
    private Post post;

    @Column(nullable = false)
    private String filename;

}
