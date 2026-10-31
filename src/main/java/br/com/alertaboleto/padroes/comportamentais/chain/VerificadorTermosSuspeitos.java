package br.com.alertaboleto.padroes.comportamentais.chain;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.ResultadoAnalise;

import java.text.Normalizer;
import java.util.List;


public class VerificadorTermosSuspeitos extends VerificadorRisco {

    private static final List<String> TERMOS = List.of(
            "regularizacao", "receita", "cpf", "urgente", "negativacao", "quitacao", "bloqueio", "serasa");

    @Override
    protected boolean verificar(Boleto boleto, ContextoAnalise contexto, ResultadoAnalise resultado) {
        String nome = semAcento(boleto.getBeneficiarioNome().toLowerCase());
        List<String> encontrados = TERMOS.stream().filter(nome::contains).toList();
        if (!encontrados.isEmpty()) {
            resultado.adicionarRisco(25, "nome do beneficiário contém termos típicos de golpe: " + encontrados);
        }
        return true;
    }

    private static String semAcento(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    @Override
    protected String nome() {
        return "Termos suspeitos";
    }
}
