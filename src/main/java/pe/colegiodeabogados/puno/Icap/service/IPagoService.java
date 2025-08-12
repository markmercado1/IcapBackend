package pe.colegiodeabogados.puno.Icap.service;

import pe.colegiodeabogados.puno.Icap.dtos.MesPagoDTO;
import pe.colegiodeabogados.puno.Icap.dtos.PagoConDetallesDTO;
import pe.colegiodeabogados.puno.Icap.dtos.PagoDTO;
import pe.colegiodeabogados.puno.Icap.model.Agremiado;
import pe.colegiodeabogados.puno.Icap.model.Pago;

import java.util.List;

public interface IPagoService extends ICrudGenericService<Pago,Long>{
    Pago savePagoConDetalles(PagoConDetallesDTO pagoConDetallesDTO);
    PagoDTO saveD(PagoDTO.PagoCADto dto);
    PagoDTO updateD(PagoDTO.PagoCADto dto, Long id);
    List<MesPagoDTO> obtenerMesesPagados(Long idAgremiado);
}
