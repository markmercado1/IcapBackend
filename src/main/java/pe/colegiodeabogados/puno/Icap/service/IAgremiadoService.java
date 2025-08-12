package pe.colegiodeabogados.puno.Icap.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.colegiodeabogados.puno.Icap.dtos.AgremiadoDTO;
import pe.colegiodeabogados.puno.Icap.dtos.AgremiadoInfoDTO;
import pe.colegiodeabogados.puno.Icap.model.Agremiado;

public interface IAgremiadoService extends ICrudGenericService<Agremiado, Long> {
    AgremiadoDTO saveD(AgremiadoDTO.AgremiadoCADto dto);
    AgremiadoDTO updateD(AgremiadoDTO.AgremiadoCADto dto, Long id);
    Page<Agremiado> listaPage(Pageable pageable);
    Page<Agremiado> buscarPorFiltroGlobal(String filtro, Pageable pageable);
    Agremiado findByDni(String dni);
    boolean estaHabilitado(Agremiado agremiado);

    AgremiadoInfoDTO obtenerInfoPorId(Long idAgremiado);
}
