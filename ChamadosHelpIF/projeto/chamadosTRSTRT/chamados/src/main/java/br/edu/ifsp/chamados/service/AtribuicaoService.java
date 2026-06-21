package br.edu.ifsp.chamados.service;

import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.enums.Role;
import br.edu.ifsp.chamados.enums.TipoManutencao;
import br.edu.ifsp.chamados.repository.IncidenteRepository;
import br.edu.ifsp.chamados.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtribuicaoService {

    private final UsuarioRepository usuarioRepository;
    private final IncidenteRepository incidenteRepository;

    @Transactional(readOnly = true)
    public Usuario escolherResponsavel(TipoManutencao tipo) {
        if (tipo == null) {
            throw new RuntimeException("Selecione o tipo de manutencao do chamado.");
        }

        List<Usuario> tecnicos = usuarioRepository.findByRoleAndTipo(Role.MANUTENCAO, tipo);

        if (tecnicos.isEmpty()) {
            log.warn("Nenhum tecnico de manutencao cadastrado para o tipo {}.", tipo);
            throw new RuntimeException(
                    "Nao ha tecnico disponivel para o tipo selecionado: " + tipo.getDescricao() + "."
            );
        }

        return tecnicos.stream()
                .min((a, b) -> {
                    long cargaA = incidenteRepository.countAbertosporResponsavel(a.getId());
                    long cargaB = incidenteRepository.countAbertosporResponsavel(b.getId());
                    return Long.compare(cargaA, cargaB);
                })
                .orElse(tecnicos.get(0));
    }
}
