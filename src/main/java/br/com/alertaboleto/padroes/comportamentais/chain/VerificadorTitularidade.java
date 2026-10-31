package br.com.alertaboleto.padroes.comportamentais.chain;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.Cpf;
import br.com.alertaboleto.dominio.ResultadoAnalise;


public class VerificadorTitularidade extends VerificadorRisco {

    @Override
    protected boolean verificar(Boleto boleto, ContextoAnalise contexto, ResultadoAnalise resultado) {
        if (!boleto.getPagadorCpf().equals(contexto.getCpfTitular())) {
            resultado.adicionarRisco(100, "CPF do pagador (" + Cpf.mascarar(boleto.getPagadorCpf())
                    + ") é diferente do seu - boleto emitido em nome de outra pessoa ou com seus dados trocados");
            return false;
        }
        return true;
    }

    @Override
    protected String nome() {
        return "Titularidade do CPF";
    }
}
