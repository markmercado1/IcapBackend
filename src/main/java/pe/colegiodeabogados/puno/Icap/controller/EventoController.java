package pe.colegiodeabogados.puno.Icap.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.colegiodeabogados.puno.Icap.dtos.AgremiadoDTO;
import pe.colegiodeabogados.puno.Icap.dtos.EventoDTO;
import pe.colegiodeabogados.puno.Icap.dtos.EventoResponseDTO;
import pe.colegiodeabogados.puno.Icap.exception.CustomErrorResponse;
import pe.colegiodeabogados.puno.Icap.mappers.EventoMapper;
import pe.colegiodeabogados.puno.Icap.mappers.EventoResponseMapper;
import pe.colegiodeabogados.puno.Icap.model.Evento;
import pe.colegiodeabogados.puno.Icap.service.IEventoService;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/eventos")
public class EventoController {
    private final IEventoService eventoService;
    private final EventoMapper eventoMapper;
    private final EventoResponseMapper eventoResponseMapper; // 👈 nuevo


    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> findAll() {
        List<EventoResponseDTO> list = eventoResponseMapper.toDTOs(eventoService.findAll());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> findById(@PathVariable("id") Long id) {
        Evento obj = eventoService.findById(id);
        return ResponseEntity.ok(eventoResponseMapper.toDTO(obj));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoDTO> update(@Valid @PathVariable("id") Long  id, @RequestBody
                                            EventoDTO dto) {
        dto.setIdEvento(id);
        Evento obj = eventoService.update(id, eventoMapper.toEntity(dto));
        return ResponseEntity.ok(eventoMapper.toDTO(obj));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomErrorResponse> delete(@PathVariable("id") Long id) {
        CustomErrorResponse response = eventoService.delete(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/pageable")
    public ResponseEntity<org.springframework.data.domain.Page<EventoDTO>> listPage(Pageable pageable) {
        Page<EventoDTO> page = eventoService.listaPage(pageable).map(e -> eventoMapper.toDTO(e));
        return ResponseEntity.ok(page);
    }
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventoDTO> saveEvento(
            @RequestParam("eTitulo") String titulo,
            @RequestParam("eDescripcion") String descripcion,
            @RequestParam("eFechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("eFechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam("eEstado") String estado,
            @RequestParam("trabajador") Long idTrabajador,
            @RequestParam(value = "eImagen", required = false) MultipartFile imagen,
            @RequestParam(value = "eDocumento", required = false) MultipartFile documento
    ) {
        try {
            EventoDTO.EventoCADTo dto = new EventoDTO.EventoCADTo(
                    null,
                    titulo,
                    descripcion,
                    fechaInicio,
                    fechaFin,
                    estado,
                    LocalDate.now(),
                    imagen != null && !imagen.isEmpty() ? imagen.getBytes() : null,
                    documento != null && !documento.isEmpty() ? documento.getBytes() : null,
                    idTrabajador
            );

            EventoDTO result = eventoService.saveD(dto);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




}