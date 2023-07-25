package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Comment;
import it.cgmconsulting.myblog.entity.Reason;
import it.cgmconsulting.myblog.entity.ReasonId;
import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.payload.request.ReasonRequest;
import it.cgmconsulting.myblog.repository.ReasonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReasonService {

    private final ReasonRepository reasonRepository;

    public ResponseEntity<Reason> createReason(ReasonRequest request){
        ReasonId reasonId = new ReasonId(request.getReason(), request.getStartDate());
        if(reasonRepository.existsById(reasonId))
            return new ResponseEntity("Reason already present", HttpStatus.BAD_REQUEST);
        Reason reason = new Reason(reasonId, request.getSeverity());
        return new ResponseEntity(reasonRepository.save(reason), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> updateReason(ReasonRequest request) {

        ReasonId reasonId = new ReasonId(request.getReason(), request.getStartDate());

        // trovare la reason attualmente valida (end date null)
        Optional<Reason> reasonVecchia = reasonRepository.findByReasonIdReasonAndEndDateIsNull(request.getReason());

        // confrontare le severity (se sono uguali nn ha senso cambiarle)
        if (reasonVecchia.isEmpty())
            return new ResponseEntity<>("Reason not found, please create a new reason before updating ", HttpStatus.NOT_FOUND);

        if (reasonVecchia.get().getSeverity() == request.getSeverity())
            return new ResponseEntity<>("The selected severity is the same as the previous one", HttpStatus.BAD_REQUEST);

        // aggiornare il record di questa reason inserendo la end date (nuova startDate - 1 giorno)
        reasonVecchia.get().setEndDate(request.getStartDate().minusDays(1));

        // creare il nuovo record con la nuova startDate e severity
        Reason nuovaReason = new Reason(reasonId, request.getSeverity());
        return new ResponseEntity<>(reasonRepository.save(nuovaReason), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> removeReason(String reason, LocalDate endDate) {

        // trovare la reason attualmente valida (end date null)
        Optional<Reason> reasonVecchia = reasonRepository.findByReasonIdReasonAndEndDateIsNull(reason);

        if (reasonVecchia.isEmpty())
            return new ResponseEntity<>("Reason not found, please create a new reason before updating ", HttpStatus.NOT_FOUND);

        reasonVecchia.get().setEndDate(endDate);
        return new ResponseEntity<>("Reason "+reason+" no more valid", HttpStatus.OK);
    }

    public ResponseEntity<?> getReasons() {
        // elenco di reason List<String> in corso di validità ordinate alfabeticamnete
        return new ResponseEntity(reasonRepository.getReasons(LocalDate.now()), HttpStatus.OK);
    }

    protected Reason getValidReason(String reason){
        Reason r = reasonRepository.getValidReason(LocalDate.now(), reason).orElseThrow(
                () -> new ResourceNotFoundException("Reason", "reason", reason)
        );
        return r;
    }
}
