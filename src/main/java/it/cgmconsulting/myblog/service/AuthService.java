package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.mail.Mail;
import it.cgmconsulting.myblog.mail.MailService;
import it.cgmconsulting.myblog.repository.UserRepository;
import it.cgmconsulting.myblog.entity.Authority;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.payload.request.*;
import it.cgmconsulting.myblog.payload.response.JwtAuthenticationResponse;
import it.cgmconsulting.myblog.security.JwtTokenProvider;
import it.cgmconsulting.myblog.security.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${app.mail.sender}")
    private String from;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;

    protected boolean existsByUsernameOrEmail(String username, String email){
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    protected User fromRequestToEntity(SignUpRequest request){
        return new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
    }

    protected User save(User user){
        userRepository.save(user);
        return user;
    }

    // Utente abilitato all'atto della registrazione
    public ResponseEntity<?> signup(SignUpRequest request){
        if(existsByUsernameOrEmail(request.getUsername(), request.getEmail()))
            return new ResponseEntity<>("Username or email already in use", HttpStatus.BAD_REQUEST);
        User u = fromRequestToEntity(request);

        Optional<Authority> a = authorityService.findByAuthorityName("ROLE_READER");
        if(!a.isPresent())
            return new ResponseEntity<>("Something went wrong during registration", HttpStatus.UNPROCESSABLE_ENTITY);
        u.getAuthorities().add(a.get());
        u.setEnabled(true);
        save(u);
        return new ResponseEntity<>("Signup successfully completed", HttpStatus.OK);
    }

    // Utente non abilitato all'atto della registrazione ma con mail di conferma
    /*
    public ResponseEntity<?> signup(SignUpRequest request){
        if(existsByUsernameOrEmail(request.getUsername(), request.getEmail()))
            return new ResponseEntity<>("Username or email already in use", HttpStatus.BAD_REQUEST);
        User u = fromRequestToEntity(request);

        Optional<Authority> a = authorityService.findByAuthorityName("ROLE_GUEST");
        if(!a.isPresent())
            return new ResponseEntity<>("Something went wrong during registration", HttpStatus.UNPROCESSABLE_ENTITY);
        u.getAuthorities().add(a.get());
        u.setConfirmCode(UUID.randomUUID().toString());
        save(u);
        mailService.sendMail(new Mail(from, request.getEmail(), "MyBlog: Please confirm your registration", "http://localhost:8081/auth/confirm/"+u.getConfirmCode()));
        return new ResponseEntity<>("Signup successfully completed", HttpStatus.OK);
    }
    */


    public ResponseEntity<?> confirmRegistration(String confirmCode) {
        // query sul db per confirmCode
        User u = userRepository.findByConfirmCode(confirmCode).orElseThrow(
                () -> new ResourceNotFoundException("User", "confirmCode", confirmCode)
        );
        // se lo trovo abilito user, cambio ruolo in ROLE_READER, annullo confirmCode e salvo
        u.setEnabled(true);
        u.setConfirmCode(null);
        Optional<Authority> a = authorityService.findByAuthorityName("ROLE_READER");
        if(!a.isPresent())
            return new ResponseEntity<>("Something went wrong during confirm registration", HttpStatus.UNPROCESSABLE_ENTITY);
        u.setAuthorities(Collections.singleton(a.get()));
        userRepository.save(u);
        mailService.sendMail(new Mail(from, u.getEmail(), "MyBlog: Thank you for registration", "bla bla blb bla bla"));
        return new ResponseEntity<>("Signup successfully completed", HttpStatus.OK);
    }

    public ResponseEntity<?> signin(SignInRequest request) {
        // verificare esistenza user e correttezza password
        User u = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(
                        () -> new ResourceNotFoundException("User", "confirmCode", request.getUsernameOrEmail())
                );
        //VERIFICA SE bannedUntil è valorizzato e se la data è scaduta o meno in modo da deciedere s el'utente può essere riabilitato
        if (u.getBannedUntil() != null)
            if (u.getBannedUntil().isBefore(LocalDateTime.now())){
                u.setEnabled(true);
                u.setBannedUntil(null);
                userRepository.save(u);
            } else {
                return new ResponseEntity<>("You are banned until" , HttpStatus.UNAUTHORIZED);
            }
        if(!passwordEncoder.matches(request.getPassword(), u.getPassword()))
            return new ResponseEntity("Wrong username or password", HttpStatus.FORBIDDEN);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // generare response con JWT
        String jwt = JwtTokenProvider.generateToken(authentication);
        JwtAuthenticationResponse currentUser = UserPrincipal.createJwtAuthenticationResponseFromUserPrincipal((UserPrincipal)authentication.getPrincipal(), jwt);
        return new ResponseEntity(currentUser, HttpStatus.OK);

    }

    public ResponseEntity<?> changeRole(ChangeRoleRequest request, UserPrincipal principal){
        if(request.getId() == principal.getId())
            return new ResponseEntity<>("You cannot change your own authorities", HttpStatus.FORBIDDEN);
        Set<Authority> authorities = authorityService.findByAuthorityNameIn(request.getNewAuthorities());
        if(authorities.isEmpty())
            return new ResponseEntity("No valid authority selected", HttpStatus.BAD_REQUEST);
        Optional<User> u = userRepository.findById(request.getId());
        if(u.isEmpty())
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        u.get().setAuthorities(authorities);
        userRepository.save(u.get());
        return new ResponseEntity("Roles updated successfully", HttpStatus.OK);
    }

    public ResponseEntity<?> changePwd(ChangePwdRequest request, UserPrincipal principal){
        if(!request.getNewPassword1().equals(request.getNewPassword2()))
            return new ResponseEntity("Password mismatch", HttpStatus.BAD_REQUEST);
        if(!passwordEncoder.matches(request.getOldPassword(), principal.getPassword()))
            return new ResponseEntity("Wrong old password", HttpStatus.BAD_REQUEST);
        if(passwordEncoder.matches(request.getNewPassword1(), principal.getPassword()))
            return new ResponseEntity("New password and old password are equals", HttpStatus.BAD_REQUEST);
        userRepository.changePwd(principal.getId(), passwordEncoder.encode(request.getNewPassword1()), LocalDateTime.now());
        return new ResponseEntity("Password has been updated", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> updateMe(UpdateMeRequest request, UserPrincipal principal){
        Optional<User> u = userRepository.findByEmail(request.getEmail());
        Optional<User> uu = Optional.empty();
        if(u.isPresent()) {
            if (u.get().getEmail().equals(request.getEmail()) && principal.getId() != u.get().getId())
                return new ResponseEntity("Email already in use", HttpStatus.FORBIDDEN);
            u.get().setBio(request.getBio());
        } else {
            uu = userRepository.findById(principal.getId());
            uu.get().setBio(request.getBio());
            uu.get().setEmail(request.getEmail());
        }
        return new ResponseEntity("User info has been updated", HttpStatus.OK);
    }

    public ResponseEntity<?> getMe(UserPrincipal principal) {
       return new ResponseEntity(userRepository.getMe(principal.getId()), HttpStatus.OK);
    }

    public ResponseEntity<?> getAllAuthors(String authorityName){
        return new ResponseEntity(userRepository.getAllAuthors(authorityName, LocalDateTime.now()), HttpStatus.OK);
    }
}
