package br.edu.ifsp.chamados.controller;

import br.edu.ifsp.chamados.enums.StatusIncidente;
import br.edu.ifsp.chamados.service.IncidenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manutencao")
@RequiredArgsConstructor
public class ManutencaoController {

    private final IncidenteService incidenteService;

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
        return "manutencao/lista";
    }

    @PostMapping("/em-analise/{id}")
    public String emAnalise(@PathVariable Long id) {
        incidenteService.atualizarStatus(id, StatusIncidente.EM_ANALISE);
        return "redirect:/manutencao";
    }

    @PostMapping("/concluir/{id}")
    public String concluir(@PathVariable Long id) {
        incidenteService.atualizarStatus(id, StatusIncidente.CONCLUIDO);
        return "redirect:/manutencao";
    }

    /** Detalhe de um chamado específico (para registro técnico) */
    @GetMapping("/chamado/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("incidente", incidenteService.buscarPorId(id));
        model.addAttribute("statuses",  StatusIncidente.values());
        return "manutencao/detalhe";
    }

    /** Atualiza status e observação técnica */
    @PostMapping("/chamado/{id}/atualizar")
    public String atualizar(@PathVariable Long id,
                            @RequestParam StatusIncidente status,
                            @RequestParam(required = false) String observacaoTecnica) {
        var inc = incidenteService.buscarPorId(id);
        inc.setStatus(status);
        if (observacaoTecnica != null && !observacaoTecnica.isBlank()) {
            String atual = inc.getObservacao();
            inc.setObservacao(atual + "\n\n[MANUTENÇÃO] " + observacaoTecnica);
        }
        incidenteService.salvarDireto(inc);
        return "redirect:/manutencao";
    }
}
