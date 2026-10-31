package br.com.alertaboleto.padroes.estruturais.adapter;

import br.com.alertaboleto.dominio.Boleto;

import java.util.List;


public interface FonteDeBoletos {

    String nomeFonte();

    List<Boleto> buscarPorCpf(String cpf, String nomeTitular);
}
