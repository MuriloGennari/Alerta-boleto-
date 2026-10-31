package br.com.alertaboleto.padroes.estruturais.decorator;


public class AlertaInformativo implements Alerta {

    private final String titulo;
    private final String conteudo;
    private final int prioridade;

    public AlertaInformativo(String titulo, String conteudo, int prioridade) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.prioridade = prioridade;
    }

    @Override
    public String titulo() {
        return titulo;
    }

    @Override
    public String conteudo() {
        return conteudo;
    }

    @Override
    public int prioridade() {
        return prioridade;
    }
}
