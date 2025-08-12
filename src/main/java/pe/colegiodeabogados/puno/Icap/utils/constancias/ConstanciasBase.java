package pe.colegiodeabogados.puno.Icap.utils.constancias;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import org.apache.pdfbox.pdmodel.font.PDFont;

import org.springframework.stereotype.Service;



import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
@RequiredArgsConstructor
public abstract class ConstanciasBase {


    protected static String convertirFechaALetrasT(LocalDate fecha) {
        String[] dias = {
                "", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve",
                "diez", "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete",
                "dieciocho", "diecinueve", "veinte", "veintiuno", "veintidós", "veintitrés",
                "veinticuatro", "veinticinco", "veintiséis", "veintisiete", "veintiocho",
                "veintinueve", "treinta", "treinta y uno"
        };
        String dia = dias[fecha.getDayOfMonth()];
        String mes = fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        return dia + " días del mes de " + mes + " del año " + fecha.getYear();
    }

    protected  String convertirFechaALetras(LocalDate fecha) {

        String mes = fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        return fecha.getDayOfMonth() + " de " + mes + " del año " + fecha.getYear();
    }


    protected void escribir(PDPageContentStream content, String texto, PDFont fuente, int tamano, float x, float y) throws IOException {
        content.beginText();
        content.setFont(fuente, tamano);
        content.newLineAtOffset(x, y);
        content.showText(texto);
        content.endText();
    }

    protected void escribirCentrado(PDPageContentStream content, String texto,
                                  PDFont fuente, float tamaño, float x, float y) throws IOException {
        content.setFont(fuente, tamaño);
        float textoWidth = fuente.getStringWidth(texto) / 1000 * tamaño;
        content.beginText();
        content.newLineAtOffset(x - textoWidth/2, y);
        content.showText(texto);
        content.endText();
    }
    protected String formatearFechaConMesEnLetras(LocalDate fecha) {
        int dia = fecha.getDayOfMonth();
        int anio = fecha.getYear();
        String mes = obtenerNombreMes(fecha.getMonthValue());

        return dia + " de " + mes + " del año " + anio;
    }

    protected  String obtenerNombreMes(int numeroMes) {
        String[] meses = {
                "enero", "febrero", "marzo", "abril", "mayo", "junio",
                "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
        };
        return meses[numeroMes - 1];
    }

    protected void escribirTextoMultilineaCentrado(PDPageContentStream content, String texto,
                                                 PDFont fuente, float tamaño, float x, float y,
                                                 float interlineado) throws IOException {
        String[] lineas = texto.split("\n");
        content.setFont(fuente, tamaño);

        for (String linea : lineas) {
            float textoWidth = fuente.getStringWidth(linea) / 1000 * tamaño;
            content.beginText();
            content.newLineAtOffset(x - textoWidth/2, y);
            content.showText(linea);
            content.endText();
            y -= interlineado;
        }
    }

    protected void escribirTextoJustificado(PDPageContentStream content, String texto,
                                          PDFont fuente, float tamaño, float xInicio,
                                          float yInicio, float xFin, float interlineado) throws IOException {
        // Configuración inicial
        content.setFont(fuente, tamaño);
        float anchoLinea = xFin - xInicio;
        float espacioWidth = fuente.getStringWidth(" ") / 1000 * tamaño;

        // Dividir el texto en palabras
        String[] palabras = texto.split(" ");
        List<String> lineas = new ArrayList<>();
        StringBuilder lineaActual = new StringBuilder();
        float anchoActual = 0;

        // 1. Dividir el texto en líneas que caben en el ancho disponible
        for (String palabra : palabras) {
            float palabraWidth = fuente.getStringWidth(palabra) / 1000 * tamaño;

            if (lineaActual.length() == 0) {
                // Primera palabra de la línea
                if (palabraWidth <= anchoLinea) {
                    lineaActual.append(palabra);
                    anchoActual = palabraWidth;
                } else {
                    // Palabra demasiado larga, dividir (manejo básico)
                    manejarPalabraLarga(palabra, fuente, tamaño, anchoLinea, lineas, lineaActual);
                }
            } else {
                // Verificar si cabe la palabra más un espacio
                if (anchoActual + espacioWidth + palabraWidth <= anchoLinea) {
                    lineaActual.append(" ").append(palabra);
                    anchoActual += espacioWidth + palabraWidth;
                } else {
                    // No cabe, terminar esta línea y comenzar nueva
                    lineas.add(lineaActual.toString());
                    lineaActual = new StringBuilder(palabra);
                    anchoActual = palabraWidth;
                }
            }
        }

        // Añadir la última línea si queda contenido
        if (lineaActual.length() > 0) {
            lineas.add(lineaActual.toString());
        }

        // 2. Escribir cada línea justificada
        float y = yInicio;
        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);

