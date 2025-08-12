package pe.colegiodeabogados.puno.Icap.pdf;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.colegiodeabogados.puno.Icap.model.DetalleOrdenCobranza;
import pe.colegiodeabogados.puno.Icap.model.OrdenCobranza;
import pe.colegiodeabogados.puno.Icap.repository.OrdenCobranzaRepository;
import pe.colegiodeabogados.puno.Icap.service.impl.OrdenCobranzaService;
import pe.colegiodeabogados.puno.Icap.utils.constancias.ConstanciaHabilitado;
import pe.colegiodeabogados.puno.Icap.utils.constancias.ConstanciaSinSanciones;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PdfFactory {

    @Autowired
    private Map<String, GeneradorPdf> generadores;
    @Autowired
    private GeneradorPdfFactory generadorPdfFactory;

    private final OrdenCobranzaRepository ordenRepo;
    // Lista de conceptos que usan recibo genérico
    private static final Set<String> CONCEPTOS_GENERICOS = Set.of(
            "Matrícula",
            "Mensualidad",
            "Derecho de Colegiatura",
            "Duplicación de Carnet",
            "Recarnetización",
            "Carnet de Biblioteca",
            "Medalla",
            "Solapera",
            "Otros"
    );

    public byte[] generarPdf(String descripcion, Long idOrden) throws IOException {
        String clave = descripcion;

        if (CONCEPTOS_GENERICOS.contains(descripcion)) {
            clave = "generico"; // usa el servicio genérico
        }

        GeneradorPdf generador = generadores.get(clave);
        if (generador == null) {
            throw new IllegalArgumentException("No se encontró generador para: " + descripcion);
        }

        return generador.generar(idOrden);
    }public byte[] generarPDFsPorOrden(Long idOrden) throws IOException {
        OrdenCobranza orden = ordenRepo.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        List<DetalleOrdenCobranza> detalles = orden.getDetalles();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationStream(baos);

        // Agrupar detalles por tipo (genérico vs constancias)
        Map<Boolean, List<DetalleOrdenCobranza>> detallesAgrupados = detalles.stream()
                .collect(Collectors.partitioningBy(
                        detalle -> CONCEPTOS_GENERICOS.contains(detalle.getDescripcion())
                ));

        // Procesar conceptos genéricos (solo un PDF para todos)
        if (!detallesAgrupados.get(true).isEmpty()) {
            byte[] pdfGenerico = generadorPdfFactory.generarPDF("generico", idOrden);
            agregarPDFAlMerge(merger, pdfGenerico);
        }

        // Procesar constancias (un PDF por cada una)
        for (DetalleOrdenCobranza detalle : detallesAgrupados.get(false)) {
            String concepto = detalle.getDescripcion();
            byte[] pdfConstancia = generadorPdfFactory.generarPDF(concepto, idOrden);
            agregarPDFAlMerge(merger, pdfConstancia);
        }

        merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        return baos.toByteArray();
    }

    private void agregarPDFAlMerge(PDFMergerUtility merger, byte[] pdfBytes) throws IOException {
        File temp = File.createTempFile("temp", ".pdf");
        try (FileOutputStream fos = new FileOutputStream(temp)) {
            fos.write(pdfBytes);
            merger.addSource(temp);
        } finally {
            temp.deleteOnExit(); // Se eliminará cuando la JVM termine
        }
    }

}
