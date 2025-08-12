package pe.colegiodeabogados.puno.Icap.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.colegiodeabogados.puno.Icap.model.Agremiado;

import java.util.Optional;

public interface IAgremiadoRepository extends ICrudGenericRepository<Agremiado,Long>{

    @Query("""
SELECT a FROM Agremiado a
WHERE CAST(a.idAgremiado AS string) LIKE LOWER(CONCAT('%', :filtro, '%'))
   OR LOWER(a.dni) LIKE LOWER(CONCAT('%', :filtro, '%'))
   OR LOWER(a.aNombres) LIKE LOWER(CONCAT('%', :filtro, '%'))
   OR LOWER(a.aApellidoPaterno) LIKE LOWER(CONCAT('%', :filtro, '%'))
   OR LOWER(a.aApellidoMaterno) LIKE LOWER(CONCAT('%', :filtro, '%'))
   OR LOWER(a.aCorreo) LIKE LOWER(CONCAT('%', :filtro, '%'))
   OR LOWER(a.aCelular) LIKE LOWER(CONCAT('%', :filtro, '%'))
   OR LOWER(a.aCiudad) LIKE LOWER(CONCAT('%', :filtro, '%'))
""")
    Page<Agremiado> buscarPorFiltroGlobal(@Param("filtro") String filtro, Pageable pageable);
    Optional<Agremiado> findByDni(String dni);
}

