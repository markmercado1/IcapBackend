package pe.colegiodeabogados.puno.Icap.pdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GeneradorPdfFactory {
    private final Map<String, GeneradorPdf> generadores;

    @Autowired
    public GeneradorPdfFactory(List<GeneradorPdf> generadoresList) {
        this.generadores = new HashMap<>();
        for (GeneradorPdf g : generadoresList) {
            Service serviceAnnotation = g.getClass().getAnnotation(Service.class);
            if (serviceAnnotation != null) {
                this.generadores.put(serviceAnnotation.value(), g);
            }
        }
    }
    public GeneradorPdf obtenerGenerador(String descripcion) {
        // Estándar: sin mayúsculas, sin espacios extra
        String clave = descripcion.trim().toLowerCase();
        return generadores.getOrDefault(clave, generadores.get("generico")); // fallback
    }
    public byte[] generarPDF(String concepto, Long idOrden) throws IOException {
        GeneradorPdf generador = generadores.getOrDefault(concepto, generadores.get("generico"));
        return generador.generar(idOrden);
    }
}
