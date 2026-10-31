package br.com.alertaboleto.servicos;

import br.com.alertaboleto.dominio.Boleto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Armazena os boletos do titular em memória.
 * Evita duplicidade: o mesmo boleto (mesma linha digitável) pode chegar de
 * sincronizações repetidas ou de duas fontes diferentes.
 */
public class RepositorioBoletos {

    private final List<Boleto> boletos = new ArrayList<>();
    private int proximoId = 1;

    /** @return {@code true} se o boleto era novo e foi adicionado */
    public boolean adicionarSeNovo(Boleto boleto) {
        boolean duplicado = boletos.stream()
                .anyMatch(b -> b.getLinhaDigitavel().equals(boleto.getLinhaDigitavel()));
        if (duplicado) {
            return false;
        }
        boleto.registrarId(proximoId++);
        boletos.add(boleto);
        return true;
    }

    public Optional<Boleto> buscarPorId(int id) {
        return boletos.stream().filter(b -> b.getId() == id).findFirst();
    }

    public List<Boleto> todos() {
        return Collections.unmodifiableList(boletos);
    }

    public void limpar() {
        boletos.clear();
        proximoId = 1;
    }
}
