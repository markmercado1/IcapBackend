package pe.colegiodeabogados.puno.Icap.utils.constancias;

import  lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pe.colegiodeabogados.puno.Icap.model.OrdenCobranza;
import pe.colegiodeabogados.puno.Icap.model.Usuario;
import pe.colegiodeabogados.puno.Icap.repository.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Component

public class ConstanciaActivoHabilitado extends ConstanciasBase{
    private final OrdenCobranzaRepository ordenRepo;
    private final IUsuarioRepository usuarioRepo;
    public byte[] generarConstanciaDesdeCero(Long idOrden) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepo.findOneByUsers(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        OrdenCobranza orden = ordenRepo.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) { // <- aquí try-with-resources
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDFont fuente = PDType1Font.TIMES_ROMAN;
            PDFont fuenteNegrita = PDType1Font.TIMES_BOLD;

            Function<Float, Float> x = mm -> mm * 2.8346f;
            Function<Float, Float> y = mm -> PDRectangle.A4.getHeight() - (mm * 2.8346f);

            try (PDPageContentStream content = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {

                float yCursor = y.apply(40f);

                escribirCentrado(content,
                        "Ilustre Colegio de Abogados de Puno, en sus III años de vida institucional",
                        fuente, 17, x.apply(105f), yCursor);
                yCursor -= 30;

                String titulo = "CONSTANCIA";
                float tituloWidth = fuenteNegrita.getStringWidth(titulo) / 1000 * 33;
                content.setFont(fuenteNegrita, 33);
                content.beginText();
                content.newLineAtOffset((PDRectangle.A4.getWidth() - tituloWidth) / 2, yCursor - 33);
                content.showText(titulo);
                content.endText();

                content.setLineWidth(1.5f);
                content.moveTo((PDRectangle.A4.getWidth() - tituloWidth) / 2, yCursor - 38);
                content.lineTo((PDRectangle.A4.getWidth() + tituloWidth) / 2, yCursor - 38);
                content.stroke();

                yCursor -= 60;

                String subtitulo = "EL DECANO DEL ILUSTRE COLEGIO DE ABOGADOS\n" +
                        "DE PUNO, EN USO DE LAS ATRIBUCIONES QUE LE\n" +
                        "CONFIERE EL ESTATUTO, HACE CONSTAR:";
                escribirTextoMultilineaCentrado(content, subtitulo, fuente, 18,
                        PDRectangle.A4.getWidth() / 2, yCursor, 25);
                yCursor -= 90;

                String nombreCompleto = orden.getAgremiado().getANombres() + " " +
                        orden.getAgremiado().getAApellidoPaterno() + " " +
                        orden.getAgremiado().getAApellidoMaterno();
                String estado = orden.getAgremiado().getEstadoColegiado().getEcDescripcion() + " Y " +
                        orden.getAgremiado().getTipoColegiado().getTcDescripcion();

                String nombreCompletoSeguro = nombreCompleto != null ? nombreCompleto : "Nombre no disponible";

                String idAgremiadoSeguro = Optional.ofNullable(orden.getAgremiado())
                        .map(a -> String.valueOf(a.getIdAgremiado()))
                        .orElse("ID no disponible");

                String fechaIncorporacionSeguro = Optional.ofNullable(orden.getAgremiado())
                        .map(a -> a.getAFechaIncorporacion())
                        .map(this::convertirFechaALetras)
                        .orElse("fecha no registrada");

                String estadoSeguro = Optional.ofNullable(estado)
                        .map(String::toUpperCase)
                        .orElse("ESTADO NO REGISTRADO");

                String fechaActualSeguro = convertirFechaALetrasT(LocalDate.now());

                String texto = String.format(
                        "Que el colegiado(a) %s se encuentra inscrito en el Ilustre Colegio de Abogados de Puno bajo el número %s, " +
                                "con fecha de incorporación %s, conforme consta en el padrón general al cual nos remitimos en caso necesario para la confrontación de ley. " +
                                "Asimismo, se deja constancia de que el colegiado figura con la condición de %s en el " +
                                "Colegio de Abogados de Puno para el ejercicio de la profesión.  " +
                                "Se expide la presente constancia al interesado para los fines que estime pertinentes. Otorgado en Puno, a los %s.",
                        nombreCompletoSeguro,
                        idAgremiadoSeguro,
                        fechaIncorporacionSeguro,
                        estadoSeguro,
                        fechaActualSeguro
                );


                float margenMM = 30f;
                float anchoTextoMM = 150f;

                escribirTextoJustificado(content, texto, fuente, 17,
                        x.apply(margenMM),
                        yCursor,
                        x.apply(margenMM + anchoTextoMM),
                        22);
                yCursor -= 180;

                String texto1 = usuario.getCodigo() + "-" + orden.getIdOrden();
                escribirCentrado(content, "RECIBO N° " + texto1,
                        fuenteNegrita, 16, x.apply(50f), y.apply(200f));

                // null-safe para usuario de la orden
                escribir(content,
                        "Verificado por: " + (orden.getUsuario() != null ? orden.getUsuario().getUsers() : "Desconocido"),
                        fuente, 16, x.apply(100f), y.apply(200f));
            }

            document.save(out);
        }

        return out.toByteArray();
    }


}
