package br.com.alertaboleto.padroes.estruturais.decorator;

/**
 * Contrato de uma mensagem de alerta sobre um boleto.
 * <p>
 * Tanto o alerta "puro" quanto qualquer alerta decorado obedecem a esta interface;
 * por isso os canais de notificação não sabem (nem precisam saber) quantos
 * decoradores foram empilhados.
 *
 * @note Papel no Padrão: Component (Decorator)
 */
public interface Alerta {

    /** Linha de assunto/título do alerta. */
    String titulo();

    /** Corpo completo da mensagem. */
    String conteudo();

    /** Prioridade de 1 (informativo) a 10 (crítico). */
    int prioridade();
}
