package pe.colegiodeabogados.puno.Icap.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MesPagoDTO {
    private int anio;
    private int mes;
    private boolean pagado;

    // Getters y setters
}
