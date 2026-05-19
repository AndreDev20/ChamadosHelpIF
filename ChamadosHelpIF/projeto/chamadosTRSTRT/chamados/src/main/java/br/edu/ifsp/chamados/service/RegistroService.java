package br.edu.ifsp.chamados.service;

import br.edu.ifsp.chamados.entity.Usuario;
import br.edu.ifsp.chamados.entity.VerificacaoEmail;
import br.edu.ifsp.chamados.enums.Role;
import br.edu.ifsp.chamados.repository.UsuarioRepository;
import br.edu.ifsp.chamados.repository.VerificacaoEmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistroService {

    private static final String[] DOMINIOS_PERMITIDOS = {
        "@ifsp.edu.br", "@aluno.ifsp.edu.br"
    };

    private final VerificacaoEmailRepository verificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /** Valida domínio, cria o registro pendente e envia o código. */
    @Transactional
    public void iniciarRegistro(String nome, String email, String senha) {
        // 1. Validar domínio
        validarDominio(email);

        // 2. Verificar se já existe conta ativa
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("Este e-mail já possui uma conta cadastrada.");
        }

        // 3. Gerar código de 6 dígitos
        String codigo = String.format("%06d", new Random().nextInt(999999));

        // 4. Salvar (ou atualizar) pendência — permite reenvio
        VerificacaoEmail pendencia = verificacaoRepository.findByEmail(email)
                .orElse(VerificacaoEmail.builder().email(email).build());

        pendencia.setNome(nome);
        pendencia.setSenhaCriptografada(passwordEncoder.encode(senha));
        pendencia.setCodigo(codigo);
        pendencia.setExpiracao(LocalDateTime.now().plusMinutes(15));
        pendencia.setConfirmado(false);
        verificacaoRepository.save(pendencia);

        // 5. Enviar e-mail
        emailService.enviarCodigoVerificacao(email, nome, codigo);
        log.info("Registro iniciado para {}", email);
    }

    /** Confirma o código e persiste o usuário como COMUM. */
    @Transactional
    public void confirmarCodigo(String email, String codigoDigitado) {
        VerificacaoEmail pendencia = verificacaoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nenhuma solicitação de cadastro encontrada para este e-mail."));

        if (pendencia.isConfirmado()) {
            throw new RuntimeException("Este código já foi utilizado.");
        }
        if (pendencia.isExpirado()) {
            throw new RuntimeException("Código expirado. Faça o cadastro novamente para receber um novo código.");
        }
        if (!pendencia.getCodigo().equals(codigoDigitado.trim())) {
            throw new RuntimeException("Código incorreto. Verifique o e-mail e tente novamente.");
        }

        // Criar usuário
        Usuario usuario = Usuario.builder()
                .nome(pendencia.getNome())
                .email(pendencia.getEmail())
                .senha(pendencia.getSenhaCriptografada())
                .role(Role.COMUM)
                .build();
        usuarioRepository.save(usuario);

        // Marcar como confirmado e limpar pendência
        pendencia.setConfirmado(true);
        verificacaoRepository.save(pendencia);

        log.info("Usuário {} cadastrado com sucesso após verificação.", email);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void validarDominio(String email) {
        if (email == null) throw new RuntimeException("E-mail inválido.");
        String lower = email.toLowerCase();
        for (String dominio : DOMINIOS_PERMITIDOS) {
            if (lower.endsWith(dominio)) return;
        }
        throw new RuntimeException(
            "Apenas e-mails institucionais (@ifsp.edu.br ou @aluno.ifsp.edu.br) são aceitos.");
    }
}
