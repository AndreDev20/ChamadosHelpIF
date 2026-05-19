package br.edu.ifsp.chamados.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String remetente;

    public void enviarCodigoVerificacao(String destinatario, String nome, String codigo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remetente);
            message.setTo(destinatario);
            message.setSubject("HelpIF — Seu código de verificação");
            message.setText(
                "Olá, " + nome + "!\n\n" +
                "Seu código de verificação para o sistema HelpIF é:\n\n" +
                "    " + codigo + "\n\n" +
                "Este código expira em 15 minutos.\n\n" +
                "Caso não tenha solicitado o cadastro, ignore este e-mail.\n\n" +
                "— Equipe HelpIF / IFSP"
            );
            mailSender.send(message);
            log.info("Código de verificação enviado para {}", destinatario);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Não foi possível enviar o e-mail de verificação. Tente novamente.");
        }
    }
}
