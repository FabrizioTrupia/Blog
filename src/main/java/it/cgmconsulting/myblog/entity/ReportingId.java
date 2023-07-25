package it.cgmconsulting.myblog.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode @AllArgsConstructor @ToString
public class ReportingId {

    @OneToOne
    @JoinColumn(name="comment_id")
    private Comment comment;

}
