package pe.colegiodeabogados.puno.Icap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.colegiodeabogados.puno.Icap.model.MensualidadPagada;

import java.util.List;

public interface MensualidadPagadaRepository extends JpaRepository<MensualidadPagada, Long> {

    List<MensualidadPagada> findByAgremiadoIdAgremiado(Long idAgremiado);
    List<MensualidadPagada> findByAgremiadoIdAgremiadoOrderByAnioDescMesDesc(Long idAgremiado);
    boolean existsByAgremiado_IdAgremiadoAndAnioAndMes(Long idAgremiado, int anio, int mes);

}