package br.com.alertaboleto.util;

/**
 * Utilitário de saída no terminal.
 * <p>
 * Centraliza as mensagens que deixam "visível" quando um padrão de projeto
 * está sendo acionado, conforme exigido no enunciado (visualização dos padrões).
 * Todas as mensagens de padrão seguem o formato: {@code [PADRÃO: Nome] mensagem}.
 */
public final class Log {

    private Log() {
    }

    /** Imprime uma mensagem indicando que um padrão de projeto foi acionado. */
    public static void padrao(String nomePadrao, String mensagem) {
        System.out.println("   [PADRÃO: " + nomePadrao + "] " + mensagem);
    }

    /** Mensagem informativa comum do sistema. */
    public static void info(String mensagem) {
        System.out.println("   " + mensagem);
    }

    /** Mensagem de erro/validação. */
    public static void erro(String mensagem) {
        System.out.println("   (!) " + mensagem);
    }
}
