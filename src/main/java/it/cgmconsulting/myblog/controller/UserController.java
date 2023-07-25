package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import it.cgmconsulting.myblog.payload.request.ChangePwdRequest;
import it.cgmconsulting.myblog.payload.request.ChangeRoleRequest;
import it.cgmconsulting.myblog.payload.request.UpdateMeRequest;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.AuthService;
import it.cgmconsulting.myblog.service.AvatarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("user") // localhost:8081/user/...
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "myBlogSecurityScheme")
public class UserController {

    private final AuthService authService;
    private final AvatarService avatarService;

    @PutMapping("change-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> changeRole(@RequestBody @Valid ChangeRoleRequest request, @AuthenticationPrincipal UserPrincipal principal){
        // verificare che l'admin non cambi ruolo a se stesso
        return authService.changeRole(request, principal);
    }

    @PutMapping("change-pwd")
    public ResponseEntity<?> changePwd(@RequestBody @Valid ChangePwdRequest request, @AuthenticationPrincipal UserPrincipal principal){
        return authService.changePwd(request, principal);
    }

    @PutMapping("update-me")
    public ResponseEntity<?> updateMe(@AuthenticationPrincipal UserPrincipal principal, @RequestBody @Valid UpdateMeRequest request){
        return authService.updateMe(request, principal);
    }

    @PostMapping(value = "avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> avatar(@AuthenticationPrincipal UserPrincipal principal, @RequestParam MultipartFile file) throws IOException {
        return avatarService.avatar(file, principal);
    }

    @GetMapping("get-me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal UserPrincipal principal){
        return authService.getMe(principal);
    }

    @GetMapping("/public/get-authors")
    public ResponseEntity<?> getAllAuthors(@RequestParam(defaultValue = "ROLE_WRITER") String authorityName){
        return authService.getAllAuthors(authorityName);
    }



}
