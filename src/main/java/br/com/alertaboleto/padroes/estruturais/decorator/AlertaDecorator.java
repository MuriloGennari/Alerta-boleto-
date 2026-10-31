package br.com.alertaboleto.padroes.estruturais.decorator;

/**
 * Base de todos os adicionais de alerta. Guarda uma referência para o alerta
 * embrulhado e, por padrão, apenas repassa as chamadas para ele.
 * Cada decorador concreto sobrescreve só o que quer enriquecer.
 *
 * @note Papel no Padrão: Decorator (classe abstrata base)
 */
public abstract class AlertaDecorator implements Alerta {

    protected final Alerta alertaEmbrulhado;

    protected AlertaDecorator(Alerta alertaEmbrulhado) {
        this.alertaEmbrulhado = alertaEmbrulhado;
    }

    @Override
    public String titulo() {
        return alertaEmbrulhado.titulo();
    }

    @Override
    public String conteudo() {
        return alertaEmbrulhado.conteudo();
    }

    @Override
    public int prioridade() {
        return alertaEmbrulhado.prioridade();
    }
}
