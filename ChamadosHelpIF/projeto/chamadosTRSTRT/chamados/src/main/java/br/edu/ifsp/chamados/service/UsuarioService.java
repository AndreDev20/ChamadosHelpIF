package br.edu.ifsp.chamados.service;

import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.enums.Role;
import br.edu.ifsp.chamados.enums.TipoManutencao;
import br.edu.ifsp.chamados.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario criar(String nome, String email, String senha, Role role) {
        return criar(nome, email, senha, role, null);
    }

    @Transactional
    public Usuario criar(String nome, String email, String senha, Role role, TipoManutencao tipo) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("E-mail ja cadastrado: " + email);
        }

        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha(passwordEncoder.encode(senha))
                .role(role)
                .tipo(normalizarTipo(role, tipo))
                .build();
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado: " + id));
    }

    @Transactional
    public Usuario atualizar(Long id, String nome, String email, Role role) {
        return atualizar(id, nome, email, role, null);
    }

    @Transactional
    public Usuario atualizar(Long id, String nome, String email, Role role, TipoManutencao tipo) {
        Usuario usuario = buscarPorId(id);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setRole(role);
        usuario.setTipo(normalizarTipo(role, tipo));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }

    private TipoManutencao normalizarTipo(Role role, TipoManutencao tipo) {
        if (role == Role.MANUTENCAO) {
            if (tipo == null) {
                throw new RuntimeException("Selecione o tipo do usuario de manutencao.");
            }
            return tipo;
        }
        return null;
    }
}
