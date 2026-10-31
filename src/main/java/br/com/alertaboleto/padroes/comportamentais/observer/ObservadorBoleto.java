package br.com.alertaboleto.padroes.comportamentais.observer;


public interface ObservadorBoleto {


    void atualizar(EventoBoleto evento);


    String descricao();
}
