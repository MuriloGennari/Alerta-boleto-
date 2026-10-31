package br.com.alertaboleto.dominio;


public enum StatusBoleto {

    ABERTO("Em aberto"),

    VENCIDO("Vencido"),

    SUSPEITO("Suspeito de golpe"),

    PAGO("Pago"),

    CONTESTADO("Contestado");

    private final String descricao;

    StatusBoleto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
