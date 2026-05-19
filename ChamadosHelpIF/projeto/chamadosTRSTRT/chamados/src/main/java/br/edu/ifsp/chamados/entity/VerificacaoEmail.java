package br.edu.ifsp.chamados.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Armazena o código de verificação enviado por e-mail durante o cadastro.
 * Expira em 15 minutos. Após confirmação, o campo confirmado é setado true
 * e o usuário correspondente é persistido.
 */
@Entity
@Table(name = "verificacoes_email")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VerificacaoEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String senhaCriptografada;

    /** Código de 6 dígitos enviado ao usuário. */
    @Column(nullable = false, length = 6)
    private String codigo;

    @Column(nullable = false)
    private LocalDateTime expiracao;

    @Column(nullable = false)
    private boolean confirmado = false;

    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(expiracao);
    }
}
