package br.com.alertaboleto.padroes.comportamentais.chain;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.LinhaDigitavel;
import br.com.alertaboleto.dominio.ResultadoAnalise;

/**
 * Os 3 primeiros dígitos da linha digitável devem ser o código do banco que diz ter
 * emitido o boleto. Divergência é o sinal clássico de boleto adulterado.
 *
 * @note Papel no Padrão: Concrete Handler (Chain of Responsibility)
 */
public class VerificadorCodigoBanco extends VerificadorRisco {

    @Override
    protected boolean verificar(Boleto boleto, ContextoAnalise contexto, ResultadoAnalise resultado) {
        String codigoNaLinha = LinhaDigitavel.codigoBanco(boleto.getLinhaDigitavel());
        if (!codigoNaLinha.equals(boleto.getCodigoBanco())) {
            resultado.adicionarRisco(40, "linha digitável aponta para o banco " + codigoNaLinha
                    + ", mas o boleto diz ser do banco " + boleto.getCodigoBanco());
        }
        return true;
    }

    @Override
    protected String nome() {
        return "Código do banco x linha digitável";
    }
}
