package pe.colegiodeabogados.puno.Icap.controller;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.colegiodeabogados.puno.Icap.dtos.OrdenRequestDTO;
import pe.colegiodeabogados.puno.Icap.model.DetalleOrdenCobranza;
import pe.colegiodeabogados.puno.Icap.model.OrdenCobranza;
import pe.colegiodeabogados.puno.Icap.pdf.GeneradorPdfFactory;
import pe.colegiodeabogados.puno.Icap.pdf.PdfFactory;
import pe.colegiodeabogados.puno.Icap.repository.OrdenCobranzaRepository;
import pe.colegiodeabogados.puno.Icap.service.impl.OrdenCobranzaService;
import pe.colegiodeabogados.puno.Icap.utils.constancias.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//@CrossOrigin("*")

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class GestionOrdenCobranzaController {

    private final PdfFactory pdfFactory;
    private final OrdenCobranzaRepository ordenRepo;
    private final OrdenCobranzaService ordenService;




    @GetMapping
    public ResponseEntity<Map<String, Object>> listarOrdenesPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "idOrden") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrdenCobranza> pageOrdenes = ordenService.listarPaginadas(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("ordenes", pageOrdenes.getContent());
        response.put("currentPage", pageOrdenes.getNumber());
        response.put("totalItems", pageOrdenes.getTotalElements());
        response.put("totalPages", pageOrdenes.getTotalPages());

        return ResponseEntity.ok(response);
    }
    @PostMapping("/generar")
    public ResponseEntity<Map<String, Object>> generarOrden(@RequestBody OrdenRequestDTO dto) {
        Long idOrden = ordenService.generarOrden(dto); // devolvemos ID ahora
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Orden generada con éxito");
        response.put("idOrden", idOrden);
            return ResponseEntity.ok(response);
    }
    @GetMapping("/pdf/{idOrden}")
    public ResponseEntity<byte[]> generarPdfOrden(@PathVariable Long idOrden) throws IOException {
        byte[] pdfFinal = pdfFactory.generarPDFsPorOrden(idOrden);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=orden_" + idOrden + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfFinal);
    }
    @PutMapping("/{id}/pagado")
    public ResponseEntity<?> marcarOrdenComoPagado(@PathVariable Long id) {


        try {

            ordenService.marcarComoPagado(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Orden marcada como pagada");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PutMapping("/{id}/cancelado")
    public ResponseEntity<?> marcarOrdenComoCancelado(@PathVariable Long id) {
        try {
            ordenService.marcarComoCancelado(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Orden marcada como cancelada");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
