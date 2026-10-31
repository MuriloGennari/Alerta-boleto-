package br.com.alertaboleto.servicos;

import br.com.alertaboleto.dominio.Boleto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class RepositorioBoletos {

    private final List<Boleto> boletos = new ArrayList<>();
    private int proximoId = 1;


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
