package br.com.alertaboleto.padroes.estruturais.adapter.externo;

import br.com.alertaboleto.dominio.LinhaDigitavel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Simula a API de uma fintech fictícia (Banco Horizonte, código 802).
 * Interface incompatível com a nossa: recebe o CPF como {@code long},
 * devolve objetos {@link CobrancaHorizonte} com nomes em inglês.
 *
 * @note Papel no Padrão: Adaptee (Adapter)
 */
public class ApiBancoHorizonte {

    public static final String BANK_CODE = "802";

    public List<CobrancaHorizonte> listCharges(long payerDocument, String payerName) {
        LocalDate hoje = LocalDate.now();
        return List.of(
                cobranca("HZ-9001", BANK_CODE, "Horizonte Financiamentos S.A.", "56.789.012/0001-34",
                        payerDocument, payerName, 1152.30, hoje.minusDays(34), hoje.minusDays(4), "OPEN", "LOAN"),
                cobranca("HZ-9002", BANK_CODE, "Colégio Saber Mais", "67.890.123/0001-45",
                        payerDocument, payerName, 890.00, hoje.minusDays(31), hoje.minusDays(1), "OPEN", "SCHOOL"),
                cobranca("HZ-9003", BANK_CODE, "Academia Corpo em Forma", "78.901.234/0001-56",
                        payerDocument, payerName, 99.90, hoje.minusDays(40), hoje.minusDays(10), "PAID", "GYM"),
                // Boleto falso: diz ser do Horizonte, mas a linha digitável começa com 999
                cobranca("HZ-9004", "999", "Central de Regularização de CPF - Receita", "99.888.777/0001-66",
                        payerDocument, payerName, 1890.00, hoje.minusDays(3), hoje.plusDays(1), "OPEN", "FEE")
        );
    }

    private CobrancaHorizonte cobranca(String id, String bancoNaLinha, String payee, String taxId, long doc,
                                       String payer, double amount, LocalDate issue, LocalDate due,
                                       String state, String category) {
        String linha = LinhaDigitavel.gerar(bancoNaLinha, id, BigDecimal.valueOf(amount), due);
        return new CobrancaHorizonte(id, linha, payee, taxId, doc, payer, amount,
                issue.toString(), due.toString(), state, category);
    }
}
