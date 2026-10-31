package br.com.alertaboleto.padroes.estruturais.decorator;

/**
 * Alerta simples de texto livre, usado para confirmações (pagamento registrado,
 * contestação aberta). Também pode ser decorado, pois implementa {@link Alerta}.
 *
 * @note Papel no Padrão: Concrete Component (Decorator)
 */
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
