package br.edu.ifsp.chamados.service;

import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.enums.Role;
import br.edu.ifsp.chamados.repository.IncidenteRepository;
import br.edu.ifsp.chamados.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Distribui chamados automaticamente entre os técnicos de manutenção
 * usando round-robin: quem tem menos chamados abertos recebe o próximo.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AtribuicaoService {

    private final UsuarioRepository usuarioRepository;
    private final IncidenteRepository incidenteRepository;

    @Transactional(readOnly = true)
    public Usuario escolherResponsavel() {
        List<Usuario> tecnicos = usuarioRepository.findByRole(Role.MANUTENCAO);

        if (tecnicos.isEmpty()) {
            log.warn("Nenhum técnico de manutenção cadastrado para atribuição automática.");
            return null;
        }

        // Escolhe o técnico com menor número de chamados abertos (não concluídos)
        return tecnicos.stream()
                .min((a, b) -> {
                    long cargaA = incidenteRepository.countAbertosporResponsavel(a.getId());
                    long cargaB = incidenteRepository.countAbertosporResponsavel(b.getId());
                    return Long.compare(cargaA, cargaB);
                })
                .orElse(tecnicos.get(0));
    }
}
