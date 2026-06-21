package br.edu.ifsp.chamados.service;

import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.enums.Role;
import br.edu.ifsp.chamados.enums.TipoManutencao;
import br.edu.ifsp.chamados.repository.IncidenteRepository;
import br.edu.ifsp.chamados.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtribuicaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private IncidenteRepository incidenteRepository;

    @InjectMocks
    private AtribuicaoService atribuicaoService;

    @Test
    void deveEscolherTecnicoDoMesmoTipoComMenorCargaAberta() {
        Usuario tecnicoComCargaAlta = Usuario.builder()
                .id(1L)
                .nome("Tecnico A")
                .email("a@ifsp.edu.br")
                .senha("hash")
                .role(Role.MANUTENCAO)
                .tipo(TipoManutencao.ELETRICA)
                .build();
        Usuario tecnicoComCargaBaixa = Usuario.builder()
                .id(2L)
                .nome("Tecnico B")
                .email("b@ifsp.edu.br")
                .senha("hash")
                .role(Role.MANUTENCAO)
                .tipo(TipoManutencao.ELETRICA)
                .build();

        when(usuarioRepository.findByRoleAndTipo(Role.MANUTENCAO, TipoManutencao.ELETRICA))
                .thenReturn(List.of(tecnicoComCargaAlta, tecnicoComCargaBaixa));
        when(incidenteRepository.countAbertosporResponsavel(1L)).thenReturn(4L);
        when(incidenteRepository.countAbertosporResponsavel(2L)).thenReturn(1L);

        Usuario escolhido = atribuicaoService.escolherResponsavel(TipoManutencao.ELETRICA);

        assertThat(escolhido).isSameAs(tecnicoComCargaBaixa);
    }

    @Test
    void deveFalharQuandoNaoExisteTecnicoCompativel() {
        when(usuarioRepository.findByRoleAndTipo(Role.MANUTENCAO, TipoManutencao.HIDRAULICA))
                .thenReturn(List.of());

        assertThatThrownBy(() -> atribuicaoService.escolherResponsavel(TipoManutencao.HIDRAULICA))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Nao ha tecnico disponivel");
    }
}
