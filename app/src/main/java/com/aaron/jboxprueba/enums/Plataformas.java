package com.aaron.jboxprueba.enums;

public enum Plataformas {
    ARRIBA("Plataforma superior", 1),
    MEDIO("Plataforma central", 2),
    ABAJO("Plataforma inferior", 3),
    HORIZONTAL("Plataforma horizontal", 4);

    private final String plataforma;
    private final int codigo;

    Plataformas(String plataforma, int codigo) {
        this.plataforma = plataforma;
        this.codigo = codigo;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public int getCodigo() {
        return codigo;
    }
}

