package br.edu.ifsp.chamados.controller;

import br.edu.ifsp.chamados.entity.Incidente;
import br.edu.ifsp.chamados.enums.BlocoLocal;
import br.edu.ifsp.chamados.enums.Role;
import br.edu.ifsp.chamados.enums.StatusIncidente;
import br.edu.ifsp.chamados.repository.UsuarioRepository;
import br.edu.ifsp.chamados.service.IncidenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Controller
@RequestMapping("/manutencao")
@RequiredArgsConstructor
public class ManutencaoController {

    private final IncidenteService incidenteService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public String listar(Model model) {
        var todos = incidenteService.listarTodos();

        long qtdCriados    = todos.stream().filter(i -> i.getStatus() == StatusIncidente.CRIADO).count();
        long qtdEmAnalise  = todos.stream().filter(i -> i.getStatus() == StatusIncidente.EM_ANALISE).count();
        long qtdConcluidos = todos.stream().filter(i -> i.getStatus() == StatusIncidente.CONCLUIDO).count();

        model.addAttribute("incidentes",    todos);
        model.addAttribute("qtdCriados",    qtdCriados);
        model.addAttribute("qtdEmAnalise",  qtdEmAnalise);
        model.addAttribute("qtdConcluidos", qtdConcluidos);
        model.addAttribute("blocos",        BlocoLocal.values());
        model.addAttribute("tecnicos",      usuarioRepository.findByRole(Role.MANUTENCAO));
        return "manutencao/lista";
    }

    @PostMapping("/em-analise/{id}")
    public String emAnalise(@PathVariable Long id) {
        incidenteService.atualizarStatus(id, StatusIncidente.EM_ANALISE);
        return "redirect:/manutencao";
    }

    /** Conclusão agora exige solução obrigatória e aceita foto de evidência. */
    @PostMapping("/concluir/{id}")
    public String concluir(@PathVariable Long id,
                           @RequestParam String solucaoAplicada,
                           @RequestParam(required = false) MultipartFile evidencia) {
        Incidente inc = incidenteService.buscarPorId(id);

        // Appenda a solução à observação
        String solucaoBloco = "\n\n✅ [SOLUÇÃO APLICADA] " + solucaoAplicada.trim();
        inc.setObservacao(inc.getObservacao() + solucaoBloco);

        // Foto de evidência (substitui o anexo original pelo da conclusão se enviada)
        if (evidencia != null && !evidencia.isEmpty()) {
            try {
                String mime = evidencia.getContentType() != null
                        ? evidencia.getContentType() : "image/jpeg";
                String b64 = Base64.getEncoder().encodeToString(evidencia.getBytes());
                inc.setAnexo("data:" + mime + ";base64," + b64);
            } catch (Exception ignored) {}
        }

        inc.setStatus(StatusIncidente.CONCLUIDO);
        incidenteService.salvarDireto(inc);
        return "redirect:/manutencao";
    }

    @GetMapping("/chamado/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("incidente", incidenteService.buscarPorId(id));
        model.addAttribute("statuses",  StatusIncidente.values());
        return "manutencao/detalhe";
    }

    @PostMapping("/chamado/{id}/atualizar")
    public String atualizar(@PathVariable Long id,
                            @RequestParam StatusIncidente status,
                            @RequestParam(required = false) String observacaoTecnica) {
        var inc = incidenteService.buscarPorId(id);
        inc.setStatus(status);
        if (observacaoTecnica != null && !observacaoTecnica.isBlank()) {
            inc.setObservacao(inc.getObservacao() + "\n\n[MANUTENÇÃO] " + observacaoTecnica);
        }
        incidenteService.salvarDireto(inc);
        return "redirect:/manutencao";
    }
}
