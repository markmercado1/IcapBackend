package pe.colegiodeabogados.puno.Icap.pdf;

import java.io.IOException;

public interface GeneradorPdf {
    byte[] generar(Long idOrden) throws IOException;
}