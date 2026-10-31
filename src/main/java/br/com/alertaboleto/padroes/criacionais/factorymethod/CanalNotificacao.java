package br.com.alertaboleto.padroes.criacionais.factorymethod;

import br.com.alertaboleto.padroes.comportamentais.observer.ObservadorBoleto;


public abstract class CanalNotificacao implements ObservadorBoleto {

    protected final String destino;

    protected CanalNotificacao(String destino) {
        this.destino = destino;
    }


    public abstract String tipoCanal();

    @Override
    public String descricao() {
        return tipoCanal() + " -> " + destino;
    }

    protected void exibirEnvio(String cabecalho, String corpo) {
        System.out.println("   +-- [" + tipoCanal().toUpperCase() + " enviado para " + destino + "]");
        System.out.println("   | " + cabecalho);
        for (String linha : corpo.split("\n")) {
            System.out.println("   | " + linha);
        }
        System.out.println("   +--");
    }
}
