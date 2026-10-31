package br.com.alertaboleto.padroes.estruturais.decorator;


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
