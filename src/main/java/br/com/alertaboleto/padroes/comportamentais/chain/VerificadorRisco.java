package br.com.alertaboleto.padroes.comportamentais.chain;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.ResultadoAnalise;
import br.com.alertaboleto.util.Log;

/**
 * Elo genérico da cadeia de análise anti-golpe.
 * <p>
 * <b>Problema resolvido:</b> detectar boleto falso envolve várias regras independentes
 * (titularidade, banco, beneficiário, valor, texto). Em vez de um método gigante cheio
 * de {@code if}, cada regra vira um elo. Cada elo faz a sua verificação e DECIDE se o
 * pedido segue para o próximo ou se a análise pode parar ali. Novas regras entram
 * apenas encaixando um novo elo, sem mexer nos outros.
 *
 * @note Papel no Padrão: Handler (Chain of Responsibility)
 */
public abstract class VerificadorRisco {

    private VerificadorRisco proximo;

    /**
     * Liga o próximo elo e o devolve, permitindo montar a cadeia de forma fluente:
     * {@code a.encadear(b).encadear(c)}.
     */
    public VerificadorRisco encadear(VerificadorRisco proximo) {
        this.proximo = proximo;
        return proximo;
    }

    /** Executa este elo e, se ele permitir, repassa para o próximo. */
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

    /**
     * Regra específica do elo.
     *
     * @return {@code true} para repassar ao próximo elo, {@code false} para encerrar a cadeia
     */
    protected abstract boolean verificar(Boleto boleto, ContextoAnalise contexto, ResultadoAnalise resultado);

    protected abstract String nome();
}
