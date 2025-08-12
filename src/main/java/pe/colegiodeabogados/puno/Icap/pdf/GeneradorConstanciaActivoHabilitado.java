package pe.colegiodeabogados.puno.Icap.pdf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.colegiodeabogados.puno.Icap.utils.constancias.ConstanciaActivoHabilitado;

import java.io.IOException;

@Service("Constancia estar Activo y Habilitado")
public class GeneradorConstanciaActivoHabilitado implements GeneradorPdf {

    @Autowired
    private ConstanciaActivoHabilitado constanciaActivoHabilitado;

    @Override
    public byte[] generar(Long idOrden) throws IOException {
        return constanciaActivoHabilitado.generarConstanciaDesdeCero(idOrden);
    }
}