package br.com.alertaboleto.padroes.comportamentais.chain;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.ResultadoAnalise;
import br.com.alertaboleto.util.Log;


public abstract class VerificadorRisco {

    private VerificadorRisco proximo;


    public VerificadorRisco encadear(VerificadorRisco proximo) {
        this.proximo = proximo;
        return proximo;
    }


    public final void analisar(Boleto boleto, ContextoAnalise contexto, ResultadoAnalise resultado) {
        int antes = resultado.getPontosAcumulados();
        boolean continuar = verificar(boleto, contexto, resultado);
        int ganho = resultado.getPontosAcumulados() - antes;
        Log.padrao("Chain of Responsibility", "  " + nome() + ": "
                + (ganho > 0 ? "+" + ganho + " pontos de risco" : "ok"));

        if (!continuar) {
            Log.padrao("Chain of Responsibility", "  " + nome() + " INTERROMPEU a cadeia (veredito definitivo).");
            return;
        }
        if (proximo != null) {
            proximo.analisar(boleto, contexto, resultado);
        }
    }


    protected abstract boolean verificar(Boleto boleto, ContextoAnalise contexto, ResultadoAnalise resultado);

    protected abstract String nome();
}
