package pe.colegiodeabogados.puno.Icap.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoResponseDTO {
    private Long idEvento;
    private String eTitulo;
    private String eDescripcion;
    private LocalDate eFechaInicio;
    private LocalDate eFechaFin;
    private String eEstado;
    private LocalDate eFechaCreacion;

    private String eImagen;     // en base64
    private String eDocumento;  // opcional

    private TrabajadorDTO trabajador;
}
