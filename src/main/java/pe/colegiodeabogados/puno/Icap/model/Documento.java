package pe.colegiodeabogados.puno.Icap.model;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreArchivo;

    private String tipoArchivo; // pdf, jpg, etc.

    @Lob
    @Column(length = 1000000)
    private byte[] contenido;

    private String resultadoValidacion;

    private LocalDateTime fechaSubida;
}
