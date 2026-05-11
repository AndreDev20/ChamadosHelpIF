package br.edu.ifsp.chamados.controller;

import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.enums.Role;
import br.edu.ifsp.chamados.service.AuthService;
import jakarta.servlet.http.Cookie;
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

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        HttpServletResponse response,
                        Model model) {
        try {
            String token = authService.autenticar(email, senha);

            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(86400); // 1 dia
            response.addCookie(cookie);

            // Redireciona baseado na role
            Usuario usuario = authService.buscarPorEmail(email);
            return switch (usuario.getRole()) {
                case ADMIN -> "redirect:/admin";
                case MANUTENCAO -> "redirect:/manutencao";
                case COMUM -> "redirect:/incidente/novo";
            };

        } catch (Exception e) {
            model.addAttribute("erro", "E-mail ou senha inválidos.");
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/auth/login";
    }
}
