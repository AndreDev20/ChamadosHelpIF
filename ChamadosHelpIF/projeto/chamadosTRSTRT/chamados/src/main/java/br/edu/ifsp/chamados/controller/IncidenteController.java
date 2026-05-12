package br.edu.ifsp.chamados.controller;

import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.enums.BlocoLocal;
import br.edu.ifsp.chamados.enums.CategoriaIncidente;
import br.edu.ifsp.chamados.enums.LocalEspecifico;
import br.edu.ifsp.chamados.repository.UsuarioRepository;
import br.edu.ifsp.chamados.service.IncidenteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/incidente")
@RequiredArgsConstructor
public class IncidenteController {

    private final IncidenteService incidenteService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/novo")
    public String novoIncidente(Model model,
                                @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
        model.addAttribute("blocos", BlocoLocal.values());
        model.addAttribute("locaisPorBloco", buildLocaisPorBlocoJson());
        model.addAttribute("categorias", CategoriaIncidente.values());
        // Passa o nome do usuário para o template sem depender de #authentication
        if (userDetails != null) {
            usuarioRepository.findByEmail(userDetails.getUsername())
                    .ifPresent(u -> model.addAttribute("nomeUsuario", u.getNome()));
        }
        return "incidente/novo";
    }

    @PostMapping("/novo")
    public String criarIncidente(@RequestParam String observacao,
                                 @RequestParam BlocoLocal bloco,
                                 @RequestParam LocalEspecifico localEspecifico,
                                 @RequestParam CategoriaIncidente categoria,
                                 @RequestParam(required = false) MultipartFile anexo,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            incidenteService.criar(observacao, bloco, localEspecifico, categoria, anexo, usuario);
            model.addAttribute("sucesso", "Chamado enviado com sucesso!");
            model.addAttribute("nomeUsuario", usuario.getNome());
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao enviar chamado: " + e.getMessage());
            if (userDetails != null) {
                usuarioRepository.findByEmail(userDetails.getUsername())
                        .ifPresent(u -> model.addAttribute("nomeUsuario", u.getNome()));
            }
        }
        try {
            model.addAttribute("blocos", BlocoLocal.values());
            model.addAttribute("locaisPorBloco", buildLocaisPorBlocoJson());
            model.addAttribute("categorias", CategoriaIncidente.values());
        } catch (JsonProcessingException ignored) {}
        return "incidente/novo";
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
