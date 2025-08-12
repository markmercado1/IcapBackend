package pe.colegiodeabogados.puno.Icap.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.colegiodeabogados.puno.Icap.dtos.AgremiadoDTO;
import pe.colegiodeabogados.puno.Icap.dtos.CursoDTO;
import pe.colegiodeabogados.puno.Icap.model.Agremiado;
import pe.colegiodeabogados.puno.Icap.model.Curso;
@Mapper(componentModel = "spring")

public interface CursoMapper  extends GenericMapper<CursoDTO, Curso>{


    @Mapping(target = "trabajador", ignore = true)
    Curso toEntityFromCADTO(CursoDTO.CursoCADTO cursoCrearDTO);
}
