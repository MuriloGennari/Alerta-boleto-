package br.com.alertaboleto.padroes.estruturais.decorator;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.LinhaDigitavel;
import br.com.alertaboleto.util.Log;


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
