package pe.colegiodeabogados.puno.Icap.service.impl;


import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.colegiodeabogados.puno.Icap.model.Documento;
import pe.colegiodeabogados.puno.Icap.repository.DocumentoRepository;


import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final OpenAiValidator openAiValidator;

    public Documento validarYGuardar(MultipartFile file) throws IOException {
        String nombre = file.getOriginalFilename();
        String tipo = file.getContentType();
        byte[] contenido = file.getBytes();

        // Paso 1: Extraer texto si es PDF
        String textoExtraido = extraerTexto(file);

        // Paso 2: Lógica de validación simple
        String resultado = validarTexto(textoExtraido);

        // Paso 3: Guardar en base de datos
        Documento doc = Documento.builder()
                .nombreArchivo(nombre)
                .tipoArchivo(tipo)
                .contenido(contenido)
                .resultadoValidacion(resultado)
                .fechaSubida(LocalDateTime.now())
                .build();

        return documentoRepository.save(doc);
    }

    private String extraerTexto(MultipartFile file) throws IOException {
        try (PDDocument documento = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(documento);
        }
    }

    private String validarTexto(String texto) {
        if (texto.contains("DNI") && texto.contains("Firma")) {
            return "Documento válido: contiene DNI y Firma.";
        } else {
            return "Documento inválido: falta DNI o Firma.";
        }
    }
}
