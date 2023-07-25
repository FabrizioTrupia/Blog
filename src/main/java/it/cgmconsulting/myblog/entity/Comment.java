package it.cgmconsulting.myblog.entity;

import it.cgmconsulting.myblog.entity.common.Creation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity
@Table(name="comment", schema = "myblog_corso")
@Getter @Setter @NoArgsConstructor @ToString
public class Comment extends Creation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement
    private long id;

    @ManyToOne
    @JoinColumn(name="post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name="author", nullable = false)
    private User author;

    @Column(nullable = false)
    private String comment;

    private boolean censored = false;

    @ManyToOne
    @JoinColumn(name="parent")
    private Comment parent;

    public Comment(Post post, User author, String comment, Comment parent) {
        this.post = post;
        this.author = author;
        this.comment = comment;
        this.parent = parent;
    }

    public Comment(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
