package br.edu.ifsp.chamados.repository;

import br.edu.ifsp.chamados.entity.VerificacaoEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificacaoEmailRepository extends JpaRepository<VerificacaoEmail, Long> {
    Optional<VerificacaoEmail> findByEmail(String email);
    void deleteByEmail(String email);
}
