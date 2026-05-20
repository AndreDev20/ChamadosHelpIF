package br.edu.ifsp.chamados.repository;

import br.edu.ifsp.chamados.entity.Incidente;
import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.enums.StatusIncidente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncidenteRepository extends JpaRepository<Incidente, Long> {

    // ── JOIN FETCH carrega o usuário junto, evitando LazyInitializationException ──

    @Query(value = "SELECT i.* FROM incidentes i ORDER BY i.datetime DESC", nativeQuery = true)
    List<Incidente> findAllNative();

    @Query("SELECT i FROM Incidente i JOIN FETCH i.usuario ORDER BY i.datetime DESC")
    List<Incidente> findAllWithUsuario();

    @Query("SELECT i FROM Incidente i JOIN FETCH i.usuario WHERE i.usuario = :usuario ORDER BY i.datetime DESC")
    List<Incidente> findByUsuarioWithUsuario(Usuario usuario);

    @Query("SELECT i FROM Incidente i JOIN FETCH i.usuario WHERE i.status = :status ORDER BY i.datetime DESC")
    List<Incidente> findByStatusWithUsuario(StatusIncidente status);

    @Query("SELECT i FROM Incidente i JOIN FETCH i.usuario WHERE i.id = :id")
    Optional<Incidente> findByIdWithUsuario(Long id);

    // ── mantém os originais para compatibilidade ──
    List<Incidente> findByUsuario(Usuario usuario);
    List<Incidente> findByStatus(StatusIncidente status);
    List<Incidente> findAllByOrderByDatetimeDesc();
}
