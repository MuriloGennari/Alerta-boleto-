package br.com.alertaboleto.dominio;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Entidade central do domínio: um boleto emitido contra o CPF do titular.
 * <p>
 * Repare que o construtor tem MUITOS parâmetros (construtor "telescópico"). Criar um
 * boleto direto por aqui é confuso e propenso a erro (trocar a ordem de dois Strings,
 * por exemplo). Por isso a criação é feita pelo {@code BoletoBuilder}, que valida
 * as regras antes de chamar este construtor.
 */
public class Boleto {

    public static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private int id;
    private final String nossoNumero;
    private final String linhaDigitavel;
    private final String beneficiarioNome;
    private final String beneficiarioCnpj;
    private final String pagadorNome;
    private final String pagadorCpf;
    private final BigDecimal valor;
    private final LocalDate emissao;
    private final LocalDate vencimento;
    private final String codigoBanco;
    private final String nomeBanco;
    private final TipoBoleto tipo;
    private final String origem;

    private StatusBoleto status;
    private ResultadoAnalise analise;
    private boolean lembreteEnviado;

    public Boleto(String nossoNumero, String linhaDigitavel, String beneficiarioNome, String beneficiarioCnpj,
                  String pagadorNome, String pagadorCpf, BigDecimal valor, LocalDate emissao,
                  LocalDate vencimento, String codigoBanco, String nomeBanco, TipoBoleto tipo,
                  String origem, StatusBoleto status) {
        this.nossoNumero = nossoNumero;
        this.linhaDigitavel = linhaDigitavel;
        this.beneficiarioNome = beneficiarioNome;
        this.beneficiarioCnpj = beneficiarioCnpj;
        this.pagadorNome = pagadorNome;
        this.pagadorCpf = pagadorCpf;
        this.valor = valor;
        this.emissao = emissao;
        this.vencimento = vencimento;
        this.codigoBanco = codigoBanco;
        this.nomeBanco = nomeBanco;
        this.tipo = tipo;
        this.origem = origem;
        this.status = status;
    }

    // ------------------------------------------------------------------ regras de negócio

    /** Dias de atraso em relação à data informada (0 se ainda está no prazo). */
    public long diasDeAtraso(LocalDate hoje) {
        return Math.max(0, ChronoUnit.DAYS.between(vencimento, hoje));
    }

    /** Dias que faltam para vencer (negativo se já venceu). */
    public long diasParaVencer(LocalDate hoje) {
        return ChronoUnit.DAYS.between(hoje, vencimento);
    }

    public boolean estaVencido(LocalDate hoje) {
        return hoje.isAfter(vencimento);
    }

    /** Boleto que ainda exige alguma ação do titular. */
    public boolean emAcompanhamento() {
        return status == StatusBoleto.ABERTO || status == StatusBoleto.VENCIDO || status == StatusBoleto.SUSPEITO;
    }

    public void marcarComoVencido() {
        if (status != StatusBoleto.ABERTO) {
            throw new IllegalStateException("Só um boleto em aberto pode passar para vencido.");
        }
        status = StatusBoleto.VENCIDO;
    }

    public void marcarComoSuspeito() {
        status = StatusBoleto.SUSPEITO;
    }

    public void marcarComoPago() {
        if (status == StatusBoleto.PAGO) {
            throw new IllegalStateException("Este boleto já está pago.");
        }
        if (status == StatusBoleto.CONTESTADO) {
            throw new IllegalStateException("Boleto contestado não pode ser pago: aguarde o resultado da contestação.");
        }
        status = StatusBoleto.PAGO;
    }

    public void contestar() {
        if (status == StatusBoleto.PAGO) {
            throw new IllegalStateException("Boleto já pago: a contestação deve ser feita junto ao banco.");
        }
        status = StatusBoleto.CONTESTADO;
    }

    /** O titular confirmou que o boleto é legítimo: volta para o fluxo normal. */
    public void reconhecerComoLegitimo() {
        if (status != StatusBoleto.SUSPEITO && status != StatusBoleto.CONTESTADO) {
            throw new IllegalStateException("Apenas boletos suspeitos ou contestados podem ser reconhecidos.");
        }
        status = StatusBoleto.ABERTO;
        analise = null;
    }

    // ------------------------------------------------------------------ exibição

    public static String moeda(BigDecimal v) {
        return MOEDA.format(v);
    }

    public String resumo(LocalDate hoje) {
        String prazo;
        if (status == StatusBoleto.PAGO || status == StatusBoleto.CONTESTADO) {
            prazo = "";
        } else if (estaVencido(hoje)) {
            prazo = " | " + diasDeAtraso(hoje) + " dia(s) de atraso";
        } else {
            prazo = " | vence em " + diasParaVencer(hoje) + " dia(s)";
        }
        return String.format("#%-2d %-34s %12s  venc. %s  [%s]%s",
                id, cortar(beneficiarioNome, 34), moeda(valor), vencimento.format(DATA_BR),
                status.getDescricao(), prazo);
    }

    private static String cortar(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    // ------------------------------------------------------------------ getters / setters

    public int getId() { return id; }

    /** O ID é atribuído uma única vez, pelo repositório, quando o boleto entra no sistema. */
    public void registrarId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("Boleto já possui ID.");
        }
        this.id = id;
    }
    public String getNossoNumero() { return nossoNumero; }
    public String getLinhaDigitavel() { return linhaDigitavel; }
    public String getBeneficiarioNome() { return beneficiarioNome; }
    public String getBeneficiarioCnpj() { return beneficiarioCnpj; }
    public String getPagadorNome() { return pagadorNome; }
    public String getPagadorCpf() { return pagadorCpf; }
    public BigDecimal getValor() { return valor; }
    public LocalDate getEmissao() { return emissao; }
    public LocalDate getVencimento() { return vencimento; }
    public String getCodigoBanco() { return codigoBanco; }
    public String getNomeBanco() { return nomeBanco; }
    public TipoBoleto getTipo() { return tipo; }
    public String getOrigem() { return origem; }
    public StatusBoleto getStatus() { return status; }
    public ResultadoAnalise getAnalise() { return analise; }
    public void setAnalise(ResultadoAnalise analise) { this.analise = analise; }
    public boolean isLembreteEnviado() { return lembreteEnviado; }
    public void setLembreteEnviado(boolean lembreteEnviado) { this.lembreteEnviado = lembreteEnviado; }
}
