package br.edu.ifsp.chamados.repository;

import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.enums.Role;
import br.edu.ifsp.chamados.enums.TipoManutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    /** Query SQL nativa — ignora cache Hibernate, dialeto e qualquer proxy issue. */
    @Query(value = "SELECT * FROM usuarios ORDER BY id ASC", nativeQuery = true)
    List<Usuario> findAllOrdered();

    /** Retorna todos os usuários de um determinado perfil (ex: MANUTENCAO). */
    @Query(value = "SELECT * FROM usuarios WHERE role = :#{#role.name()} ORDER BY nome ASC", nativeQuery = true)
    List<Usuario> findByRole(Role role);

    @Query(value = "SELECT * FROM usuarios WHERE role = :#{#role.name()} AND tipo = :#{#tipo.name()} ORDER BY nome ASC", nativeQuery = true)
    List<Usuario> findByRoleAndTipo(Role role, TipoManutencao tipo);
}
