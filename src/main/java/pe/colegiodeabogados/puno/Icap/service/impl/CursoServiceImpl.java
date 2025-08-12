package pe.colegiodeabogados.puno.Icap.service.impl;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.events.Event;
import pe.colegiodeabogados.puno.Icap.dtos.CursoDTO;
import pe.colegiodeabogados.puno.Icap.mappers.CursoMapper;
import pe.colegiodeabogados.puno.Icap.model.*;
import pe.colegiodeabogados.puno.Icap.repository.ICrudGenericRepository;
import pe.colegiodeabogados.puno.Icap.repository.ICursoRepository;
import pe.colegiodeabogados.puno.Icap.repository.ITrabajadorRepository;
import pe.colegiodeabogados.puno.Icap.service.ICursoService;
@Transactional
@Service
@RequiredArgsConstructor
public class CursoServiceImpl extends CrudGenericoServiceImp<Curso, Long> implements ICursoService {


    private final ICursoRepository cursoRepository;
    private final CursoMapper cursoMapper;
    private final ITrabajadorRepository trabajadorRepository;

    @Override
    public CursoDTO saveD(CursoDTO.CursoCADTO dto) {
        Curso curso = cursoMapper.toEntityFromCADTO(dto);
        Trabajador trabajador =trabajadorRepository.findById(dto.trabajador())
                .orElseThrow(() -> new EntityNotFoundException("Trabajador no encontrado"));
        curso.setTrabajador(trabajador);
        Curso cursoGuardado = cursoRepository.save(curso);
        return cursoMapper.toDTO(cursoGuardado);
    }

    @Override
    public CursoDTO updateD(CursoDTO.CursoCADTO dto, Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado"));
        Curso cursox = cursoMapper.toEntityFromCADTO(dto);
        cursox.setIdCurso(curso.getIdCurso());
        Trabajador trabajador =trabajadorRepository.findById(dto.trabajador())
                .orElseThrow(() -> new EntityNotFoundException("Trabajador no encontrado"));
        cursox.setTrabajador(trabajador);
        Curso cursoActualizado = cursoRepository.save(cursox);
        return cursoMapper.toDTO(cursoActualizado);
    }

    @Override
    protected ICrudGenericRepository<Curso, Long> getRepo() {
        return cursoRepository;
    }

    public Page<Curso> listaPage(Pageable pageable){
        return cursoRepository.findAll(pageable);
    }

}
