package it.cgmconsulting.myblog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode @AllArgsConstructor @ToString
public class ReasonId implements Serializable {

    @Column(nullable = false, length = 50)
    private String reason;

    @Column(nullable = false)
    private LocalDate startDate;
}