            // No justificar la última línea (convención tipográfica)
            if (i == lineas.size() - 1) {
                content.beginText();
                content.newLineAtOffset(xInicio, y);
                content.showText(linea);
                content.endText();
            } else {
                escribirLineaJustificada(content, linea, fuente, tamaño, xInicio, y, xFin);
            }

            y -= interlineado;
        }
    }

    private void escribirLineaJustificada(PDPageContentStream content, String linea,
                                          PDFont fuente, float tamaño, float xInicio,
                                          float y, float xFin) throws IOException {
        // Dividir la línea en palabras
        String[] palabras = linea.split(" ");

        if (palabras.length < 2) {
            // Solo una palabra, escribir normal
            content.beginText();
            content.newLineAtOffset(xInicio, y);
            content.showText(linea);
            content.endText();
            return;
        }

        // Calcular ancho total del texto sin espacios
        float anchoTexto = 0;
        for (String palabra : palabras) {
            anchoTexto += fuente.getStringWidth(palabra) / 1000 * tamaño;
        }

        // Calcular espacio total disponible para distribuir
        float espacioTotal = xFin - xInicio - anchoTexto;
        float espacioEntrePalabras = espacioTotal / (palabras.length - 1);

        // Escribir cada palabra con el espaciado calculado
        float x = xInicio;
        content.beginText();
        content.newLineAtOffset(x, y);

        for (int i = 0; i < palabras.length; i++) {
            content.showText(palabras[i]);

            if (i < palabras.length - 1) {
                // Mover posición para la siguiente palabra
                float palabraWidth = fuente.getStringWidth(palabras[i]) / 1000 * tamaño;
                x += palabraWidth + espacioEntrePalabras;
                content.endText();
                content.beginText();
                content.newLineAtOffset(x, y);
            }
        }

        content.endText();
    }

    protected void manejarPalabraLarga(String palabra, PDFont fuente, float tamaño,
                                     float anchoLinea, List<String> lineas,
                                     StringBuilder lineaActual) throws IOException {
        // Manejo básico de palabras demasiado largas (podría mejorarse)
        int maxCaracteres = (int) (anchoLinea / (fuente.getStringWidth("M") / 1000 * tamaño));
        maxCaracteres = Math.max(1, maxCaracteres - 1); // Asegurar al menos 1 carácter

        int inicio = 0;
        while (inicio < palabra.length()) {
            int fin = Math.min(inicio + maxCaracteres, palabra.length());
            String parte = palabra.substring(inicio, fin);

            if (lineaActual.length() == 0) {
                lineas.add(parte);
            } else {
                lineas.add(lineaActual.toString());
                lineaActual.setLength(0);
                lineas.add(parte);
            }

            inicio = fin;
        }
    }

    protected  String calcularAntiguedad(LocalDate fechaIncorporacion) {
        if (fechaIncorporacion == null) {
            return "Fecha no disponible";
        }

        LocalDate hoy = LocalDate.now();
        Period periodo = Period.between(fechaIncorporacion, hoy);

        int años = periodo.getYears();
        int meses = periodo.getMonths();

        if (años >= 1) {
            return años + (años == 1 ? " año" : " años");
        } else if (meses >= 1) {
            return meses + (meses == 1 ? " mes" : " meses");
        } else {
            return "Menos de un mes";
        }
    }
}
