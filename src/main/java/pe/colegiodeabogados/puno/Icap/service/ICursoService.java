package pe.colegiodeabogados.puno.Icap.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.colegiodeabogados.puno.Icap.dtos.CursoDTO;
import pe.colegiodeabogados.puno.Icap.model.Agremiado;
import pe.colegiodeabogados.puno.Icap.model.Curso;

public interface ICursoService extends ICrudGenericService<Curso,Long>{
    CursoDTO saveD(CursoDTO.CursoCADTO dto);
    CursoDTO updateD(CursoDTO.CursoCADTO dto, Long id);
    Page<Curso> listaPage(Pageable pageable);


}
