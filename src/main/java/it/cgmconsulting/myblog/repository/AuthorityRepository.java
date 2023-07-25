package it.cgmconsulting.myblog.repository;

import java.util.Optional;
import java.util.Set;

import it.cgmconsulting.myblog.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long>{

    Optional<Authority> findByAuthorityName(String authorityName);

    Set<Authority> findByAuthorityNameIn(Set<String> authorities); // select * from authority where authority_name IN ('role_reader', 'role_writer', 'etc..' );

}
