package it.cgmconsulting.myblog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @ToString @AllArgsConstructor
public class ReportAuthorRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement
    private long id;
    @ManyToOne
    @JoinColumn(name = "author" , nullable = false)
    private User user;
    private double average;
    private byte postWritten;
    private LocalDate actually;

}
