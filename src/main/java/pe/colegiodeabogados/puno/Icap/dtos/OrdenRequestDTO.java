package pe.colegiodeabogados.puno.Icap.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrdenRequestDTO {
    private String dni;
    private Long idAgremiado;

    private List<ConceptoDTO> conceptos;

    public BigDecimal calcularTotal() {
        return conceptos.stream()
                .map(ConceptoDTO::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
