package br.com.alertaboleto.padroes.comportamentais.chain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

/**
 * Informações do titular que os verificadores da cadeia precisam consultar.
 *
 * @note Papel no Padrão: Classe de apoio (contexto/dados compartilhados entre os Handlers) da Chain of Responsibility
 */
public class ContextoAnalise {

    private final String cpfTitular;
    private final Set<String> cnpjsConhecidos;
    private final BigDecimal valorMedioHistorico;

    public ContextoAnalise(String cpfTitular, Set<String> cnpjsConhecidos, BigDecimal valorMedioHistorico) {
        this.cpfTitular = cpfTitular;
        this.cnpjsConhecidos = Collections.unmodifiableSet(cnpjsConhecidos);
        this.valorMedioHistorico = valorMedioHistorico;
    }

    public String getCpfTitular() { return cpfTitular; }
    public Set<String> getCnpjsConhecidos() { return cnpjsConhecidos; }
    public BigDecimal getValorMedioHistorico() { return valorMedioHistorico; }
}
