package br.com.alertaboleto.padroes.estruturais.adapter.externo;

/**
 * Objeto de resposta da API do Banco Horizonte (fintech fictícia).
 * Campos em inglês, valor em {@code Double}, datas em texto ISO e status próprio.
 *
 * @note Papel no Padrão: Classe de apoio do Adaptee (formato de dados incompatível que o Adapter precisa traduzir)
 */
public class CobrancaHorizonte {

    private final String chargeId;
    private final String barcodeLine;
    private final String payeeName;
    private final String payeeTaxId;
    private final long payerDocument;
    private final String payerName;
    private final Double amount;
    private final String issueDate;
    private final String dueDate;
    private final String state;    // OPEN | PAID
    private final String category; // UTILITY | LOAN | SCHOOL | FEE | GYM

    public CobrancaHorizonte(String chargeId, String barcodeLine, String payeeName, String payeeTaxId,
                             long payerDocument, String payerName, Double amount, String issueDate,
                             String dueDate, String state, String category) {
        this.chargeId = chargeId;
        this.barcodeLine = barcodeLine;
        this.payeeName = payeeName;
        this.payeeTaxId = payeeTaxId;
        this.payerDocument = payerDocument;
        this.payerName = payerName;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.state = state;
        this.category = category;
    }

    public String getChargeId() { return chargeId; }
    public String getBarcodeLine() { return barcodeLine; }
    public String getPayeeName() { return payeeName; }
    public String getPayeeTaxId() { return payeeTaxId; }
    public long getPayerDocument() { return payerDocument; }
    public String getPayerName() { return payerName; }
    public Double getAmount() { return amount; }
    public String getIssueDate() { return issueDate; }
    public String getDueDate() { return dueDate; }
    public String getState() { return state; }
    public String getCategory() { return category; }
}
