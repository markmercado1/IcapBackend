package pe.colegiodeabogados.puno.Icap.service.impl;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.colegiodeabogados.puno.Icap.service.PlantillaUploaderService;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlantillaUploaderServiceImpl implements PlantillaUploaderService {

    private final Cloudinary cloudinary;
    @Override
    public String subirPlantillaPdf(String rutaLocal, String publicId) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    new File(rutaLocal),
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "public_id", publicId // Ej: "plantillas/constancia_habilidad"
                    )
            );
            return uploadResult.get("secure_url").toString(); // Devuelve la URL pública
        } catch (IOException e) {
            throw new RuntimeException("Error al subir plantilla a Cloudinary", e);
        }
    }
}
