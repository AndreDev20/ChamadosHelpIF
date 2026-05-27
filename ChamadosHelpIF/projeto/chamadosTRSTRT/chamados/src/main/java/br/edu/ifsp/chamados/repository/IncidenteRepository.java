package br.edu.ifsp.chamados.repository;

import br.edu.ifsp.chamados.entity.Incidente;
import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.enums.StatusIncidente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncidenteRepository extends JpaRepository<Incidente, Long> {

    // ── JOIN FETCH carrega o usuário junto, evitando LazyInitializationException ──

    @Query(value = "SELECT i.* FROM incidentes i ORDER BY i.datetime DESC", nativeQuery = true)
    List<Incidente> findAllNative();

    @Query("SELECT i FROM Incidente i LEFT JOIN FETCH i.usuario LEFT JOIN FETCH i.responsavel ORDER BY i.datetime DESC")
    List<Incidente> findAllWithUsuario();

    @Query("SELECT i FROM Incidente i LEFT JOIN FETCH i.usuario LEFT JOIN FETCH i.responsavel WHERE i.usuario = :usuario ORDER BY i.datetime DESC")
    List<Incidente> findByUsuarioWithUsuario(Usuario usuario);

    @Query("SELECT i FROM Incidente i LEFT JOIN FETCH i.usuario LEFT JOIN FETCH i.responsavel WHERE i.status = :status ORDER BY i.datetime DESC")
    List<Incidente> findByStatusWithUsuario(StatusIncidente status);

    @Query("SELECT i FROM Incidente i LEFT JOIN FETCH i.usuario LEFT JOIN FETCH i.responsavel WHERE i.id = :id")
    Optional<Incidente> findByIdWithUsuario(Long id);

    // ── mantém os originais para compatibilidade ──
    List<Incidente> findByUsuario(Usuario usuario);
    List<Incidente> findByStatus(StatusIncidente status);
    List<Incidente> findAllByOrderByDatetimeDesc();

    /** Conta chamados não concluídos de um responsável (para balanceamento de carga). */
    @Query(value = "SELECT COUNT(*) FROM incidentes WHERE responsavel_id = :responsavelId AND status != 'CONCLUIDO'", nativeQuery = true)
    long countAbertosporResponsavel(@Param("responsavelId") Long responsavelId);
}
