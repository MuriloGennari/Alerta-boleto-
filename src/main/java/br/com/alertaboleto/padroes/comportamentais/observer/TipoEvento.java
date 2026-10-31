package br.com.alertaboleto.padroes.comportamentais.observer;

/**
 * Tipos de acontecimento que o monitor de boletos publica para os observadores.
 *
 * @note Papel no Padrão: Classe de apoio do Observer (tipos de evento publicados pelo Subject)
 */
public enum TipoEvento {
    BOLETO_VENCIDO("Boleto vencido"),
    LEMBRETE_VENCIMENTO("Lembrete de vencimento"),
    SUSPEITA_GOLPE("Suspeita de golpe"),
    BOLETO_PAGO("Pagamento registrado"),
    BOLETO_CONTESTADO("Boleto contestado");

    private final String descricao;

    TipoEvento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
