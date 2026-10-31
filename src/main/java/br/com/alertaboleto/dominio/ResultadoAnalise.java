package br.com.alertaboleto.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado da análise anti-golpe de um boleto: pontuação de risco (0 a 100)
 * e os motivos encontrados por cada verificador da cadeia.
 */
public class ResultadoAnalise {

    /** A partir desta pontuação o boleto é tratado como suspeito de golpe. */
    public static final int LIMITE_SUSPEITA = 50;

    private int pontuacao;
    private final List<String> motivos = new ArrayList<>();

    public void adicionarRisco(int pontos, String motivo) {
        pontuacao += pontos;
        motivos.add(motivo + " (+" + pontos + ")");
    }

    /** Soma bruta dos pontos (pode passar de 100), usada para medir o peso de cada verificador. */
    public int getPontosAcumulados() {
        return pontuacao;
    }

    /** Pontuação exibida ao usuário, limitada a 100. */
    public int getPontuacao() {
        return Math.min(100, pontuacao);
    }

    public List<String> getMotivos() {
        return Collections.unmodifiableList(motivos);
    }

    public boolean isSuspeito() {
        return pontuacao >= LIMITE_SUSPEITA;
    }

    public String getNivel() {
        if (pontuacao >= LIMITE_SUSPEITA) {
            return "ALTO";
        }
        return pontuacao >= 20 ? "MÉDIO" : "BAIXO";
    }
}
