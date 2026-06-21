package br.edu.ifsp.chamados.service;

import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.enums.BlocoLocal;
import br.edu.ifsp.chamados.enums.CategoriaIncidente;
import br.edu.ifsp.chamados.enums.LocalEspecifico;
import br.edu.ifsp.chamados.enums.Role;
import br.edu.ifsp.chamados.repository.IncidenteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class IncidenteServiceTest {

    @Mock
    private IncidenteRepository incidenteRepository;

    @InjectMocks
    private IncidenteService incidenteService;

    @Test
    void deveExigirTipoParaCriarIncidente() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Aluno")
                .email("aluno@ifsp.edu.br")
                .senha("hash")
                .role(Role.COMUM)
                .build();

        assertThatThrownBy(() -> incidenteService.criar(
                "Problema",
                BlocoLocal.TERREO_BLOCO_PEDAGOGICO,
                LocalEspecifico.BP_CANTINA,
                CategoriaIncidente.MANUTENCAO,
                null,
                null,
                usuario,
                null
        ))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("tipo de manutencao");
    }
}
