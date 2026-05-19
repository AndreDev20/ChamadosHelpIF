package br.edu.ifsp.chamados.controller;

import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.service.AuthService;
import br.edu.ifsp.chamados.service.RegistroService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RegistroService registroService;

    // ── LOGIN ─────────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {
        try {
            String token = authService.autenticar(email, senha);

            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            response.addCookie(cookie);

            request.getSession().invalidate();
            request.getSession(true);

            Usuario usuario = authService.buscarPorEmail(email);
            return switch (usuario.getRole()) {
                case ADMIN      -> "redirect:/admin";
                case MANUTENCAO -> "redirect:/manutencao";
                case COMUM      -> "redirect:/incidente/novo";
            };

        } catch (Exception e) {
            model.addAttribute("erro", "E-mail ou senha inválidos.");
            return "auth/login";
        }
    }

    // ── REGISTRO — passo 1: formulário ───────────────────────────────────────

    @GetMapping("/registro")
    public String registroPage() {
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nome,
                           @RequestParam String email,
                           @RequestParam String senha,
                           @RequestParam String confirmaSenha,
                           Model model) {
        try {
            if (!senha.equals(confirmaSenha)) {
                model.addAttribute("erro", "As senhas não coincidem.");
                return "auth/registro";
            }
            if (senha.length() < 6) {
                model.addAttribute("erro", "A senha deve ter no mínimo 6 caracteres.");
                return "auth/registro";
            }
            registroService.iniciarRegistro(nome, email, senha);
            // Passa o email para a tela de verificação via redirect não funciona bem,
            // então usamos model + forward interno
            model.addAttribute("email", email);
            return "auth/verificar";

        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "auth/registro";
        }
    }

    // ── REGISTRO — passo 2: verificação do código ─────────────────────────────

    @GetMapping("/verificar")
    public String verificarPage(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "auth/verificar";
    }

    @PostMapping("/verificar")
    public String verificar(@RequestParam String email,
                            @RequestParam String codigo,
                            Model model) {
        try {
            registroService.confirmarCodigo(email, codigo);
            model.addAttribute("sucesso", "Conta criada com sucesso! Faça login para continuar.");
            return "auth/login";

        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("email", email);
            return "auth/verificar";
        }
    }

    // ── LOGOUT ────────────────────────────────────────────────────────────────

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        try { request.getSession().invalidate(); } catch (Exception ignored) {}
        return "redirect:/auth/login";
    }
}
