package br.com.alertaboleto.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ResultadoAnalise {


    public static final int LIMITE_SUSPEITA = 50;

    private int pontuacao;
    private final List<String> motivos = new ArrayList<>();

    public void adicionarRisco(int pontos, String motivo) {
        pontuacao += pontos;
        motivos.add(motivo + " (+" + pontos + ")");
    }


    public int getPontosAcumulados() {
        return pontuacao;
    }


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
