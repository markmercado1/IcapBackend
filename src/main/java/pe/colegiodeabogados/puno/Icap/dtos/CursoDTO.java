package pe.colegiodeabogados.puno.Icap.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.colegiodeabogados.puno.Icap.model.Curso;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDTO {

    private Long idCurso;

    @NotNull(message = "El nombre no puede ser nulo")
    @Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
    private String nombre;

    @Size(max = 250, message = "La descripción no debe exceder los 250 caracteres")
    private String descripcion;
    @Size(max = 250, message = "El docente no debe de exceder los 100 caracteres")
    private String docente;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @NotNull(message = "La modalidad no puede ser nula")
    private Curso.Modalidad modalidad;

    @NotNull(message = "El estado no puede ser nulo")
    private Curso.Estado estado;

    @NotNull(message = "El trabajador no puede ser nulo")
    private TrabajadorDTO trabajador;
    public record CursoCADTO(

            Long idCurso,

            @NotNull(message = "El nombre no puede ser nulo")
            @Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
            String nombre,

            @Size(max = 250, message = "La descripción no debe exceder los 250 caracteres")
            String descripcion,
            @Size(max = 100, message = "El docente  no debe exceder los 100 caracteres")
            String docente,
            LocalDate fechaInicio,

            LocalDate fechaFin,

            @NotNull(message = "La modalidad no puede ser nula")
            String modalidad, // se espera PRESENCIAL, VIRTUAL, MIXTO

            @NotNull(message = "El estado no puede ser nulo")
            String estado, // se espera ACTIVO, INACTIVO

            @NotNull(message = "El ID del trabajador no puede ser nulo")
            Long trabajador
    ) {}
}
