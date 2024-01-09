package com.aaron.jboxprueba.enums;

public enum Muros {
    ARRIBA("Muro superior", 1),
    DERECHA("Muro derecha", 2),
    ABAJO("Muro inferior", 3),
    IZQUIERDA("Muro Superior", 4);


    private final String muro;
    private final int codigo;

    Muros(String muro, int codigo) {
        this.muro = muro;
        this.codigo = codigo;
    }

    public String getMuro() {
        return muro;
    }

    public int getCodigo() {
        return codigo;
    }
}