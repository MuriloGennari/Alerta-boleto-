package br.com.alertaboleto.padroes.comportamentais.chain;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.ResultadoAnalise;

/**
 * O beneficiário (CNPJ) está na lista de empresas que o titular já reconhece?
 *
 * @note Papel no Padrão: Concrete Handler (Chain of Responsibility)
 */
public class VerificadorBeneficiario extends VerificadorRisco {

    @Override
    protected boolean verificar(Boleto boleto, ContextoAnalise contexto, ResultadoAnalise resultado) {
        if (!contexto.getCnpjsConhecidos().contains(boleto.getBeneficiarioCnpj())) {
            resultado.adicionarRisco(25, "beneficiário " + boleto.getBeneficiarioNome()
                    + " (CNPJ " + boleto.getBeneficiarioCnpj() + ") nunca te cobrou antes");
        }
        return true;
    }

    @Override
    protected String nome() {
        return "Beneficiário conhecido";
    }
}
