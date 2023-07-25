package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Reason;
import it.cgmconsulting.myblog.entity.ReasonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReasonRepository extends JpaRepository<Reason, ReasonId> {

    Optional<Reason> findByReasonIdReasonAndEndDateIsNull(String reason);

    @Query(value="SELECT r.reason " +
            "FROM reason r " +
            "WHERE (r.end_date IS NULL AND r.start_date <= :now) " +
            "OR (r.end_date IS NOT NULL AND :now BETWEEN r.start_date AND r.end_date) " +
            "ORDER BY r.reason", nativeQuery = true)
    List<String> getReasons(@Param("now") LocalDate now);

    @Query(value="SELECT * FROM reason r " +
            "WHERE ((r.end_date IS NULL AND r.start_date <= :now) " +
            "OR (r.end_date IS NOT NULL AND :now BETWEEN r.start_date AND r.end_date)) " +
            "AND r.reason = :reason " , nativeQuery = true)
    Optional<Reason> getValidReason(@Param("now") LocalDate now, @Param("reason") String reason);

}
