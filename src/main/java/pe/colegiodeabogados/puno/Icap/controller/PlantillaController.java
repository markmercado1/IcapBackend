package pe.colegiodeabogados.puno.Icap.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.colegiodeabogados.puno.Icap.service.impl.PlantillaUploaderServiceImpl;

@RestController
@RequestMapping("/api/plantillas")
@RequiredArgsConstructor
public class PlantillaController {

    private final PlantillaUploaderServiceImpl plantillaUploader;

    @PostMapping("/subir")
    public String subirPlantilla() {
        // Ruta local en tu PC donde está el PDF
        String rutaLocal = "C:/plantillas/constancia_incorporacion.pdf";

        // Public ID para Cloudinary
        String publicId = "plantillas/constancia_incorporacion";

        return plantillaUploader.subirPlantillaPdf(rutaLocal, publicId);
    }
}
