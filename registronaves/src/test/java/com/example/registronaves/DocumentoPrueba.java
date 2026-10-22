package com.example.registronaves;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** PDF real para las pruebas heredadas; una cabecera simulada ya no es suficiente. */
final class DocumentoPrueba {
    private DocumentoPrueba() {}

    static Path crearPdf() throws IOException {
        Path archivo = Files.createTempFile("solicitud", ".pdf");
        try (OutputStream salida = Files.newOutputStream(archivo)) {
            Document documento = new Document();
            try {
                PdfWriter.getInstance(documento, salida);
                documento.open();
                documento.add(new Paragraph("Documento de prueba"));
            } finally {
                documento.close();
            }
        } catch (DocumentException e) {
            Files.deleteIfExists(archivo);
            throw new IOException("No se pudo crear el PDF de prueba.", e);
        }
        return archivo;
    }
}
