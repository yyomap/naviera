package com.example.registronaves.service;

import com.example.registronaves.exception.DatosInvalidosException;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidacionPdfTest {
    private byte[] pdf() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Document doc = new Document();
        PdfWriter.getInstance(doc, bytes);
        doc.open();
        doc.add(new Paragraph("Documento de prueba"));
        doc.close();
        return bytes.toByteArray();
    }

    @Test void aceptaPdfLegible() throws Exception {
        assertDoesNotThrow(() -> NaveService.validarPdf(pdf()));
    }

    @Test void rechazaTextoConCabeceraSimulada() {
        assertThrows(DatosInvalidosException.class,
            () -> NaveService.validarPdf("%PDF basura".getBytes(StandardCharsets.US_ASCII)));
    }

    @Test void rechazaCabeceraYFinalSimuladosSinEstructura() {
        assertThrows(DatosInvalidosException.class,
            () -> NaveService.validarPdf("%PDF-1.4\nEsto no es un PDF\n%%EOF".getBytes(StandardCharsets.US_ASCII)));
    }

    @Test void rechazaPdfTruncado() throws Exception {
        byte[] original = pdf();
        assertThrows(DatosInvalidosException.class,
            () -> NaveService.validarPdf(Arrays.copyOf(original, original.length - 30)));
    }

    @Test void rechazaArchivoVacio() {
        assertThrows(DatosInvalidosException.class, () -> NaveService.validarPdf(new byte[0]));
    }

    @Test void rechazaPdfConContrasena() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(pdf());
        PdfStamper stamper = new PdfStamper(reader, bytes);
        stamper.setEncryption("clave-usuario".getBytes(StandardCharsets.UTF_8),
            "clave-propietario".getBytes(StandardCharsets.UTF_8), 0, PdfWriter.ENCRYPTION_AES_128);
        stamper.close();
        reader.close();
        assertThrows(DatosInvalidosException.class, () -> NaveService.validarPdf(bytes.toByteArray()));
    }
}
