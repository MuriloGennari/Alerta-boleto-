package br.com.alertaboleto.padroes.estruturais.decorator;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.LinhaDigitavel;
import br.com.alertaboleto.util.Log;

/**
 * Adicional que anexa a linha digitável formatada para o titular copiar e pagar.
 * <p>
 * Regra de segurança: se o boleto foi marcado como suspeito pela análise anti-golpe,
 * o código NÃO é anexado - o sistema nunca deve facilitar o pagamento de um golpe.
 *
 * @note Papel no Padrão: Concrete Decorator
 */
public class CodigoPagamentoDecorator extends AlertaDecorator {

    private final Boleto boleto;

    public CodigoPagamentoDecorator(Alerta alerta, Boleto boleto) {
        super(alerta);
        this.boleto = boleto;
        Log.padrao("Decorator", "+ CodigoPagamentoDecorator (linha digitável)");
    }

    @Override
    public String conteudo() {
        boolean suspeito = boleto.getAnalise() != null && boleto.getAnalise().isSuspeito();
        if (suspeito) {
            return super.conteudo() + "\n-- Linha digitável OMITIDA por segurança (boleto suspeito).";
        }
        return super.conteudo() + "\n-- Linha digitável: " + LinhaDigitavel.formatar(boleto.getLinhaDigitavel());
    }
}
