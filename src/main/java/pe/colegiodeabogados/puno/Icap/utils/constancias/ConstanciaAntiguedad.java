package pe.colegiodeabogados.puno.Icap.utils.constancias;


import lombok.RequiredArgsConstructor;
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

public class ConstanciaAntiguedad extends ConstanciasBase{
    private final OrdenCobranzaRepository ordenRepo;
    private final IUsuarioRepository usuarioRepo;

    public byte[] generarConstanciaDesdeCero(Long idOrden) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepo.findOneByUsers(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        OrdenCobranza orden = ordenRepo.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDFont fuente = PDType1Font.TIMES_ROMAN;
        PDFont fuenteNegrita = PDType1Font.TIMES_BOLD;

        // Conversión mm → pt (1 mm = 2.8346 pt)
        Function<Float, Float> x = mm -> mm * 2.8346f;
        Function<Float, Float> y = mm -> PDRectangle.A4.getHeight() - (mm * 2.8346f);

        PDPageContentStream content = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

        // Margen superior inicial (40mm desde arriba)
        float yCursor = y.apply(40f);

        // 1. ENCABEZADO (más espacio antes del título)
        escribirCentrado(content,
                "Ilustre Colegio de Abogados de Puno, en sus III años de vida institucional",
                fuente, 17, x.apply(105f), yCursor);
        yCursor -= 30; // Más espacio después del encabezado (3cm)

        // 2. TÍTULO "CONSTANCIA" (Times New Roman 33, negrita y subrayado)
        String titulo = "CONSTANCIA";
        float tituloWidth = fuenteNegrita.getStringWidth(titulo) / 1000 * 33;
        content.setFont(fuenteNegrita, 33);
        content.beginText();
        content.newLineAtOffset((PDRectangle.A4.getWidth() - tituloWidth) / 2, yCursor - 33);
        content.showText(titulo);
        content.endText();

        // Dibujar línea de subrayado
        content.setLineWidth(1.5f);
        content.moveTo((PDRectangle.A4.getWidth() - tituloWidth) / 2, yCursor - 38); // Bajar un poco la línea
        content.lineTo((PDRectangle.A4.getWidth() + tituloWidth) / 2, yCursor - 38);
        content.stroke();

        yCursor -= 60; // Más espacio después del título (6cm)

        // 3. SUBTÍTULO (más espacio arriba y abajo)
        String subtitulo = "EL DECANO DEL ILUSTRE COLEGIO DE ABOGADOS\n" +
                "DE PUNO, EN USO DE LAS ATRIBUCIONES QUE LE\n" +
                "CONFIERE EL ESTATUTO, HACE CONSTAR:";

        escribirTextoMultilineaCentrado(content, subtitulo, fuente, 18,
                PDRectangle.A4.getWidth() / 2, yCursor, 25); // Más interlineado
        yCursor -= 90; // Más espacio después del subtítulo (9cm)

        // 4. TEXTO PRINCIPAL (justificado)
        String nombreCompleto = orden.getAgremiado().getANombres() + " " +
                orden.getAgremiado().getAApellidoPaterno() + " " +
                orden.getAgremiado().getAApellidoMaterno();


        String nombreCompletoSeguro = nombreCompleto != null ? nombreCompleto : "Nombre no disponible";

        String idAgremiadoSeguro = Optional.ofNullable(orden.getAgremiado())
                .map(a -> String.valueOf(a.getIdAgremiado()))
                .orElse("ID no disponible");

        String fechaIncorporacionSeguro = Optional.ofNullable(orden.getAgremiado())
                .map(a -> a.getAFechaIncorporacion())
                .map(this::convertirFechaALetras)
                .orElse("fecha no registrada");

        String antiguedadSeguro = Optional.ofNullable(orden.getAgremiado())
                .map(a -> a.getAFechaIncorporacion())
                .map(this::calcularAntiguedad)
                .orElse("antigüedad no registrada");

        String fechaActualSeguro = convertirFechaALetrasT(LocalDate.now());

        String texto = String.format(
                "Que, el Colegiado(a), %s, se encuentra inscrito en el Ilustre Colegio de Abogados de Puno bajo el número %s. " +
                        "de fecha  %s, conforme aparece en el padrón general al que nos remitimos " +
                        "en caso necesario para la confrontación de Ley. Asimismo, certifico que el colegiado cuenta con %s de agremiado a esta orden. " +
                        "Se expide la presente constancia al interesado para los fines pertinentes. Otorgado en Puno a los %s.",
                nombreCompletoSeguro,
                idAgremiadoSeguro,
                fechaIncorporacionSeguro,
                antiguedadSeguro,
                fechaActualSeguro
        );


        // Definir márgenes de 3 cm (30 mm) a cada lado
        float margenMM = 30f; // 3 cm = 30 mm
        float anchoTextoMM = 150f; // 210mm (A4) - 60mm (márgenes) = 150mm

// Usar estas medidas en tu llamada
        escribirTextoJustificado(content, texto, fuente, 17,
                x.apply(margenMM),      // 30mm desde la izquierda (≈3cm)
                yCursor,
                x.apply(margenMM + anchoTextoMM),  // 30mm + 150mm = 180mm (30mm antes del borde derecho)
                22);
        yCursor -= 180; // Espacio después del texto principal

        // 5. NÚMERO DE RECIBO (centrado, negrita, más abajo)
        String texto1 = usuario.getCodigo() + "-" + orden.getIdOrden();

        escribirCentrado(content, "RECIBO N° " + texto1,
                fuenteNegrita, 16, x.apply(50f), y.apply(180f)); // Fijo a 5cm del borde inferior
        escribir(content, "Verificado por: "+(orden.getUsuario() != null ? orden.getUsuario().getUsers() : "Desconocido"),
                fuente,16, x.apply(100f), y.apply(180f));
        content.close();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();
        return out.toByteArray();
    }

}
