package pe.colegiodeabogados.puno.Icap.service.impl;

public class NumeroALetras {

    private static final String[] UNIDADES = {
            "", "uno", "dos", "tres", "cuatro", "cinco",
            "seis", "siete", "ocho", "nueve", "diez",
            "once", "doce", "trece", "catorce", "quince",
            "dieciséis", "diecisiete", "dieciocho", "diecinueve", "veinte"
    };

    private static final String[] DECENAS = {
            "", "", "veinti", "treinta", "cuarenta", "cincuenta",
            "sesenta", "setenta", "ochenta", "noventa"
    };

    private static final String[] CENTENAS = {
            "", "ciento", "doscientos", "trescientos", "cuatrocientos",
            "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos"
    };

    public static String convertir(int numero) {
        if (numero == 0) return "cero";

        if (numero == 100) return "cien";

        StringBuilder resultado = new StringBuilder();

        if (numero >= 1_000_000) {
            int millones = numero / 1_000_000;
            resultado.append((millones == 1 ? "un millón" : convertir(millones) + " millones")).append(" ");
            numero %= 1_000_000;
        }

        if (numero >= 1000) {
            int miles = numero / 1000;
            if (miles == 1) resultado.append("mil ");
            else resultado.append(convertir(miles)).append(" mil ");
            numero %= 1000;
        }

        if (numero >= 100) {
            int centenas = numero / 100;
            resultado.append(CENTENAS[centenas]).append(" ");
            numero %= 100;
        }

        if (numero > 20) {
            int decenas = numero / 10;
            int unidades = numero % 10;
            if (decenas == 2 && unidades != 0) {
                resultado.append(DECENAS[decenas]).append(UNIDADES[unidades]);
            } else {
                resultado.append(DECENAS[decenas]);
                if (unidades != 0) {
                    resultado.append(" y ").append(UNIDADES[unidades]);
                }
            }
        } else if (numero > 0) {
            resultado.append(UNIDADES[numero]);
        }

        return resultado.toString().trim();
    }
}
