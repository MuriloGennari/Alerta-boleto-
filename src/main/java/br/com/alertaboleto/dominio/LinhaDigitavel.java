package br.com.alertaboleto.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public final class LinhaDigitavel {

    public static final int TAMANHO = 47;
    private static final LocalDate DATA_BASE_FATOR = LocalDate.of(1997, 10, 7);

    private LinhaDigitavel() {
    }


    public static String gerar(String codigoBanco, String nossoNumero, BigDecimal valor, LocalDate vencimento) {
        String campoLivre = String.format("%029d", Math.abs((long) nossoNumero.hashCode()) % 100_000_000_000L);
        long centavos = valor.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
        return codigoBanco + "9" + campoLivre
                + String.format("%04d", fatorVencimento(vencimento))
                + String.format("%010d", centavos);
    }

    public static int fatorVencimento(LocalDate vencimento) {
        long fator = ChronoUnit.DAYS.between(DATA_BASE_FATOR, vencimento);
        if (fator > 9999) {
            fator = ((fator - 10000) % 9000) + 1000;
        }
        return (int) fator;
    }

    public static boolean formatoValido(String linha) {
        return linha != null && linha.matches("\\d{" + TAMANHO + "}");
    }


    public static String codigoBanco(String linha) {
        return linha.substring(0, 3);
    }


    public static String formatar(String l) {
        if (!formatoValido(l)) {
            return l;
        }
        return l.substring(0, 5) + "." + l.substring(5, 10) + " "
                + l.substring(10, 15) + "." + l.substring(15, 21) + " "
                + l.substring(21, 26) + "." + l.substring(26, 32) + " "
                + l.substring(32, 33) + " " + l.substring(33);
    }
}
