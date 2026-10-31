package br.com.alertaboleto.padroes.criacionais.factorymethod;

import br.com.alertaboleto.padroes.comportamentais.observer.EventoBoleto;
import br.com.alertaboleto.padroes.comportamentais.observer.TipoEvento;


public class CanalWhatsApp extends CanalNotificacao {

    CanalWhatsApp(String destino) {
        super(destino);
    }

    @Override
    public String tipoCanal() {
        return "WhatsApp";
    }

    @Override
    public void atualizar(EventoBoleto evento) {
        // Atalho de resposta só faz sentido em cobranças legítimas ainda pendentes
        boolean cobrancaPendente = evento.getTipo() == TipoEvento.BOLETO_VENCIDO
                || evento.getTipo() == TipoEvento.LEMBRETE_VENCIMENTO;
        exibirEnvio("*" + evento.getAlerta().titulo() + "*", evento.getAlerta().conteudo()
                + (cobrancaPendente ? "\n_Responda PAGO para registrar o pagamento._" : ""));
    }
}
