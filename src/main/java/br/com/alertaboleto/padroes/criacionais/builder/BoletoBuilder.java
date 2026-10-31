package br.com.alertaboleto.padroes.criacionais.builder;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.Cpf;
import br.com.alertaboleto.dominio.LinhaDigitavel;
import br.com.alertaboleto.dominio.StatusBoleto;
import br.com.alertaboleto.dominio.TipoBoleto;
import br.com.alertaboleto.util.Log;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class BoletoBuilder {

    private static int contadorNossoNumero = 1000;

    private String nossoNumero;
    private String linhaDigitavel;
    private String beneficiarioNome;
    private String beneficiarioCnpj;
    private String pagadorNome;
    private String pagadorCpf;
    private BigDecimal valor;
    private LocalDate emissao;
    private LocalDate vencimento;
    private String codigoBanco;
    private String nomeBanco;
    private TipoBoleto tipo = TipoBoleto.OUTROS;
    private String origem = "Cadastro manual";
    private StatusBoleto status = StatusBoleto.ABERTO;

    private final List<String> padroesAplicados = new ArrayList<>();

    private BoletoBuilder() {
    }


    public static BoletoBuilder novoBoleto() {
        return new BoletoBuilder();
    }

    public BoletoBuilder doBeneficiario(String nome, String cnpj) {
        this.beneficiarioNome = nome;
        this.beneficiarioCnpj = cnpj;
        return this;
    }

    public BoletoBuilder paraOPagador(String nome, String cpf) {
        this.pagadorNome = nome;
        this.pagadorCpf = Cpf.somenteDigitos(cpf);
        return this;
    }

    public BoletoBuilder noValorDe(BigDecimal valor) {
        this.valor = valor;
        return this;
    }

    public BoletoBuilder emitidoEm(LocalDate emissao) {
        this.emissao = emissao;
        return this;
    }

    public BoletoBuilder comVencimentoEm(LocalDate vencimento) {
        this.vencimento = vencimento;
        return this;
    }

    public BoletoBuilder doBanco(String codigo, String nome) {
        this.codigoBanco = codigo;
        this.nomeBanco = nome;
        return this;
    }

    public BoletoBuilder comLinhaDigitavel(String linha) {
        this.linhaDigitavel = linha == null ? null : linha.replaceAll("\\D", "");
        return this;
    }

    public BoletoBuilder comNossoNumero(String nossoNumero) {
        this.nossoNumero = nossoNumero;
        return this;
    }

    public BoletoBuilder doTipo(TipoBoleto tipo) {
        this.tipo = tipo;
        return this;
    }

    public BoletoBuilder vindoDe(String origem) {
        this.origem = origem;
        return this;
    }

    public BoletoBuilder comStatus(StatusBoleto status) {
        this.status = status;
        return this;
    }


    public Boleto construir() {
        aplicarPadroes();

        List<String> erros = new ArrayList<>();
        if (vazio(beneficiarioNome)) erros.add("beneficiário é obrigatório");
        if (vazio(pagadorNome)) erros.add("nome do pagador é obrigatório");
        if (!Cpf.valido(pagadorCpf)) erros.add("CPF do pagador inválido");
        if (valor == null || valor.signum() <= 0) erros.add("valor deve ser maior que zero");
        if (vencimento == null) erros.add("data de vencimento é obrigatória");
        if (codigoBanco == null || !codigoBanco.matches("\\d{3}")) erros.add("código do banco deve ter 3 dígitos");
        if (vencimento != null && emissao != null && vencimento.isBefore(emissao)) {
            erros.add("vencimento não pode ser anterior à emissão");
        }
        if (!LinhaDigitavel.formatoValido(linhaDigitavel)) erros.add("linha digitável deve ter 47 dígitos");

        if (!erros.isEmpty()) {
            Log.padrao("Builder", "Construção recusada: " + String.join("; ", erros));
            throw new IllegalArgumentException(String.join("; ", erros));
        }

        Boleto boleto = new Boleto(nossoNumero, linhaDigitavel, beneficiarioNome, beneficiarioCnpj,
                pagadorNome, pagadorCpf, valor, emissao, vencimento, codigoBanco, nomeBanco, tipo, origem, status);

        Log.padrao("Builder", "Boleto " + nossoNumero + " montado (" + beneficiarioNome + ")"
                + (padroesAplicados.isEmpty() ? "" : " - padrões aplicados: " + String.join(", ", padroesAplicados)));
        return boleto;
    }

    private void aplicarPadroes() {
        if (vazio(nossoNumero)) {
            nossoNumero = "NN" + (++contadorNossoNumero);
            padroesAplicados.add("nosso número");
        }
        if (vencimento != null && emissao == null) {
            emissao = vencimento.minusDays(30);
            padroesAplicados.add("emissão = vencimento - 30 dias");
        }
        if (vazio(nomeBanco)) {
            nomeBanco = "Banco " + codigoBanco;
        }
        if (vazio(beneficiarioCnpj)) {
            beneficiarioCnpj = "não informado";
        }
        if (vazio(linhaDigitavel) && valor != null && vencimento != null && codigoBanco != null) {
            linhaDigitavel = LinhaDigitavel.gerar(codigoBanco, nossoNumero, valor, vencimento);
            padroesAplicados.add("linha digitável gerada");
        }
    }

    private static boolean vazio(String s) {
        return s == null || s.isBlank();
    }
}
