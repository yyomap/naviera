package com.example.registronaves.exception;

/** Mensaje de validación redactado para mostrarse al usuario. */
public class DatosInvalidosException extends IllegalArgumentException {
    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
