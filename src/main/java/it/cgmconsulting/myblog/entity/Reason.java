package it.cgmconsulting.myblog.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
@Check(constraints = "severity > 0")
public class Reason {

    @EmbeddedId
    private ReasonId reasonId;

    private LocalDate endDate;

    private int severity; // indica la gravità della motivazione ed insieme il numero di giorni di ban associato alla motivazione stessa

    public Reason(ReasonId reasonId, int severity) {
        this.reasonId = reasonId;
        this.severity = severity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reason reason = (Reason) o;
        return reasonId.equals(reason.reasonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reasonId);
    }
}
