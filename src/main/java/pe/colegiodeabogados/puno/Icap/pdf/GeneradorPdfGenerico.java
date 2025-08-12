package pe.colegiodeabogados.puno.Icap.pdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.colegiodeabogados.puno.Icap.utils.constancias.Recibo;

import java.io.IOException;

@Service("generico")
public class GeneradorPdfGenerico implements GeneradorPdf {

    @Autowired
    private Recibo generadorRecibo;

    @Override
    public byte[] generar(Long idOrden) throws IOException {
        return generadorRecibo.generarReciboDesdeCero(idOrden);
    }
}
