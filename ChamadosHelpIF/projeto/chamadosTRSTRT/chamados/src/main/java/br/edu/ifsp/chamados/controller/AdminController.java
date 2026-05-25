package br.edu.ifsp.chamados.controller;

import br.edu.ifsp.chamados.enums.BlocoLocal;
import br.edu.ifsp.chamados.enums.CategoriaIncidente;
import br.edu.ifsp.chamados.enums.LocalEspecifico;
import br.edu.ifsp.chamados.enums.Role;
import br.edu.ifsp.chamados.enums.StatusIncidente;
import br.edu.ifsp.chamados.entity.Incidente;
import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.service.IncidenteService;
import br.edu.ifsp.chamados.service.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IncidenteService incidenteService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String painel(Model model) {
        List<Incidente> incidentes = incidenteService.listarTodos();
        if (incidentes == null) incidentes = new ArrayList<>();

        long qtdCriados    = incidentes.stream().filter(i -> i.getStatus() == StatusIncidente.CRIADO).count();
        long qtdEmAnalise  = incidentes.stream().filter(i -> i.getStatus() == StatusIncidente.EM_ANALISE).count();
        long qtdConcluidos = incidentes.stream().filter(i -> i.getStatus() == StatusIncidente.CONCLUIDO).count();

        List<Usuario> usuarios = usuarioService.listarTodos();
        if (usuarios == null) usuarios = new ArrayList<>();

        log.debug("Painel admin — incidentes: {}, usuarios: {}", incidentes.size(), usuarios.size());

        model.addAttribute("incidentes",    incidentes);
        model.addAttribute("usuarios",      usuarios);
        model.addAttribute("qtdCriados",    qtdCriados);
        model.addAttribute("qtdEmAnalise",  qtdEmAnalise);
        model.addAttribute("qtdConcluidos", qtdConcluidos);

        return "admin/painel";
    }

    @GetMapping("/incidente/editar/{id}")
    public String editarIncidente(@PathVariable Long id, Model model) throws JsonProcessingException {
        model.addAttribute("incidente",   incidenteService.buscarPorId(id));
        model.addAttribute("blocos",       BlocoLocal.values());
        model.addAttribute("todosLocais",  LocalEspecifico.values());
        model.addAttribute("statuses",     StatusIncidente.values());
        model.addAttribute("categorias",   CategoriaIncidente.values());
        return "admin/editar-incidente";
    }

    @PostMapping("/incidente/editar/{id}")
    public String salvarIncidente(@PathVariable Long id,
                                  @RequestParam String observacao,
                                  @RequestParam BlocoLocal bloco,
                                  @RequestParam LocalEspecifico localEspecifico,
                                  @RequestParam CategoriaIncidente categoria,
                                  @RequestParam StatusIncidente status) {
        incidenteService.atualizar(id, observacao, bloco, localEspecifico, categoria, status);
        return "redirect:/admin";
    }

    @PostMapping("/incidente/excluir/{id}")
    public String excluirIncidente(@PathVariable Long id) {
        incidenteService.deletar(id);
        return "redirect:/admin";
    }

    @GetMapping("/usuario/novo")
    public String novoUsuario(Model model) {
        model.addAttribute("roles", Role.values());
        return "admin/novo-usuario";
    }

    @PostMapping("/usuario/novo")
    public String criarUsuario(@RequestParam String nome, @RequestParam String email,
                               @RequestParam String senha, @RequestParam Role role, Model model) {
        try {
            usuarioService.criar(nome, email, senha, role);
            return "redirect:/admin";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("roles", Role.values());
            return "admin/novo-usuario";
        }
    }

    @GetMapping("/usuario/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        model.addAttribute("roles",   Role.values());
        return "admin/editar-usuario";
    }

    @PostMapping("/usuario/editar/{id}")
    public String salvarUsuario(@PathVariable Long id, @RequestParam String nome,
                                @RequestParam String email, @RequestParam Role role) {
        usuarioService.atualizar(id, nome, email, role);
        return "redirect:/admin";
    }

    @PostMapping("/usuario/excluir/{id}")
    public String excluirUsuario(@PathVariable Long id) {
        usuarioService.deletar(id);
        return "redirect:/admin";
    }

    private String buildLocaisPorBlocoJson() throws JsonProcessingException {
        Map<String, List<Map<String, String>>> map = new LinkedHashMap<>();
        for (BlocoLocal bloco : BlocoLocal.values()) {
            List<Map<String, String>> locais = Arrays.stream(LocalEspecifico.values())
                    .filter(l -> l.getBloco() == bloco)
                    .map(l -> {
                        Map<String, String> entry = new LinkedHashMap<>();
                        entry.put("name", l.name());
                        entry.put("descricao", l.getDescricao());
                        return entry;
                    })
                    .collect(Collectors.toList());
            map.put(bloco.name(), locais);
        }
        return new ObjectMapper().writeValueAsString(map);
    }
}
