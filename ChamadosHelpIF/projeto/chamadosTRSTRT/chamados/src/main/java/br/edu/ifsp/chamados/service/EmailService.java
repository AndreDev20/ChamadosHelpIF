package br.edu.ifsp.chamados.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Envia e-mails via API HTTP do Brevo (sem SMTP).
 * O Render bloqueia portas SMTP (587/465), mas permite chamadas HTTP normais.
 */
@Slf4j
@Service
public class EmailService {

    @Value("${BREVO_API_KEY:placeholder}")
    private String apiKey;

    @Value("${BREVO_SENDER_EMAIL:noreply@ifsp.edu.br}")
    private String senderEmail;

    @Value("${BREVO_SENDER_NAME:HelpIF}")
    private String senderName;

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public void enviarCodigoVerificacao(String destinatario, String nome, String codigo) {
        String body = """
                {
                  "sender": { "name": "%s", "email": "%s" },
                  "to": [{ "email": "%s", "name": "%s" }],
                  "subject": "HelpIF — Seu código de verificação",
                  "textContent": "Olá, %s!\\n\\nSeu código de verificação para o sistema HelpIF é:\\n\\n    %s\\n\\nEste código expira em 15 minutos.\\n\\nCaso não tenha solicitado o cadastro, ignore este e-mail.\\n\\n— Equipe HelpIF / IFSP"
                }
                """.formatted(senderName, senderEmail, destinatario, nome, nome, codigo);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("Content-Type", "application/json")
                    .header("api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                log.info("Código de verificação enviado para {} via API Brevo", destinatario);
            } else {
                log.error("Brevo API retornou status {}: {}", response.statusCode(), response.body());
                throw new RuntimeException("Falha ao enviar e-mail (status " + response.statusCode() + ").");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao chamar API Brevo para {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Não foi possível enviar o e-mail de verificação. Tente novamente.");
        }
    }
}
