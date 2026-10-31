package br.com.alertaboleto.padroes.comportamentais.chain;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.ResultadoAnalise;

import java.math.BigDecimal;


public class VerificadorValorAtipico extends VerificadorRisco {

    private static final BigDecimal FATOR = new BigDecimal("3");

    @Override
    protected boolean verificar(Boleto boleto, ContextoAnalise contexto, ResultadoAnalise resultado) {
        BigDecimal media = contexto.getValorMedioHistorico();
        if (media.signum() > 0 && boleto.getValor().compareTo(media.multiply(FATOR)) > 0) {
            resultado.adicionarRisco(20, "valor " + Boleto.moeda(boleto.getValor())
                    + " é mais que 3x a sua média (" + Boleto.moeda(media) + ")");
        }
        return true;
    }

    @Override
    protected String nome() {
        return "Valor atípico";
    }
}
