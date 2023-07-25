package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.entity.common.ReportingStatus;
import it.cgmconsulting.myblog.payload.request.ReportingRequest;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.ReportingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("reporting")
@RequiredArgsConstructor @Validated
@SecurityRequirement(name = "myBlogSecurityScheme")
public class ReportingController {
    private final ReportingService reportingService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_READER')")
    public ResponseEntity<?> createReporting(@RequestBody @Valid ReportingRequest request, @AuthenticationPrincipal UserPrincipal principal){
        return reportingService.createReporting(request, principal.getId());
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> manageReporting(@PathVariable long commentId, @RequestParam(required = false) String reason, @RequestParam String status){
        return reportingService.manageReporting(commentId, reason, status);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> getReporting (@RequestParam(defaultValue = "OPEN") ReportingStatus status){
        return reportingService.getReporting(status);
    }

}
