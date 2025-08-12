package pe.colegiodeabogados.puno.Icap.mappers;

import org.springframework.stereotype.Component;
import pe.colegiodeabogados.puno.Icap.dtos.EventoResponseDTO;
import pe.colegiodeabogados.puno.Icap.dtos.TrabajadorDTO;
import pe.colegiodeabogados.puno.Icap.model.Evento;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventoResponseMapper {

    public EventoResponseDTO toDTO(Evento evento) {
        EventoResponseDTO dto = new EventoResponseDTO();
        dto.setIdEvento(evento.getIdEvento());
        dto.setETitulo(evento.getETitulo());
        dto.setEDescripcion(evento.getEDescripcion());
        dto.setEFechaInicio(evento.getEFechaInicio());
        dto.setEFechaFin(evento.getEFechaFin());
        dto.setEEstado(evento.getEEstado());
        dto.setEFechaCreacion(evento.getEFechaCreacion());

        // 🔥 Conversión de imagen/documento a base64
        if (evento.getEImagen() != null) {
            dto.setEImagen(Base64.getEncoder().encodeToString(evento.getEImagen()));
        }

        if (evento.getEDocumento() != null) {
            dto.setEDocumento(Base64.getEncoder().encodeToString(evento.getEDocumento()));
        }

        if (evento.getTrabajador() != null) {
            dto.setTrabajador(new TrabajadorDTO(
                    evento.getTrabajador().getIdTrabajador(),
                    evento.getTrabajador().getTNombre() // y demás campos que uses
            ));
        }

        return dto;
    }

    public List<EventoResponseDTO> toDTOs(List<Evento> eventos) {
        return eventos.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
