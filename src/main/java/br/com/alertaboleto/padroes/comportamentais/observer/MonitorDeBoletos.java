package br.com.alertaboleto.padroes.comportamentais.observer;

import br.com.alertaboleto.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central que publica os eventos dos boletos. Ela NÃO conhece e-mail, SMS nem
 * WhatsApp: apenas mantém uma lista de observadores inscritos e avisa todos.
 * Assim, um novo canal (ex.: Telegram) pode ser criado sem alterar esta classe.
 *
 * @note Papel no Padrão: Subject / Concrete Subject (Observer)
 */
public class MonitorDeBoletos {

    private final List<ObservadorBoleto> observadores = new ArrayList<>();

    public void inscrever(ObservadorBoleto observador) {
        observadores.add(observador);
        Log.padrao("Observer", "Inscrito: " + observador.descricao() + " (total: " + observadores.size() + ")");
    }

    public void remover(ObservadorBoleto observador) {
        observadores.remove(observador);
        Log.padrao("Observer", "Removido: " + observador.descricao() + " (total: " + observadores.size() + ")");
    }

    public void notificar(EventoBoleto evento) {
        Log.padrao("Observer", "Evento '" + evento.getTipo().getDescricao() + "' do boleto #"
                + evento.getBoleto().getId() + " -> notificando " + observadores.size() + " observador(es)");
        for (ObservadorBoleto observador : observadores) {
            observador.atualizar(evento);
        }
    }

    public List<ObservadorBoleto> getObservadores() {
        return Collections.unmodifiableList(observadores);
    }
}
