package pe.colegiodeabogados.puno.Icap.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConceptoDTO {
    private String descripcion;
    private BigDecimal monto;
    private Integer mes;  // <-- Agregar esto
    private Integer anio; // <-- Y esto
}
