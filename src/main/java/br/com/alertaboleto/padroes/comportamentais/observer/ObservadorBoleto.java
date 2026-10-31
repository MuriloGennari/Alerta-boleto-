package br.com.alertaboleto.padroes.comportamentais.observer;

/**
 * Qualquer objeto interessado em saber o que acontece com os boletos do titular
 * (canais de e-mail, SMS, WhatsApp, histórico...).
 *
 * @note Papel no Padrão: Observer (interface)
 */
public interface ObservadorBoleto {

    /** Chamado pelo monitor sempre que um evento relevante acontece. */
    void atualizar(EventoBoleto evento);

    /** Nome amigável para exibição no menu e nos logs. */
    String descricao();
}
