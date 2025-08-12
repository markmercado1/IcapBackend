package pe.colegiodeabogados.puno.Icap.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.colegiodeabogados.puno.Icap.dtos.EventoDTO;
import pe.colegiodeabogados.puno.Icap.model.Evento;

public interface IEventoService extends ICrudGenericService<Evento,Long>{
    EventoDTO saveD(EventoDTO.EventoCADTo dto);
    EventoDTO updateD(EventoDTO.EventoCADTo dto, Long id);
    Page<Evento> listaPage(Pageable pageable);

}
