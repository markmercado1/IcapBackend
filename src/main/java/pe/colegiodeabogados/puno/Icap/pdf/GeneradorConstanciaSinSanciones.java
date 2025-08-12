package pe.colegiodeabogados.puno.Icap.pdf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.colegiodeabogados.puno.Icap.utils.constancias.ConstanciaSinSanciones;

import java.io.IOException;

@Service("Constancia No tener Sanciones")
public class GeneradorConstanciaSinSanciones implements GeneradorPdf {

    @Autowired
    private ConstanciaSinSanciones constanciaSinSanciones;

    @Override
    public byte[] generar(Long idOrden) throws IOException {
        return constanciaSinSanciones.generarConstanciaDesdeCero(idOrden);
    }
}