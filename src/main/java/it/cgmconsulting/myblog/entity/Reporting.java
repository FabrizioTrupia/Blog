package it.cgmconsulting.myblog.entity;

import it.cgmconsulting.myblog.entity.common.ReportingStatus;
import it.cgmconsulting.myblog.entity.common.CreationUpdate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Reporting extends CreationUpdate {

    @EmbeddedId
    private ReportingId reportingId;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "reason", referencedColumnName = "reason"),
            @JoinColumn(name = "start_date", referencedColumnName = "startDate")
    })
    private Reason reason;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private ReportingStatus status = ReportingStatus.OPEN;


    public Reporting(ReportingId reportingId, User user, Reason reason) {
        this.reportingId = reportingId;
        this.user = user;
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reporting reporting = (Reporting) o;
        return reportingId.equals(reporting.reportingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportingId);
    }
}
