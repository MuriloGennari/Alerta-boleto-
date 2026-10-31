package br.com.alertaboleto.padroes.estruturais.adapter;

import br.com.alertaboleto.dominio.Boleto;

import java.util.List;

/**
 * Interface que o sistema espera de QUALQUER banco: "me dê os boletos deste CPF".
 * O serviço de alertas só conhece esta interface.
 *
 * @note Papel no Padrão: Target (Adapter)
 */
public interface FonteDeBoletos {

    String nomeFonte();

    List<Boleto> buscarPorCpf(String cpf, String nomeTitular);
}
