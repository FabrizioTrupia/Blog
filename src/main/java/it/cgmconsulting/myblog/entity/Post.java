package it.cgmconsulting.myblog.entity;

import it.cgmconsulting.myblog.entity.common.Creation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Post extends Creation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement
    private long id;

    @Column(nullable = false, length = 100, unique = true)
    private String title;

    @Column(nullable = false)
    private String overview;

    @Column(nullable = false, length = 20000)
    private String content;

    @ManyToOne
    @JoinColumn(name="author", nullable = false)
    private User author;

    // null: il caporedattore deve ancora approvare il post
    // data futura: il caporedattore ha approvato il post ma questo verrà pubblicato in futuro
    // data passata: post pubblicato e visibile
    private LocalDateTime publishedAt;

    @ManyToMany
    @JoinTable(name="post_category",
            joinColumns = {@JoinColumn(name="post_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name="category_name", referencedColumnName = "categoryName")}
    )
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public Post(String title, String overview, String content, User author) {
        this.title = title;
        this.overview = overview;
        this.content = content;
        this.author = author;
    }

    public Post(long id) {
        this.id  = id;
    }

    public void addComment(Comment comment){
        comments.add(comment);
        comment.setPost(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
