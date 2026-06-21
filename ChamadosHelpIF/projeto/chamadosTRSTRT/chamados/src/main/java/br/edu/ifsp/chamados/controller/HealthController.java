package br.edu.ifsp.chamados.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint simples usado por um serviço externo de ping (ex: UptimeRobot, cron-job.org)
 * para evitar que o app hiberne no plano free do Render.
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
