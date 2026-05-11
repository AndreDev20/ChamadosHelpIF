package br.edu.ifsp.chamados.service;

import br.edu.ifsp.chamados.entity.Incidente;
import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.enums.BlocoLocal;
import br.edu.ifsp.chamados.enums.CategoriaIncidente;
import br.edu.ifsp.chamados.enums.LocalEspecifico;
import br.edu.ifsp.chamados.enums.StatusIncidente;
import br.edu.ifsp.chamados.repository.IncidenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;

    @Transactional
    public Incidente criar(String observacao, BlocoLocal bloco, LocalEspecifico localEspecifico,
                           CategoriaIncidente categoria, MultipartFile anexo, Usuario usuario) throws IOException {
        Incidente incidente = Incidente.builder()
                .observacao(observacao)
                .bloco(bloco)
                .localEspecifico(localEspecifico)
                .categoria(categoria)
                .usuario(usuario)
                .status(StatusIncidente.CRIADO)
                .build();

        if (anexo != null && !anexo.isEmpty()) {
            String base64 = Base64.getEncoder().encodeToString(anexo.getBytes());
            String mimeType = anexo.getContentType();
            incidente.setAnexo("data:" + mimeType + ";base64," + base64);
        }

        return incidenteRepository.save(incidente);
    }

    /** Carrega todos os chamados com usuário em memória (JOIN FETCH — evita LazyInitializationException). */
    public List<Incidente> listarTodos() {
        return incidenteRepository.findAllWithUsuario();
    }

    public List<Incidente> listarPorUsuario(Usuario usuario) {
        return incidenteRepository.findByUsuarioWithUsuario(usuario);
    }

    public Incidente buscarPorId(Long id) {
        return incidenteRepository.findByIdWithUsuario(id)
                .orElseThrow(() -> new RuntimeException("Incidente não encontrado: " + id));
    }

    @Transactional
    public Incidente atualizarStatus(Long id, StatusIncidente novoStatus) {
        Incidente incidente = buscarPorId(id);
        incidente.setStatus(novoStatus);
        return incidenteRepository.save(incidente);
    }

    /** Salva qualquer alteração feita diretamente no objeto (usado pelo ManutencaoController). */
    @Transactional
    public Incidente salvarDireto(Incidente incidente) {
        return incidenteRepository.save(incidente);
    }

    @Transactional
    public void deletar(Long id) {
        incidenteRepository.deleteById(id);
    }

    @Transactional
    public Incidente atualizar(Long id, String observacao, BlocoLocal bloco,
                               LocalEspecifico localEspecifico, CategoriaIncidente categoria,
                               StatusIncidente status) {
        Incidente incidente = buscarPorId(id);
        incidente.setObservacao(observacao);
        incidente.setBloco(bloco);
        incidente.setLocalEspecifico(localEspecifico);
        incidente.setCategoria(categoria);
        incidente.setStatus(status);
        return incidenteRepository.save(incidente);
    }
}
