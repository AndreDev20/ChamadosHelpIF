package br.edu.ifsp.chamados.entity;

import br.edu.ifsp.chamados.enums.BlocoLocal;
import br.edu.ifsp.chamados.enums.CategoriaIncidente;
import br.edu.ifsp.chamados.enums.LocalEspecifico;
import br.edu.ifsp.chamados.enums.StatusIncidente;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidentes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaIncidente categoria;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlocoLocal bloco;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocalEspecifico localEspecifico;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String observacaoTecnica;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String observacao;

    @Column(columnDefinition = "TEXT")
    private String anexo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Técnico da manutenção responsável pelo chamado. Obrigatório. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id", nullable = true)
    private Usuario responsavel;

    @Column(nullable = false)
    private LocalDateTime datetime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusIncidente status;

    @PrePersist
    public void prePersist() {
        this.datetime = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusIncidente.CRIADO;
        }
    }
}
