package br.edu.ifsp.chamados.controller;

import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.enums.BlocoLocal;
import br.edu.ifsp.chamados.enums.CategoriaIncidente;
import br.edu.ifsp.chamados.enums.LocalEspecifico;
import br.edu.ifsp.chamados.enums.TipoManutencao;
import br.edu.ifsp.chamados.repository.UsuarioRepository;
import br.edu.ifsp.chamados.service.AtribuicaoService;
import br.edu.ifsp.chamados.service.IncidenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/incidente")
@RequiredArgsConstructor
public class IncidenteController {

    private final IncidenteService incidenteService;
    private final UsuarioRepository usuarioRepository;
    private final AtribuicaoService atribuicaoService;

    @GetMapping("/novo")
    public String novoIncidente(Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        preencherOpcoesFormulario(model);
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
                                 @RequestParam TipoManutencao tipo,
                                 @RequestParam(required = false, defaultValue = "MANUTENCAO") CategoriaIncidente categoria,
                                 @RequestParam(required = false) MultipartFile anexo,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            Usuario responsavel = atribuicaoService.escolherResponsavel(tipo);
            incidenteService.criar(observacao, bloco, localEspecifico, categoria, tipo, anexo, usuario, responsavel);
            model.addAttribute("sucesso", "Chamado enviado com sucesso!");
            model.addAttribute("nomeUsuario", usuario.getNome());
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao enviar chamado: " + e.getMessage());
            if (userDetails != null) {
                usuarioRepository.findByEmail(userDetails.getUsername())
                        .ifPresent(u -> model.addAttribute("nomeUsuario", u.getNome()));
            }
        }
        preencherOpcoesFormulario(model);
        return "incidente/novo";
    }

    private void preencherOpcoesFormulario(Model model) {
        model.addAttribute("blocos", BlocoLocal.values());
        model.addAttribute("todosLocais", LocalEspecifico.values());
        model.addAttribute("categorias", CategoriaIncidente.values());
        model.addAttribute("tipos", TipoManutencao.values());
    }
}
