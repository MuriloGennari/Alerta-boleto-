package br.com.alertaboleto.dominio;

/**
 * Situação de um boleto dentro do monitoramento.
 */
public enum StatusBoleto {
    /** Boleto em aberto e ainda dentro do prazo. */
    ABERTO("Em aberto"),
    /** Vencimento já passou e não foi pago. */
    VENCIDO("Vencido"),
    /** A análise anti-golpe encontrou indícios de fraude. */
    SUSPEITO("Suspeito de golpe"),
    /** Pagamento confirmado pelo titular. */
    PAGO("Pago"),
    /** O titular não reconhece o boleto e abriu contestação. */
    CONTESTADO("Contestado");

    private final String descricao;

    StatusBoleto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
