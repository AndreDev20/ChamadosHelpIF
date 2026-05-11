package br.edu.ifsp.chamados.enums;

public enum BlocoLocal {

    TERREO_BLOCO_PEDAGOGICO("Térreo - Bloco Pedagógico"),
    TERREO_AREA_EXTERNA("Térreo - Área Externa e Esportiva"),
    PRIMEIRO_ANDAR("1º Andar"),
    SEGUNDO_ANDAR("2º Andar");

    private final String descricao;

    BlocoLocal(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
