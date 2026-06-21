package br.edu.ifsp.chamados.enums;

public enum TipoManutencao {

    TECNOLOGIA("Tecnologia"),
    ELETRICA("Eletrica"),
    HIDRAULICA("Hidraulica"),
    INFRAESTRUTURA("Infraestrutura"),
    MOBILIARIO("Mobiliario");

    private final String descricao;

    TipoManutencao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
