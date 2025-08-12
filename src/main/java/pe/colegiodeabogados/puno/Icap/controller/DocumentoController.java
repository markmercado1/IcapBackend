package pe.colegiodeabogados.puno.Icap.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.colegiodeabogados.puno.Icap.model.Documento;
import pe.colegiodeabogados.puno.Icap.service.impl.DocumentoService;


@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentoController {

    private final DocumentoService documentoService;

    @PostMapping("/validar")
    public ResponseEntity<?> validarDocumento(@RequestParam("file") MultipartFile file) {
        try {
            Documento doc = documentoService.validarYGuardar(file);
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
