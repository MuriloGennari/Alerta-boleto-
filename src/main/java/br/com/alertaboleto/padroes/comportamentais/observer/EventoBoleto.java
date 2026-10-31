package br.com.alertaboleto.padroes.comportamentais.observer;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.padroes.estruturais.decorator.Alerta;

import java.time.LocalDateTime;


public class EventoBoleto {

    private final TipoEvento tipo;
    private final Boleto boleto;
    private final Alerta alerta;
    private final LocalDateTime momento = LocalDateTime.now();

    public EventoBoleto(TipoEvento tipo, Boleto boleto, Alerta alerta) {
        this.tipo = tipo;
        this.boleto = boleto;
        this.alerta = alerta;
    }

    public TipoEvento getTipo() { return tipo; }
    public Boleto getBoleto() { return boleto; }
    public Alerta getAlerta() { return alerta; }
    public LocalDateTime getMomento() { return momento; }
}
