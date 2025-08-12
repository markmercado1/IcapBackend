    package pe.colegiodeabogados.puno.Icap.service.impl;

    import org.springframework.security.core.Authentication;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Service;
    import pe.colegiodeabogados.puno.Icap.dtos.OrdenRequestDTO;
    import pe.colegiodeabogados.puno.Icap.model.*;
    import pe.colegiodeabogados.puno.Icap.repository.*;
    import pe.colegiodeabogados.puno.Icap.utils.constancias.*;
    import java.io.IOException;
    import java.time.LocalDate;
    import java.util.List;
    import java.util.stream.Collectors;
    @Transactional

    @Service
    @RequiredArgsConstructor
    public class OrdenCobranzaService {
        private final ConstanciaActivoHabilitado constanciaActivoHabilitado;
        private final ConstanciaAntiguedad constanciaAntiguedad;
        private final ConstanciaIncorporacion constanciaIncorporacion;
        private final ConstanciaSinSanciones constanciaSinSanciones;
        private final ConstanciaHabilitado constanciaHabilitado;
        private final Recibo recibo;
        private final OrdenCobranzaRepository ordenRepo;
        private final DetalleOrdenRepository detalleRepo;
        private final IAgremiadoRepository agremiadoRepo;
        private final IPagoRepository pagoRepository;
        private final IUsuarioRepository usuarioRepo;
        private final MensualidadPagadaRepository mensualidadRepo;
        public Long generarOrden(OrdenRequestDTO dto) {


            Agremiado agremiado = null;
            // Obtener usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioRepo.findOneByUsers(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            // Buscar por DNI si está presente
            if (dto.getDni() != null && !dto.getDni().isBlank()) {
                agremiado = agremiadoRepo.findByDni(dto.getDni())
                        .orElseThrow(() -> new RuntimeException("Agremiado no encontrado por DNI"));
            }
            // Si no hay DNI, buscar por ID
            else if (dto.getIdAgremiado() != null) {
                agremiado = agremiadoRepo.findById(dto.getIdAgremiado())
                        .orElseThrow(() -> new RuntimeException("Agremiado no encontrado por ID"));
            }
            // Si no se envió ninguno, lanzar error
            else {
                throw new RuntimeException("Debe proporcionar un DNI o un ID de agremiado");
            }

            OrdenCobranza orden = new OrdenCobranza();
            orden.setAgremiado(agremiado);
            orden.setFechaGeneracion(LocalDate.now());
            orden.setEstado("PENDIENTE");
            orden.setTotal(dto.calcularTotal());
            orden.setUsuario(usuario);


            ordenRepo.save(orden);

            List<DetalleOrdenCobranza> detalles = dto.getConceptos().stream()
                    .map(c -> new DetalleOrdenCobranza(orden, c.getDescripcion(), c.getMonto(),c.getMes(),c.getAnio()))
                    .collect(Collectors.toList());

            detalleRepo.saveAll(detalles);
            return orden.getIdOrden();
        }
        @Transactional
        public void marcarComoPagado(Long idOrden) {
            // Validaciones iniciales
            OrdenCobranza orden = ordenRepo.findById(idOrden)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + idOrden));

            if (!"Pendiente".equalsIgnoreCase(orden.getEstado())) {
                throw new RuntimeException("Solo se pueden pagar órdenes en estado Pendiente");
            }

            Agremiado agremiado = orden.getAgremiado();
            if (agremiado == null) {
                throw new RuntimeException("La orden no tiene agremiado asociado");
            }

            if (orden.getDetalles() == null || orden.getDetalles().isEmpty()) {
                throw new RuntimeException("La orden no tiene detalles asociados");
            }

            LocalDate fechaPago = LocalDate.now();

            // Filtrar solo mensualidades
            List<DetalleOrdenCobranza> mensualidades = orden.getDetalles().stream()
                    .filter(det -> "Mensualidad".equalsIgnoreCase(det.getDescripcion()))
                    .collect(Collectors.toList());

            // Actualizar fechas SOLO si hay mensualidades
            if (!mensualidades.isEmpty()) {
                // Actualizar último pago
                agremiado.setUltimoPago(fechaPago);

                // Calcular nuevo aHabilHasta (6 meses desde la fecha de pago o desde aHabilHasta existente)
                LocalDate nuevoHabilHasta = fechaPago.plusMonths(6);

                // Si no tiene fecha o la nueva fecha es posterior
                if (agremiado.getAHabilHasta() == null || nuevoHabilHasta.isAfter(agremiado.getAHabilHasta())) {
                    agremiado.setAHabilHasta(nuevoHabilHasta);
                }

                // Registrar cada mensualidad pagada
                for (DetalleOrdenCobranza detalle : mensualidades) {
                    if (detalle.getAnio() == null || detalle.getMes() == null) {
                        throw new RuntimeException("Falta el año o mes en un detalle de mensualidad");
                    }

                    MensualidadPagada mp = new MensualidadPagada();
                    mp.setAgremiado(agremiado);
                    mp.setAnio(detalle.getAnio());
                    mp.setMes(detalle.getMes());
                    mp.setFechaPago(fechaPago);
                    mensualidadRepo.save(mp);
                }
            }

            // Cambiar estado de orden (siempre)
            orden.setEstado("Pagado");

            // Registrar pago general (siempre)
            Pago pago = new Pago();
            pago.setOrden(orden);
            pago.setAgremiado(agremiado);
            pago.setPMedioPago("Efectivo"); // Considera hacer esto configurable
            pago.setPFechaPago(fechaPago);
            pago.setPMonto(orden.getTotal());
            pagoRepository.save(pago);
        }


        public byte[] generarPdfPorConcepto(String conceptoKey, Long idOrden) throws IOException {
            return switch (conceptoKey.toUpperCase()) {
                case "CONSTANCIA_HABILIDAD" -> constanciaHabilitado.generarConstanciaDesdeCero(idOrden);
                case "CONSTANCIA_INCORPORACION" -> constanciaIncorporacion.generarConstanciaDesdeCero(idOrden);
                case "CONSTANCIA_ANTIGUEDAD" -> constanciaAntiguedad.generarConstanciaDesdeCero(idOrden);
                case "CONSTANCIA_ACTIVO_HABILITADO" -> constanciaActivoHabilitado.generarConstanciaDesdeCero(idOrden);
                case "CONSTANCIA_SIN_SANCIONES" -> constanciaSinSanciones.generarConstanciaDesdeCero(idOrden);
                default -> recibo.generarReciboDesdeCero(idOrden); // para Matricula, Mensualidad, Otros, etc.
            };
        }


        public Page<OrdenCobranza> listarPaginadas(Pageable pageable) {
            return ordenRepo.findAll(pageable);
        }
        @Transactional
        public void marcarComoCancelado(Long idOrden) {
            OrdenCobranza orden = ordenRepo.findById(idOrden)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + idOrden));

            if (!"Pendiente".equalsIgnoreCase(orden.getEstado())) {
                throw new RuntimeException("Solo se pueden cancelar órdenes en estado Pendiente");
            }

            orden.setEstado("Cancelado");
            ordenRepo.save(orden);
        }




    }