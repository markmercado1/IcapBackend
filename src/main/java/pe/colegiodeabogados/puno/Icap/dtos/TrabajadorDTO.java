package pe.colegiodeabogados.puno.Icap.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.colegiodeabogados.puno.Icap.model.Trabajador;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorDTO {

    public TrabajadorDTO(Long id, String nombres) {
        this.idTrabajador = id;
        this.tNombre = nombres;
    }

    private Long idTrabajador;

    @NotNull
    @Size(min = 4, max = 60)
    private String tNombre;

    @NotNull
    @Size(min = 4, max = 60)
    private String tCorreo;

    @NotNull
    @Size(min = 4, max = 60)
    private String tUsername;

    @NotNull
    private Trabajador.Rol tRol; // o enum si lo necesitas como objeto
}
