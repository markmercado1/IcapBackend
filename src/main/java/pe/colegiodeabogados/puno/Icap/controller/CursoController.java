package pe.colegiodeabogados.puno.Icap.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.colegiodeabogados.puno.Icap.dtos.AgremiadoDTO;
import pe.colegiodeabogados.puno.Icap.dtos.CursoDTO;
import pe.colegiodeabogados.puno.Icap.exception.CustomErrorResponse;
import pe.colegiodeabogados.puno.Icap.mappers.AgremiadoMapper;
import pe.colegiodeabogados.puno.Icap.mappers.CursoMapper;
import pe.colegiodeabogados.puno.Icap.model.Agremiado;
import pe.colegiodeabogados.puno.Icap.model.Curso;
import pe.colegiodeabogados.puno.Icap.service.IAgremiadoService;
import pe.colegiodeabogados.puno.Icap.service.ICursoService;

import java.net.URI;
import java.util.List;

//@CrossOrigin("*")

@RequiredArgsConstructor
@RestController
@RequestMapping("/cursos")
public class CursoController {

    private final ICursoService cursoService;
    private final CursoMapper cursoMapper;
    @GetMapping
    public ResponseEntity<List<CursoDTO>> findAll() {
        List<CursoDTO> list = cursoMapper.toDTOs(cursoService.findAll());
        return ResponseEntity.ok(list);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CursoDTO> findById(@PathVariable("id") Long
                                                         id) {
        Curso obj = cursoService.findById(id);
        return ResponseEntity.ok(cursoMapper.toDTO(obj));
    }


    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody CursoDTO.CursoCADTO dto) {
        CursoDTO obj = cursoService.saveD(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(
                obj.getIdCurso()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoDTO> update(@Valid @RequestBody CursoDTO.CursoCADTO dto,@PathVariable("id")
    Long    id) {

        CursoDTO obj=cursoService.updateD(dto,id);
        return ResponseEntity.ok(obj);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomErrorResponse> delete(@PathVariable("id") Long id) {
        CustomErrorResponse response = cursoService.delete(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/pageable")
    public ResponseEntity<org.springframework.data.domain.Page<CursoDTO>> listPage(Pageable pageable){
        Page<CursoDTO> page = cursoService.listaPage(pageable).map(e -> cursoMapper.toDTO(e));
        return ResponseEntity.ok(page);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Curso>> listarTodos() {
        return ResponseEntity.ok(cursoService.findAll());
    }

}
