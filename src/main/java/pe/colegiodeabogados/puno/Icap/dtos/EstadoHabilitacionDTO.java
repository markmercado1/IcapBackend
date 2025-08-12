package pe.colegiodeabogados.puno.Icap.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EstadoHabilitacionDTO {
    private boolean habilitado;
    private List<String> mesesFaltantes;

    public String getMensaje() {
        if (habilitado) {
            return "Agremiado al día con los pagos.";
        } else {
            return "Faltan los pagos de: " + String.join(", ", mesesFaltantes);
        }
    }
}
