package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Reporting;
import it.cgmconsulting.myblog.entity.ReportingId;
import it.cgmconsulting.myblog.entity.common.ReportingStatus;
import it.cgmconsulting.myblog.payload.response.ReportingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportingRepository extends JpaRepository<Reporting, ReportingId> {

    boolean existsById(ReportingId reportingId);

    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.ReportingResponse(" +
            "rep.reportingId.comment.id, " +
            "rep.reportingId.comment.comment, " +
            "rep.reason.reasonId.reason, " +
            "rep.note, " +
            "rep.user.username, " +
            "rep.updatedAt" +
            ") FROM Reporting rep " +
            "WHERE rep.status = :status " +
            "ORDER BY rep.updatedAt")
    List<ReportingResponse> getReportingByStatus(@Param("status") ReportingStatus status);
}
