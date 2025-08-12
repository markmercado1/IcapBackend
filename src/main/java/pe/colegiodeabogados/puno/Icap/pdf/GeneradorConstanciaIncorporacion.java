package pe.colegiodeabogados.puno.Icap.pdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.colegiodeabogados.puno.Icap.utils.constancias.ConstanciaIncorporacion;

import java.io.IOException;

@Service("Constancia de Incorporación")
public class GeneradorConstanciaIncorporacion implements GeneradorPdf {

    @Autowired
    private ConstanciaIncorporacion constanciaIncorporacion;

    @Override
    public byte[] generar(Long idOrden) throws IOException {
        return constanciaIncorporacion.generarConstanciaDesdeCero(idOrden);
    }
}