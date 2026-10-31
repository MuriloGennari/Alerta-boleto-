package br.com.alertaboleto.padroes.estruturais.adapter;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.Cpf;
import br.com.alertaboleto.dominio.TipoBoleto;
import br.com.alertaboleto.padroes.criacionais.builder.BoletoBuilder;
import br.com.alertaboleto.padroes.estruturais.adapter.externo.SistemaLegadoBancoAurora;
import br.com.alertaboleto.util.Log;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class AdaptadorBancoAurora implements FonteDeBoletos {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("ddMMyyyy");
    private final SistemaLegadoBancoAurora legado;

    public AdaptadorBancoAurora(SistemaLegadoBancoAurora legado) {
        this.legado = legado;
    }

    @Override
    public String nomeFonte() {
        return "Banco Aurora (sistema legado)";
    }

    @Override
    public List<Boleto> buscarPorCpf(String cpf, String nomeTitular) {
        String[] registros = legado.consultarTitulosSacado(Cpf.somenteDigitos(cpf), nomeTitular);
        Log.padrao("Adapter", "AdaptadorBancoAurora: " + registros.length
                + " registro(s) em texto ';' (cedente/sacado, centavos, ddMMyyyy) -> convertendo para Boleto");

        List<Boleto> boletos = new ArrayList<>();
        for (String registro : registros) {
            String[] c = registro.split(";");
            boletos.add(BoletoBuilder.novoBoleto()
                    .comNossoNumero(c[0])
                    .comLinhaDigitavel(c[1])
                    .doBeneficiario(c[2], c[3])                // cedente  -> beneficiário
                    .paraOPagador(capitalizar(c[5]), c[4])     // sacado   -> pagador
                    .noValorDe(BigDecimal.valueOf(Long.parseLong(c[6]), 2)) // centavos -> reais
                    .emitidoEm(LocalDate.parse(c[7], DATA))
                    .comVencimentoEm(LocalDate.parse(c[8], DATA))
                    .doTipo(converterEspecie(c[9]))
                    .doBanco(SistemaLegadoBancoAurora.CODIGO_BANCO, "Banco Aurora")
                    .vindoDe(nomeFonte())
                    .construir());
        }
        return boletos;
    }

    private TipoBoleto converterEspecie(String especie) {
        return switch (especie) {
            case "ENE", "TEL", "AGU" -> TipoBoleto.CONSUMO;
            case "CND" -> TipoBoleto.CONDOMINIO;
            case "DM" -> TipoBoleto.OUTROS;
            default -> TipoBoleto.OUTROS;
        };
    }

    private String capitalizar(String nome) {
        StringBuilder sb = new StringBuilder();
        for (String parte : nome.toLowerCase().split(" ")) {
            if (!parte.isEmpty()) {
                sb.append(Character.toUpperCase(parte.charAt(0))).append(parte.substring(1)).append(' ');
            }
        }
        return sb.toString().trim();
    }
}
