package br.com.alertaboleto.padroes.comportamentais.observer;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HistoricoAlertasObserver implements ObservadorBoleto {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");
    private final List<String> registros = new ArrayList<>();

    @Override
    public void atualizar(EventoBoleto evento) {
        registros.add(evento.getMomento().format(HORA) + " | " + evento.getTipo().getDescricao()
                + " | boleto #" + evento.getBoleto().getId() + " | prioridade " + evento.getAlerta().prioridade()
                + " | " + evento.getAlerta().titulo());
    }

    @Override
    public String descricao() {
        return "Histórico de alertas (auditoria)";
    }

    public List<String> getRegistros() {
        return Collections.unmodifiableList(registros);
    }
}
