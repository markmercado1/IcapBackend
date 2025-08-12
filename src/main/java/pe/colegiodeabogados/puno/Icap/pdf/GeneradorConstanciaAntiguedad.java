package pe.colegiodeabogados.puno.Icap.pdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.colegiodeabogados.puno.Icap.utils.constancias.ConstanciaAntiguedad;

import java.io.IOException;

@Service("Constancia de Antigüedad")
public class GeneradorConstanciaAntiguedad implements GeneradorPdf {

    @Autowired
    private ConstanciaAntiguedad constanciaAntiguedad;

    @Override
    public byte[] generar(Long idOrden) throws IOException {
        return constanciaAntiguedad.generarConstanciaDesdeCero(idOrden);
    }
}