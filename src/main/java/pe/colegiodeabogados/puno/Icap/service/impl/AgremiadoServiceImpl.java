package pe.colegiodeabogados.puno.Icap.service.impl;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.colegiodeabogados.puno.Icap.dtos.AgremiadoDTO;
import pe.colegiodeabogados.puno.Icap.dtos.AgremiadoInfoDTO;
import pe.colegiodeabogados.puno.Icap.dtos.EstadoHabilitacionDTO;
import pe.colegiodeabogados.puno.Icap.mappers.AgremiadoMapper;
import pe.colegiodeabogados.puno.Icap.model.*;
import pe.colegiodeabogados.puno.Icap.repository.*;
import pe.colegiodeabogados.puno.Icap.service.IAgremiadoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.colegiodeabogados.puno.Icap.service.ITipoColegiadoService;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class AgremiadoServiceImpl extends CrudGenericoServiceImp<Agremiado,Long>implements IAgremiadoService {

    @Autowired
    private DataSource dataSource;
    private  final IAgremiadoRepository agremiadoRepository;
    private final AgremiadoMapper agremiadoMapper;
    private final ITipoColegiadoRepository tipoColegiadoRepository;
    private final IEstadoColegiadoRepository estadoColegiadoRepository;
    private final ITrabajadorRepository trabajadorRepository;
    private final MensualidadPagadaRepository mensualidadRepo;
    @Override
    protected ICrudGenericRepository<Agremiado, Long> getRepo() {
        return agremiadoRepository;
    }

    @Override
    public AgremiadoDTO saveD(AgremiadoDTO.AgremiadoCADto dto) {
        Agremiado agremiado = agremiadoMapper.toEntityFromCADTO(dto);
        TipoColegiado tipoColegiado =tipoColegiadoRepository.findById(dto.tipoColegiado())
                        .orElseThrow(() -> new EntityNotFoundException("Tipo colegiado no  encontrado"));
        EstadoColegiado estadoColegiado = estadoColegiadoRepository.findById(dto.estadoColegiado())
                                .orElseThrow(() -> new EntityNotFoundException("Estado de colegiado no encontrada"));
        Trabajador trabajador =trabajadorRepository.findById(dto.trabajador())
                .orElseThrow(() -> new EntityNotFoundException("Trabajador  no encontrada"));
        agremiado.setTrabajador(trabajador);
        agremiado.setEstadoColegiado(estadoColegiado);
        agremiado.setTipoColegiado(tipoColegiado);
        Agremiado agremiadoGuardado = agremiadoRepository.save(agremiado);
        return agremiadoMapper.toDTO(agremiadoGuardado);


}

    @Override
    public AgremiadoDTO updateD(AgremiadoDTO.AgremiadoCADto dto, Long id) {

        Agremiado agremiado = agremiadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agremiado no encontrado"));
        Agremiado agremiadox = agremiadoMapper.toEntityFromCADTO(dto);
        agremiadox.setIdAgremiado(agremiado.getIdAgremiado());
        TipoColegiado tipoColegiado = tipoColegiadoRepository.findById(dto.tipoColegiado())
                        .orElseThrow(() -> new EntityNotFoundException("Tipo colegiado no encontrada"));
        EstadoColegiado estadoColegiado = estadoColegiadoRepository.findById(dto.estadoColegiado())
                                .orElseThrow(() -> new EntityNotFoundException("Estado colegiado no  encontrada"));
        Trabajador trabajador =trabajadorRepository.findById(dto.trabajador())
                                                .orElseThrow(() -> new EntityNotFoundException("Trabajador  no encontrada"));
        agremiadox.setEstadoColegiado(estadoColegiado);
        agremiadox.setTipoColegiado(tipoColegiado);
        agremiadox.setTrabajador(trabajador);
        Agremiado agremiadoActualizado = agremiadoRepository.save(agremiadox);
        return agremiadoMapper.toDTO(agremiadoActualizado);

    }
    public Page<Agremiado> listaPage(Pageable pageable){
        return agremiadoRepository.findAll(pageable);
    }
    @Override
    public Page<Agremiado> buscarPorFiltroGlobal(String filtro, Pageable pageable) {
        return agremiadoRepository.buscarPorFiltroGlobal(filtro, pageable);
    }
    @Override
    public Agremiado findByDni(String dni) {
        return agremiadoRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Agremiado no encontrado con DNI: " + dni));
    }
    @Override
    public boolean estaHabilitado(Agremiado agremiado) {
        LocalDate hoy = LocalDate.now();

        List<MensualidadPagada> pagos = mensualidadRepo.findByAgremiadoIdAgremiado(agremiado.getIdAgremiado());

        Set<String> mesesPagados = pagos.stream()
                .map(p -> String.format("%04d-%02d", p.getAnio(), p.getMes()))
                .collect(Collectors.toSet());

        int mesesNoPagados = 0;

        for (int i = 0; i < 6; i++) {
            LocalDate mes = hoy.minusMonths(i);
            String clave = String.format("%04d-%02d", mes.getYear(), mes.getMonthValue());

            if (!mesesPagados.contains(clave)) {
                mesesNoPagados++;
            }
        }

        // Si NO pagó NINGUNO de los últimos 6 meses → deshabilitado
        return mesesNoPagados < 6;
    }

    public EstadoHabilitacionDTO verificarHabilitacion(Agremiado agremiado) {
        LocalDate hoy = LocalDate.now();

        List<MensualidadPagada> pagos = mensualidadRepo
                .findByAgremiadoIdAgremiadoOrderByAnioDescMesDesc(agremiado.getIdAgremiado());

        Set<String> mesesPagados = pagos.stream()
                .map(p -> String.format("%04d-%02d", p.getAnio(), p.getMes()))
                .collect(Collectors.toSet());

        List<String> mesesFaltantes = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            LocalDate mesEsperado = hoy.minusMonths(i);
            String claveEsperada = String.format("%04d-%02d", mesEsperado.getYear(), mesEsperado.getMonthValue());

            if (!mesesPagados.contains(claveEsperada)) {
                String mesNombre = mesEsperado.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                String texto = mesNombre + " " + mesEsperado.getYear();
                mesesFaltantes.add(texto);
            }
        }

        boolean habilitado = mesesFaltantes.isEmpty();

        return new EstadoHabilitacionDTO(habilitado, mesesFaltantes);
    }

    @Transactional
    public void verificarYActualizarEstado(Agremiado agremiado) {
        boolean habil = estaHabilitado(agremiado);
        long idEstado = habil ? 2 : 1;

        EstadoColegiado nuevoEstado = estadoColegiadoRepository.findById(idEstado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

        agremiado.setEstadoColegiado(nuevoEstado);
        agremiadoRepository.save(agremiado);
    }
@Override
    public AgremiadoInfoDTO obtenerInfoPorId(Long idAgremiado) {
        Agremiado agremiado = agremiadoRepository.findById(idAgremiado)
                .orElseThrow(() -> new RuntimeException("Agremiado no encontrado con ID: " + idAgremiado));

        return new AgremiadoInfoDTO(
                agremiado.getANombres(),
                agremiado.getAApellidoPaterno(),
                agremiado.getAApellidoMaterno(),
                agremiado.getAFechaIncorporacion(),
                agremiado.getAHabilHasta()
        );
    }
}
