package br.com.alertaboleto.padroes.estruturais.adapter.externo;

import br.com.alertaboleto.dominio.LinhaDigitavel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Simula o sistema LEGADO de um banco fictício (Banco Aurora, código 801).
 * <p>
 * Ele não pode ser alterado (é "de terceiros") e tem uma interface totalmente
 * incompatível com o nosso sistema:
 * <ul>
 *   <li>devolve um array de Strings separadas por ponto e vírgula;</li>
 *   <li>usa a nomenclatura antiga de boleto: CEDENTE (quem cobra) e SACADO (quem paga);</li>
 *   <li>valores em CENTAVOS e datas no formato ddMMyyyy;</li>
 *   <li>a busca é "aproximada" e às vezes traz boletos de homônimos (outro CPF!).</li>
 * </ul>
 *
 * @note Papel no Padrão: Adaptee (Adapter)
 */
public class SistemaLegadoBancoAurora {

    public static final String CODIGO_BANCO = "801";
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("ddMMyyyy");

    /** Layout: NOSSO_NUM;LINHA;CEDENTE;CNPJ_CEDENTE;CPF_SACADO;NOME_SACADO;VALOR_CENT;EMISSAO;VENCTO;ESPECIE */
    public String[] consultarTitulosSacado(String documentoSacado, String nomeSacado) {
        LocalDate hoje = LocalDate.now();
        List<String> linhas = new ArrayList<>();
        linhas.add(registro("AUR-50871", "Luz & Força do Sul S.A.", "12.345.678/0001-90",
                documentoSacado, nomeSacado, 28745, hoje.minusDays(42), hoje.minusDays(12), "ENE"));
        linhas.add(registro("AUR-50872", "Condomínio Residencial Araucárias", "23.456.789/0001-01",
                documentoSacado, nomeSacado, 64000, hoje.minusDays(68), hoje.minusDays(38), "CND"));
        linhas.add(registro("AUR-50873", "NetVale Telecom Ltda", "34.567.890/0001-12",
                documentoSacado, nomeSacado, 11990, hoje.minusDays(28), hoje.plusDays(2), "TEL"));
        // Homônimo trazido pela busca aproximada por nome: CPF diferente do titular!
        linhas.add(registro("AUR-50874", "Mega Eletro Comércio Ltda", "45.678.901/0001-23",
                "11144477735", nomeSacado, 239900, hoje.minusDays(50), hoje.minusDays(20), "DM"));
        return linhas.toArray(new String[0]);
    }

    private String registro(String nossoNumero, String cedente, String cnpj, String cpf, String nome,
                            long centavos, LocalDate emissao, LocalDate vencimento, String especie) {
        String linha = LinhaDigitavel.gerar(CODIGO_BANCO, nossoNumero, BigDecimal.valueOf(centavos, 2), vencimento);
        return String.join(";", nossoNumero, linha, cedente, cnpj, cpf, nome.toUpperCase(),
                String.valueOf(centavos), emissao.format(DATA), vencimento.format(DATA), especie);
    }
}
