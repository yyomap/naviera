package com.example.registronaves.config;

import com.example.registronaves.exception.DatosInvalidosException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class ManejadorErroresTest {
    private MockMvc mvc;

    @RestController
    static class ControladorDePrueba {
        @GetMapping("/inesperado") String inesperado() { throw new IllegalStateException("jdbc:mysql://servidor-interno/base"); }
        @GetMapping("/argumento") String argumento() { throw new IllegalArgumentException("detalle técnico interno"); }
        @GetMapping("/validacion") String validacion() { throw new DatosInvalidosException("Indique la observación del rechazo."); }
        @PostMapping("/json") String json(@RequestBody java.util.Map<String, String> cuerpo) { return "ok"; }
    }

    @BeforeEach void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(new ControladorDePrueba())
            .setControllerAdvice(new ManejadorErrores()).build();
    }

    @Test void errorInesperadoNoExponeDetallesInternos() throws Exception {
        var respuesta = mvc.perform(get("/inesperado")).andReturn().getResponse();
        assertEquals(500, respuesta.getStatus());
        assertTrue(respuesta.getContentAsString().contains("Intenta de nuevo"));
        assertFalse(respuesta.getContentAsString().contains("jdbc"));
        assertFalse(respuesta.getContentAsString().contains("IllegalStateException"));
    }

    @Test void argumentoInesperadoNoExponeSuMensaje() throws Exception {
        var respuesta = mvc.perform(get("/argumento")).andReturn().getResponse();
        assertEquals(400, respuesta.getStatus());
        assertFalse(respuesta.getContentAsString().contains("interno"));
    }

    @Test void conservaMensajeDeValidacionDelNegocio() throws Exception {
        var respuesta = mvc.perform(get("/validacion")).andReturn().getResponse();
        assertEquals(400, respuesta.getStatus());
        assertTrue(respuesta.getContentAsString(java.nio.charset.StandardCharsets.UTF_8).contains("observación del rechazo"));
    }

    @Test void jsonMalformadoDevuelve400() throws Exception {
        var respuesta = mvc.perform(post("/json").contentType("application/json").content("{mal"))
            .andReturn().getResponse();
        assertEquals(400, respuesta.getStatus());
        assertFalse(respuesta.getContentAsString().contains("JsonParseException"));
    }

    @Test void metodoNoPermitidoConserva405() throws Exception {
        var respuesta = mvc.perform(get("/json")).andReturn().getResponse();
        assertEquals(405, respuesta.getStatus());
        assertTrue(respuesta.getHeader("Allow").contains("POST"));
    }
}
