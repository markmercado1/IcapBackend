package pe.colegiodeabogados.puno.Icap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class DetalleOrdenCobranza {
    @Id
    @GeneratedValue
    private Long idDetalle;

    public DetalleOrdenCobranza(Long idDetalle, BigDecimal monto, String descripcion, OrdenCobranza orden) {
        this.idDetalle = idDetalle;
        this.monto = monto;
        this.descripcion = descripcion;
        this.orden = orden;
    }

    @ManyToOne
    @JsonIgnore

    private OrdenCobranza orden;
    private Integer anio;
    private Integer mes;

    private String descripcion;
    private BigDecimal monto;
    public DetalleOrdenCobranza(OrdenCobranza orden, String descripcion, BigDecimal monto,Integer mes,Integer anio) {
        this.orden = orden;
        this.descripcion = descripcion;
        this.monto = monto;
        this.mes=mes;
        this.anio=anio;
    }

}
