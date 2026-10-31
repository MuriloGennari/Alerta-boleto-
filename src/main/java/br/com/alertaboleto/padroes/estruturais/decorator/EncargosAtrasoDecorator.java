package br.com.alertaboleto.padroes.estruturais.decorator;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.servicos.CalculadoraEncargos;
import br.com.alertaboleto.util.Log;

import java.time.LocalDate;


public class EncargosAtrasoDecorator extends AlertaDecorator {

    private final CalculadoraEncargos.Encargos encargos;

    public EncargosAtrasoDecorator(Alerta alerta, Boleto boleto, LocalDate hoje, CalculadoraEncargos calculadora) {
        super(alerta);
        this.encargos = calculadora.calcular(boleto, hoje);
        Log.padrao("Decorator", "+ EncargosAtrasoDecorator (multa e juros)");
    }

    @Override
    public String conteudo() {
        if (encargos.diasAtraso() == 0) {
            return super.conteudo() + "\n-- Encargos: nenhum, boleto ainda dentro do prazo.";
        }
        return super.conteudo()
                + "\n-- Encargos por " + encargos.diasAtraso() + " dia(s) de atraso:"
                + "\n   Multa (2%): " + Boleto.moeda(encargos.multa())
                + "\n   Juros de mora (1% a.m. pro rata): " + Boleto.moeda(encargos.juros())
                + "\n   TOTAL ATUALIZADO: " + Boleto.moeda(encargos.total());
    }

    @Override
    public int prioridade() {
        return Math.min(10, super.prioridade() + (encargos.diasAtraso() > 0 ? 1 : 0));
    }
}
