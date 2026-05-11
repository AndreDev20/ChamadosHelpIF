package br.edu.ifsp.chamados.config;

import br.edu.ifsp.chamados.enums.Role;
import br.edu.ifsp.chamados.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioService usuarioService;

    @Override
    public void run(String... args) {
        try {
            usuarioService.criar("Administrador", "admin@ifsp.edu.br", "admin123", Role.ADMIN);
            log.info("=== Usuário admin criado: admin@ifsp.edu.br / admin123 ===");
        } catch (Exception e) {
            log.info("=== Usuário admin já existe, pulando criação ===");
        }
    }
}
