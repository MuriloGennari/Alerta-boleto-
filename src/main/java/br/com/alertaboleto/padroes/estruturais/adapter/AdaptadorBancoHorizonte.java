package br.com.alertaboleto.padroes.estruturais.adapter;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.Cpf;
import br.com.alertaboleto.dominio.StatusBoleto;
import br.com.alertaboleto.dominio.TipoBoleto;
import br.com.alertaboleto.padroes.criacionais.builder.BoletoBuilder;
import br.com.alertaboleto.padroes.estruturais.adapter.externo.ApiBancoHorizonte;
import br.com.alertaboleto.padroes.estruturais.adapter.externo.CobrancaHorizonte;
import br.com.alertaboleto.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class AdaptadorBancoHorizonte implements FonteDeBoletos {

    private final ApiBancoHorizonte api;

    public AdaptadorBancoHorizonte(ApiBancoHorizonte api) {
        this.api = api;
    }

    @Override
    public String nomeFonte() {
        return "Banco Horizonte (API)";
    }

    @Override
    public List<Boleto> buscarPorCpf(String cpf, String nomeTitular) {
        List<CobrancaHorizonte> cobrancas = api.listCharges(Long.parseLong(Cpf.somenteDigitos(cpf)), nomeTitular);
        Log.padrao("Adapter", "AdaptadorBancoHorizonte: " + cobrancas.size()
                + " objeto(s) CobrancaHorizonte (inglês, Double, ISO, OPEN/PAID) -> convertendo para Boleto");

        List<Boleto> boletos = new ArrayList<>();
        for (CobrancaHorizonte c : cobrancas) {
            boletos.add(BoletoBuilder.novoBoleto()
                    .comNossoNumero(c.getChargeId())
                    .comLinhaDigitavel(c.getBarcodeLine())
                    .doBeneficiario(c.getPayeeName(), c.getPayeeTaxId())
                    .paraOPagador(c.getPayerName(), String.format("%011d", c.getPayerDocument()))
                    .noValorDe(BigDecimal.valueOf(c.getAmount()).setScale(2, RoundingMode.HALF_UP))
                    .emitidoEm(LocalDate.parse(c.getIssueDate()))
                    .comVencimentoEm(LocalDate.parse(c.getDueDate()))
                    .doTipo(converterCategoria(c.getCategory()))
                    .comStatus("PAID".equals(c.getState()) ? StatusBoleto.PAGO : StatusBoleto.ABERTO)
                    .doBanco(ApiBancoHorizonte.BANK_CODE, "Banco Horizonte")
                    .vindoDe(nomeFonte())
                    .construir());
        }
        return boletos;
    }

    private TipoBoleto converterCategoria(String categoria) {
        return switch (categoria) {
            case "UTILITY" -> TipoBoleto.CONSUMO;
            case "LOAN" -> TipoBoleto.FINANCIAMENTO;
            case "SCHOOL", "GYM" -> TipoBoleto.MENSALIDADE;
            case "FEE" -> TipoBoleto.TAXA;
            default -> TipoBoleto.OUTROS;
        };
    }
}
