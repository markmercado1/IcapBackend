package pe.colegiodeabogados.puno.Icap.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsuarioDTO {
    private Long idUsuario;
    @NotNull
    private String users;
    @NotNull
    private String estado;
    private String token;
    private String codigo;

    public record CredencialesDto(String users, char[] clave) { }

    public record UsuarioCrearDto(String users, char[] clave, String rol, String estado,String codigo) { }
}
