package com.example.registronaves.Model;

import com.example.registronaves.exception.DatosInvalidosException;

public enum EstadoTramite {
    EN_REVISION("En revisión"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado");

    private final String etiqueta;

    EstadoTramite(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }

    public static EstadoTramite desdeEtiqueta(String texto) {
        for (EstadoTramite e : values()) {
            if (e.etiqueta.equalsIgnoreCase(texto == null ? "" : texto.trim())) return e;
        }
        throw new DatosInvalidosException("Estado no válido. Use: En revisión, Aprobado o Rechazado.");
    }
}