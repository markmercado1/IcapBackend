package pe.colegiodeabogados.puno.Icap.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class OrdenCobranza {
    @Id
    @GeneratedValue
    private Long idOrden;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "id_agremiado")
    private Agremiado agremiado;
    @Column(nullable = false)
    private int anio;

    @Column(nullable = false)
    private int mes;
    private BigDecimal total;
    private LocalDate fechaGeneracion;
    private String estado;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleOrdenCobranza> detalles;
}