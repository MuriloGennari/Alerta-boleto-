package br.com.alertaboleto.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Regras da linha digitável (47 dígitos) do boleto bancário brasileiro.
 * <p>
 * Estrutura simplificada usada no projeto:
 * <pre>
 * [3 código do banco][1 moeda = 9][29 campo livre][4 fator de vencimento][10 valor em centavos]
 * </pre>
 * Os 3 primeiros dígitos SEMPRE identificam o banco emissor - é isso que a análise
 * anti-golpe confere (golpistas costumam alterar a linha digitável).
 */
public final class LinhaDigitavel {

    public static final int TAMANHO = 47;
    private static final LocalDate DATA_BASE_FATOR = LocalDate.of(1997, 10, 7);

    private LinhaDigitavel() {
    }

    /** Gera uma linha digitável coerente com banco, valor e vencimento informados. */
    public static String gerar(String codigoBanco, String nossoNumero, BigDecimal valor, LocalDate vencimento) {
        String campoLivre = String.format("%029d", Math.abs((long) nossoNumero.hashCode()) % 100_000_000_000L);
        long centavos = valor.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
        return codigoBanco + "9" + campoLivre
                + String.format("%04d", fatorVencimento(vencimento))
                + String.format("%010d", centavos);
    }

    /**
     * Fator de vencimento = dias desde 07/10/1997. Ao estourar 9999 (o que ocorreu em
     * 22/02/2025) o fator recomeça em 1000, conforme regra da FEBRABAN.
     */
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

    /** Código do banco = 3 primeiros dígitos. */
    public static String codigoBanco(String linha) {
        return linha.substring(0, 3);
    }

    /** Formata em blocos, do jeito que aparece impresso no boleto. */
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
