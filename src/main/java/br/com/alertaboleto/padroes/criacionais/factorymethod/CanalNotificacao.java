package br.com.alertaboleto.padroes.criacionais.factorymethod;

import br.com.alertaboleto.padroes.comportamentais.observer.ObservadorBoleto;

/**
 * Tipo genérico dos canais de notificação criados pelas fábricas.
 * Todo canal também é um Observer, para poder se inscrever no monitor de boletos.
 *
 * @note Papel no Padrão: Product (Factory Method) e Observer (Observer)
 */
public abstract class CanalNotificacao implements ObservadorBoleto {

    protected final String destino;

    protected CanalNotificacao(String destino) {
        this.destino = destino;
    }

    /** Nome do canal, ex.: "E-mail". */
    public abstract String tipoCanal();

    @Override
    public String descricao() {
        return tipoCanal() + " -> " + destino;
    }

    /** Impressão padronizada da mensagem "enviada" (simulação de envio no terminal). */
    protected void exibirEnvio(String cabecalho, String corpo) {
        System.out.println("   +-- [" + tipoCanal().toUpperCase() + " enviado para " + destino + "]");
        System.out.println("   | " + cabecalho);
        for (String linha : corpo.split("\n")) {
            System.out.println("   | " + linha);
        }
        System.out.println("   +--");
    }
}
