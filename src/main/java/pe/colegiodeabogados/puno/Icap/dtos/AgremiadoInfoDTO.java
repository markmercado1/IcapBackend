package pe.colegiodeabogados.puno.Icap.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AgremiadoInfoDTO {
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaIncorporacion;
    private LocalDate habilHasta;
}