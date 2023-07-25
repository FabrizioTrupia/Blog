package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.*;
import it.cgmconsulting.myblog.entity.common.ReportingStatus;
import it.cgmconsulting.myblog.payload.request.ReportingRequest;
import it.cgmconsulting.myblog.repository.ReportingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportingService {
    private final ReportingRepository reportingRepository;
    private final ReasonService reasonService;
    private final CommentService commentService;

    public ResponseEntity<?> createReporting(ReportingRequest request, long userId) {
        // verificare che il commento non sia già presente nella reporting
        if (reportingRepository.existsById(new ReportingId(new Comment(request.getCommentId()))))
            return new ResponseEntity("This comment is already reported", HttpStatus.BAD_REQUEST);
        Comment c = commentService.findCommentNotCensored(request.getCommentId());
        if (userId == c.getAuthor().getId())
            return new ResponseEntity("You cannot report yourself", HttpStatus.BAD_REQUEST);
        User u = new User(userId);
        Reason r = reasonService.getValidReason(request.getReason());
        Reporting rep = new Reporting(new ReportingId(c), u, r);
        rep.setNote(request.getNote());
        reportingRepository.save(rep);
        return new ResponseEntity("New reporting about comment " + rep.getReportingId().getComment().getId() + " has been created", HttpStatus.CREATED);
    }
    @Transactional
    public ResponseEntity<?> manageReporting(long commentId, String reason, String status) {
        Reason r = null;

        Optional<Reporting> reporting = reportingRepository.findById(new ReportingId(commentService.findCommentNotCensored(commentId)));
        if (!reporting.isPresent())
            return new ResponseEntity("Reporting not found", HttpStatus.NOT_FOUND);
        Reporting rep = reporting.get();

        if(reason != null) {
            if (rep.getReason().getReasonId().getReason().equals(reason))
                r = rep.getReason();
            else
                r = reasonService.getValidReason(reason);
        } else
            r = rep.getReason();

        if (rep.getStatus().equals(ReportingStatus.CLOSED_WITH_BAN) ||
                rep.getStatus().equals(ReportingStatus.CLOSED_WITHOUT_BAN)
        )
            return new ResponseEntity("This Reporting is closed", HttpStatus.FORBIDDEN);
        /* Cambi di status
        OPEN - > IN_PROGRESS, CLOSED_WITH_BAN, CLOSED_WITHOUT_BAN
        IN_PROGRESS -> CLOSED_WITH_BAN, CLOSED_WITHOUT_BAN
        Una volta che la segnalazione è chiusa (con o senza ban) non è più modificabile
         */
        if (ReportingStatus.valueOf(status).equals(rep.getStatus()))
            return new ResponseEntity("Please change status", HttpStatus.FORBIDDEN);
        if (ReportingStatus.valueOf(status).equals(ReportingStatus.OPEN) && rep.getStatus().equals(ReportingStatus.IN_PROGRESS))
            return new ResponseEntity("You cannot revert the status to OPEN", HttpStatus.FORBIDDEN);
        else {
            if (ReportingStatus.valueOf(status).equals(ReportingStatus.CLOSED_WITH_BAN)) {
                // CENSURO IL COMMENTO E DISABILITO LO USER
                rep.getReportingId().getComment().setCensored(true);
                rep.getReportingId().getComment().getAuthor().setEnabled(false);
                rep.getReportingId().getComment().getAuthor().setBannedUntil(LocalDateTime.now().plusDays(r.getSeverity()));
            }
            rep.setStatus(ReportingStatus.valueOf(status));
            rep.setReason(r);
        }
        return new ResponseEntity("REPORTING UPDATED!", HttpStatus.OK);
    }

    public ResponseEntity<?> getReporting(ReportingStatus status) {
        return new ResponseEntity<>(reportingRepository.getReportingByStatus(status), HttpStatus.OK);
    }
}
