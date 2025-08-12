package pe.colegiodeabogados.puno.Icap.utils.constancias;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pe.colegiodeabogados.puno.Icap.model.DetalleOrdenCobranza;
import pe.colegiodeabogados.puno.Icap.model.OrdenCobranza;
import pe.colegiodeabogados.puno.Icap.model.Usuario;
import pe.colegiodeabogados.puno.Icap.repository.DetalleOrdenRepository;
import pe.colegiodeabogados.puno.Icap.repository.IUsuarioRepository;
import pe.colegiodeabogados.puno.Icap.repository.OrdenCobranzaRepository;
import pe.colegiodeabogados.puno.Icap.service.impl.NumeroALetras;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Component
@RequiredArgsConstructor
public class Recibo {
    private final OrdenCobranzaRepository ordenRepo;
    private final DetalleOrdenRepository detalleRepo;
    private final IUsuarioRepository usuarioRepo;

    public byte[] generarReciboDesdeCero(Long idOrden) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepo.findOneByUsers(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        OrdenCobranza orden = ordenRepo.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        List<DetalleOrdenCobranza> detalles = detalleRepo.findByOrden(orden);

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            // Configuración inicial
            float margin = 40;
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            float yPosition = pageHeight - margin;
            float leading = 18;

            // =========== CABECERA PROFESIONAL ===========
            // Logo (ejemplo con un rectángulo, reemplazar con imagen real)
            content.setNonStrokingColor(34, 139, 34);    // Verde forestal (más vibrante pero formal)
            content.addRect(margin, yPosition - 30, 60, 30);
            content.fill();

            // Texto del logo
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.setNonStrokingColor(0, 102, 102);
            content.newLineAtOffset(margin + 10, yPosition - 20);
            content.showText("ICAP");
            content.endText();

            // Información institucional
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.setNonStrokingColor(0, 0, 0);
            content.newLineAtOffset(margin + 80, yPosition - 15);
            content.showText("COLEGIO DE ABOGADOS DE PUNO");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(margin + 80, yPosition - 30);
            content.showText("Jr. Grau N°310-Puno, Perú");
            content.endText();

            yPosition -= 60;

            // Línea divisoria
            content.setLineWidth(1f);
            content.setStrokingColor(0, 51, 102);
            content.moveTo(margin, yPosition);
            content.lineTo(pageWidth - margin, yPosition);
            content.stroke();

            yPosition -= 30;

            // =========== TÍTULO DEL RECIBO ===========
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.setNonStrokingColor(0, 0, 0);
            content.newLineAtOffset(pageWidth / 2 - 50, yPosition);
            content.showText("RECIBO DE PAGO");
            content.endText();

            yPosition -= 30;

            // =========== INFORMACIÓN DEL RECIBO ===========
            float column1 = margin;
            float column2 = pageWidth / 2;

            // Número de recibo
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.newLineAtOffset(column1, yPosition);
            content.showText("NRO. RECIBO:");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(column1 + 80, yPosition);
            content.showText(usuario.getCodigo() + "-" + orden.getIdOrden());
            content.endText();

            // Fecha
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.newLineAtOffset(column2, yPosition);
            content.showText("FECHA:");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(column2 + 50, yPosition);
            content.showText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            content.endText();

            yPosition -= leading;

            // Cajero
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.newLineAtOffset(column1, yPosition);
            content.showText("CAJERO:");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(column1 + 80, yPosition);
            content.showText(orden.getUsuario() != null ? orden.getUsuario().getUsers() : "Desconocido");
            content.endText();

            yPosition -= leading * 2;

            // =========== INFORMACIÓN DEL CLIENTE ===========
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.newLineAtOffset(column1, yPosition);
            content.showText("DATOS DEL COLEGIADO:");
            content.endText();

            yPosition -= leading;

            // Nombre completo
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.newLineAtOffset(column1, yPosition);
            content.showText("NOMBRE:");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(column1 + 80, yPosition);
            content.showText(orden.getAgremiado().getANombres() + " " +
                    orden.getAgremiado().getAApellidoPaterno() + " " +
                    orden.getAgremiado().getAApellidoMaterno());
            content.endText();

            yPosition -= leading;

            // Matrícula
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.newLineAtOffset(column1, yPosition);
            content.showText("MATRÍCULA:");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(column1 + 80, yPosition);
            content.showText(orden.getAgremiado().getIdAgremiado().toString());
            content.endText();

            yPosition -= leading * 2;

            // =========== TABLA DE CONCEPTOS ===========
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.newLineAtOffset(column1, yPosition);
            content.showText("DETALLE DE PAGO:");
            content.endText();

            yPosition -= leading;

            // Configuración de la tabla
            float tableTopY = yPosition - 10;
            float tableWidth = pageWidth - 2 * margin;
            float rowHeight = 20;
            float[] colWidths = {40, 300, 100, 100}; // Nro, Concepto, Monto, Subtotal
            float tableBottomY = tableTopY - (rowHeight * (detalles.size() + 1));

            // Encabezado de tabla
            content.setLineWidth(0.5f);
            content.setNonStrokingColor(34, 139, 34);    // Verde forestal (más vibrante pero formal)
            content.setNonStrokingColor(34, 139, 34);    // Verde forestal (más vibrante pero formal)

            // Fondo del encabezado
            content.addRect(margin, tableTopY - rowHeight, tableWidth, rowHeight);
            content.fill();

            // Texto del encabezado
            String[] headers = {"ITEM", "CONCEPTO", "MONTO", "SUBTOTAL"};
            float textX = margin + 5;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.setNonStrokingColor(34, 139, 34);    // Verde forestal (más vibrante pero formal)

            for (int i = 0; i < headers.length; i++) {
                content.newLineAtOffset(textX - (i > 0 ? colWidths[i-1] : 0), tableTopY - rowHeight + 5);
                content.showText(headers[i]);
                textX += colWidths[i];
            }
            content.endText();

            // Filas de la tabla
            content.setNonStrokingColor(0, 0, 0);
            float currentY = tableTopY - rowHeight;
            BigDecimal total = BigDecimal.ZERO; // más recomendado

            for (int i = 0; i < detalles.size(); i++) {
                DetalleOrdenCobranza detalle = detalles.get(i);
                currentY -= rowHeight;

                // Líneas divisorias
                content.setLineWidth(0.2f);
                content.setStrokingColor(200, 200, 200);
                content.moveTo(margin, currentY);
                content.lineTo(margin + tableWidth, currentY);
                content.stroke();

                // Contenido de las celdas
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 9);
                content.newLineAtOffset(margin + 5, currentY + 5);
                content.showText(String.valueOf(i + 1));
                content.endText();

                content.beginText();
                content.newLineAtOffset(margin + colWidths[0] + 5, currentY + 5);
                content.showText(detalle.getDescripcion());
                content.endText();

                content.beginText();
                content.newLineAtOffset(margin + colWidths[0] + colWidths[1] + 5, currentY + 5);
                content.showText(String.format("S/ %.2f", detalle.getMonto()));
                content.endText();

                content.beginText();
                content.newLineAtOffset(margin + colWidths[0] + colWidths[1] + colWidths[2] + 5, currentY + 5);
                content.showText(String.format("S/ %.2f", detalle.getMonto()));
                content.endText();

                total = total.add(detalle.getMonto());
            }

