package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.payload.request.CommentRequest;
import it.cgmconsulting.myblog.service.CommentService;
import it.cgmconsulting.myblog.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comment")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "myBlogSecurityScheme")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_READER') or hasRole('ROLE_EDITORIAL_STAFF')")
    public ResponseEntity<?> createComment(@RequestBody @Valid CommentRequest request, @AuthenticationPrincipal UserPrincipal principal){
        return commentService.save(request, principal);
    }

    @GetMapping("public/{postId}")
    public ResponseEntity<?> getCommentsByPost(@PathVariable @Min(1) long postId){
        return commentService.getComments(postId);
    }
}
