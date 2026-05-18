package br.edu.ifsp.chamados.repository;

import br.edu.ifsp.chamados.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    /** Retorna todos os usuários ordenados por ID — query explícita para garantir compatibilidade com qualquer dialeto. */
    @Query("SELECT u FROM Usuario u ORDER BY u.id ASC")
    List<Usuario> findAllOrdered();
}
