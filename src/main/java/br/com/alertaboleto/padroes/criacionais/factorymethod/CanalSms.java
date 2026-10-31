package br.com.alertaboleto.padroes.criacionais.factorymethod;

import br.com.alertaboleto.padroes.comportamentais.observer.EventoBoleto;
import br.com.alertaboleto.padroes.comportamentais.observer.TipoEvento;


public class CanalSms extends CanalNotificacao {

    private static final int LIMITE_CARACTERES = 160;
    private static final int PRIORIDADE_MINIMA = 4;

    CanalSms(String destino) {
        super(destino);
    }

    @Override
    public String tipoCanal() {
        return "SMS";
    }

    @Override
    public void atualizar(EventoBoleto evento) {
        boolean importante = evento.getAlerta().prioridade() >= PRIORIDADE_MINIMA
                || evento.getTipo() == TipoEvento.SUSPEITA_GOLPE;
        if (!importante) {
            System.out.println("   (SMS " + destino + ") ignorado: prioridade " + evento.getAlerta().prioridade()
                    + " abaixo do mínimo " + PRIORIDADE_MINIMA + " para envio pago.");
            return;
        }
        String texto = "AlertaBoleto: " + evento.getAlerta().titulo() + ". Veja detalhes no app.";
        if (texto.length() > LIMITE_CARACTERES) {
            texto = texto.substring(0, LIMITE_CARACTERES - 3) + "...";
        }
        exibirEnvio("(" + texto.length() + "/" + LIMITE_CARACTERES + " caracteres)", texto);
    }
}
