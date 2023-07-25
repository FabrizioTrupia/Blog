package it.cgmconsulting.myblog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString @EqualsAndHashCode
public class AvatarId implements Serializable {

    @OneToOne
    @JoinColumn(name="id", nullable = false)
    private User user;
}
