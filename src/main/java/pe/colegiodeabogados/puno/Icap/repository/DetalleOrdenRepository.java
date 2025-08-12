package pe.colegiodeabogados.puno.Icap.repository;

import pe.colegiodeabogados.puno.Icap.model.DetalleOrdenCobranza;
import pe.colegiodeabogados.puno.Icap.model.OrdenCobranza;

import java.util.List;

public interface DetalleOrdenRepository extends ICrudGenericRepository<DetalleOrdenCobranza,Long>{
    // Método automático por convención de nombres de Spring Data JPA
    List<DetalleOrdenCobranza> findByOrden(OrdenCobranza orden);
}
