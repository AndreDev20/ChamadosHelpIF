package br.edu.ifsp.chamados.service;

import br.edu.ifsp.chamados.enums.Role;
import br.edu.ifsp.chamados.enums.TipoManutencao;
import br.edu.ifsp.chamados.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveExigirTipoParaUsuarioManutencao() {
        when(usuarioRepository.existsByEmail("tec@ifsp.edu.br")).thenReturn(false);

        assertThatThrownBy(() ->
                usuarioService.criar("Tecnico", "tec@ifsp.edu.br", "123456", Role.MANUTENCAO, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("tipo do usuario de manutencao");
    }

    @Test
    void deveAceitarUsuarioComumSemTipo() {
        when(usuarioRepository.existsByEmail("aluno@ifsp.edu.br")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hash");

        usuarioService.criar("Aluno", "aluno@ifsp.edu.br", "123456", Role.COMUM, TipoManutencao.TECNOLOGIA);
    }
}
