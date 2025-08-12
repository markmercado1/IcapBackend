package pe.colegiodeabogados.puno.Icap.pdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.colegiodeabogados.puno.Icap.utils.constancias.ConstanciaHabilitado;

import java.io.IOException;

@Service("Constancia de Habilitación")
public class GeneradorConstanciaHabilitado implements GeneradorPdf {

    @Autowired
    private ConstanciaHabilitado constanciaHabilitado;

    @Override
    public byte[] generar(Long idOrden) throws IOException {
        return constanciaHabilitado.generarConstanciaDesdeCero(idOrden);
    }
}