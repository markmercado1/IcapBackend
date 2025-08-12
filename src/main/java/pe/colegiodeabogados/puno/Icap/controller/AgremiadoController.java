package pe.colegiodeabogados.puno.Icap.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.colegiodeabogados.puno.Icap.dtos.AgremiadoDTO;
import pe.colegiodeabogados.puno.Icap.dtos.AgremiadoInfoDTO;
import pe.colegiodeabogados.puno.Icap.exception.CustomErrorResponse;
import pe.colegiodeabogados.puno.Icap.mappers.AgremiadoMapper;
import pe.colegiodeabogados.puno.Icap.model.Agremiado;
import pe.colegiodeabogados.puno.Icap.service.IAgremiadoService;

import java.net.URI;
import java.util.List;
//@CrossOrigin("*")

@RequiredArgsConstructor
@RestController
@RequestMapping("/agremiados")
public class AgremiadoController {
    private final IAgremiadoService agremiadoService;
    private final AgremiadoMapper agremiadoMapper;
    @GetMapping
    public ResponseEntity<List<AgremiadoDTO>> findAll() {
        List<AgremiadoDTO> list = agremiadoMapper.toDTOs(agremiadoService.findAll());
        return ResponseEntity.ok(list);
    }
    @GetMapping("/{id}")
    public ResponseEntity<AgremiadoDTO> findById(@PathVariable("id") Long
                                                      id) {
        Agremiado obj = agremiadoService.findById(id);
        return ResponseEntity.ok(agremiadoMapper.toDTO(obj));
    }

    @GetMapping("/{id}/info")  // Obtiene información detallada
    public AgremiadoInfoDTO obtenerInfo(@PathVariable Long id) {
        return agremiadoService.obtenerInfoPorId(id);
    }

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody AgremiadoDTO.AgremiadoCADto dto) {
        AgremiadoDTO obj = agremiadoService.saveD(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(
                        obj.getIdAgremiado()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgremiadoDTO> update(@Valid @RequestBody AgremiadoDTO.AgremiadoCADto dto,@PathVariable("id")
                                               Long    id) {

        AgremiadoDTO obj=agremiadoService.updateD(dto,id);
        return ResponseEntity.ok(obj);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomErrorResponse> delete(@PathVariable("id") Long id) {
        CustomErrorResponse response = agremiadoService.delete(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pageable")
    public ResponseEntity<org.springframework.data.domain.Page<AgremiadoDTO>> listPage(Pageable pageable) {
        Page<AgremiadoDTO> page = agremiadoService.listaPage(pageable).map(e -> agremiadoMapper.toDTO(e));
        return ResponseEntity.ok(page);
    }
    @GetMapping("/filtrar")
    public ResponseEntity<Page<AgremiadoDTO>> filtrarGlobal(
            @RequestParam String filtro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "idAgremiado") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<AgremiadoDTO> resultados = agremiadoService.buscarPorFiltroGlobal(filtro, pageable)
                .map(agremiadoMapper::toDTO); // solo si estás usando mapper
        return new ResponseEntity<>(resultados, HttpStatus.OK);
    }
    @GetMapping("/dni/{dni}")
    public ResponseEntity<AgremiadoDTO> findByDni(@PathVariable("dni") String dni) {
        Agremiado agremiado = agremiadoService.findByDni(dni);
        return ResponseEntity.ok(agremiadoMapper.toDTO(agremiado));
    }


}