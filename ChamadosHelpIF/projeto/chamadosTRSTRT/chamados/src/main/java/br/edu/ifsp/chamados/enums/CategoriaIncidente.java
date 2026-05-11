package br.edu.ifsp.chamados.enums;

public enum CategoriaIncidente {

    MANUTENCAO("Manutenção"),
    LABORATORIO("Laboratório");

    private final String descricao;

    CategoriaIncidente(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
