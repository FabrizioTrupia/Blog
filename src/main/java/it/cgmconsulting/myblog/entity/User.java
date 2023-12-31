package it.cgmconsulting.myblog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.cgmconsulting.myblog.entity.common.CreationUpdate;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class User extends CreationUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement
    private long id;
    @Column(nullable = false, length = 20, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    private boolean enabled;
    private String bio;

    @ManyToMany
    @JoinTable(name="user_authority",
            joinColumns = {@JoinColumn(name="user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name="authority_id", referencedColumnName = "id")}
    )
    private Set<Authority> authorities = new HashSet<>();
    private String confirmCode;
    private LocalDateTime bannedUntil;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = false;
    }


    public User(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
