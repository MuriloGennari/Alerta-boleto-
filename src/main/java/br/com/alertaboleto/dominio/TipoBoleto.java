package br.com.alertaboleto.dominio;

/**
 * Natureza da cobrança representada pelo boleto.
 */
public enum TipoBoleto {
    CONSUMO("Conta de consumo (luz, água, internet)"),
    CONDOMINIO("Condomínio"),
    FINANCIAMENTO("Financiamento / Empréstimo"),
    MENSALIDADE("Mensalidade (escola, academia, curso)"),
    TAXA("Taxa / Tributo"),
    OUTROS("Outros");

    private final String descricao;

    TipoBoleto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
