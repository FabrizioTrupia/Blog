package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.ReportAuthorRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportAuthorRatingRepository extends JpaRepository<ReportAuthorRating, Long> {


    // List<ReportAuthorRating> getReportAuthorRating();
}
