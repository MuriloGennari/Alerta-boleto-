package br.com.alertaboleto.padroes.criacionais.factorymethod;

import br.com.alertaboleto.padroes.comportamentais.observer.EventoBoleto;

/**
 * Canal de e-mail: aceita mensagem completa, com assunto e corpo detalhado.
 *
 * @note Papel no Padrão: Concrete Product (Factory Method) / Concrete Observer (Observer)
 */
public class CanalEmail extends CanalNotificacao {

    CanalEmail(String destino) {
        super(destino);
    }

    @Override
    public String tipoCanal() {
        return "E-mail";
    }

    @Override
    public void atualizar(EventoBoleto evento) {
        exibirEnvio("Assunto: [AlertaBoleto] " + evento.getAlerta().titulo(),
                evento.getAlerta().conteudo() + "\n\nEquipe AlertaBoleto - você recebe este e-mail porque monitora seu CPF.");
    }
}