            // Total
            currentY -= rowHeight;
            content.setLineWidth(0.5f);
            content.setStrokingColor(0, 51, 102);
            content.moveTo(margin, currentY);
            content.lineTo(margin + tableWidth, currentY);
            content.stroke();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.newLineAtOffset(margin + colWidths[0] + colWidths[1] + 5, currentY + 5);
            content.showText("TOTAL:");
            content.endText();

            content.beginText();
            content.newLineAtOffset(margin + colWidths[0] + colWidths[1] + colWidths[2] + 5, currentY + 5);
            content.showText(String.format("S/ %.2f", total));
            content.endText();

            yPosition = currentY - leading * 2;

            // =========== INFORMACIÓN ADICIONAL ===========
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.newLineAtOffset(margin, yPosition);
            content.showText("SON:");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(margin + 30, yPosition);
            content.showText(convertirNumeroALetras(total) + " SOLES");
            content.endText();

            yPosition -= leading;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.newLineAtOffset(margin, yPosition);
            content.showText("FORMA DE PAGO:");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(margin + 90, yPosition);
            content.showText("EFECTIVO"); // Puedes parametrizar esto
            content.endText();

            yPosition -= leading * 3;

            // =========== FIRMAS ===========
            float signatureX = pageWidth / 2 - 100;

            content.setLineWidth(0.5f);
            content.moveTo(signatureX, yPosition);
            content.lineTo(signatureX + 200, yPosition);
            content.stroke();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.newLineAtOffset(signatureX + 70, yPosition - 15);
            content.showText("FIRMA Y SELLO");
            content.endText();

            yPosition -= leading * 2;

            // =========== PIE DE PÁGINA ===========
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
            content.setNonStrokingColor(100, 100, 100);
            content.newLineAtOffset(margin, yPosition);
            content.showText("Conserve este recibo como comprobante de pago");
            content.endText();

            yPosition -= leading;

            content.beginText();
            content.newLineAtOffset(margin, yPosition);
            content.showText("Colegio de Abogados de Puno - Todos los derechos reservados");
            content.endText();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();
        return out.toByteArray();
    }
    public static String convertirNumeroALetras(BigDecimal numero) {
        // Aquí puedes usar alguna librería o implementar uno manual.
        // Para el ejemplo, algo básico:
        int parteEntera = numero.intValue();
        int parteDecimal = numero.remainder(BigDecimal.ONE).movePointRight(2).intValue();

        return NumeroALetras.convertir(parteEntera) + " con " + String.format("%02d", parteDecimal) + "/100 soles";
    }
}
