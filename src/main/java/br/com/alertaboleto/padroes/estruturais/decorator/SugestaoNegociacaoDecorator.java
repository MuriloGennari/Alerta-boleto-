package br.com.alertaboleto.padroes.estruturais.decorator;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.util.Log;

import java.time.LocalDate;

/**
 * Adicional que orienta o titular conforme o tempo de atraso: atrasos longos
 * (mais de 30 dias) recebem sugestão de renegociação e aviso sobre negativação.
 *
 * @note Papel no Padrão: Concrete Decorator
 */
public class SugestaoNegociacaoDecorator extends AlertaDecorator {

    private static final int DIAS_ATRASO_CRITICO = 30;
    private final long diasAtraso;

    public SugestaoNegociacaoDecorator(Alerta alerta, Boleto boleto, LocalDate hoje) {
        super(alerta);
        this.diasAtraso = boleto.diasDeAtraso(hoje);
        Log.padrao("Decorator", "+ SugestaoNegociacaoDecorator (orientação por tempo de atraso)");
    }

    @Override
    public String conteudo() {
        String orientacao;
        if (diasAtraso > DIAS_ATRASO_CRITICO) {
            orientacao = "\n-- ATENÇÃO: mais de " + DIAS_ATRASO_CRITICO + " dias de atraso."
                    + "\n   Risco de negativação do CPF. Pelo CDC (art. 43, §2º) você deve ser avisado"
                    + "\n   por escrito antes da inclusão. Procure o credor e proponha um acordo/parcelamento.";
        } else if (diasAtraso > 0) {
            orientacao = "\n-- Dica: pague o quanto antes; cada dia soma juros e, após "
                    + DIAS_ATRASO_CRITICO + " dias, aumenta o risco de negativação.";
        } else {
            orientacao = "\n-- Dica: agende o pagamento para não esquecer.";
        }
        return super.conteudo() + orientacao;
    }

    @Override
    public int prioridade() {
        return Math.min(10, super.prioridade() + (diasAtraso > DIAS_ATRASO_CRITICO ? 2 : 0));
    }
}
