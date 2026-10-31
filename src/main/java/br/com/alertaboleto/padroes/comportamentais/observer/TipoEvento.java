package br.com.alertaboleto.padroes.comportamentais.observer;


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
