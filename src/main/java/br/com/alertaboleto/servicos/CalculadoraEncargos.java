package br.com.alertaboleto.servicos;

import br.com.alertaboleto.dominio.Boleto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Calcula multa e juros de mora de um boleto atrasado.
 * <p>
 * Regras usadas (padrão em contratos de consumo no Brasil):
 * <ul>
 *   <li>Multa de 2% sobre o valor (limite do art. 52, §1º do CDC);</li>
 *   <li>Juros de mora de 1% ao mês, proporcionais aos dias de atraso (pro rata die).</li>
 * </ul>
 */
public class CalculadoraEncargos {

    private static final BigDecimal MULTA = new BigDecimal("0.02");
    private static final BigDecimal JUROS_AO_DIA = new BigDecimal("0.01").divide(new BigDecimal("30"), 10, RoundingMode.HALF_UP);

    /** Valores calculados para um boleto em uma data. */
    public record Encargos(long diasAtraso, BigDecimal multa, BigDecimal juros, BigDecimal total) {
    }

    public Encargos calcular(Boleto boleto, LocalDate hoje) {
        long dias = boleto.diasDeAtraso(hoje);
        if (dias == 0) {
            return new Encargos(0, BigDecimal.ZERO, BigDecimal.ZERO, boleto.getValor());
        }
        BigDecimal valor = boleto.getValor();
        BigDecimal multa = valor.multiply(MULTA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal juros = valor.multiply(JUROS_AO_DIA).multiply(BigDecimal.valueOf(dias)).setScale(2, RoundingMode.HALF_UP);
        return new Encargos(dias, multa, juros, valor.add(multa).add(juros));
    }
}
