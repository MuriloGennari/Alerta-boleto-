package br.com.alertaboleto.util;


public final class Log {

    private Log() {
    }


    public static void padrao(String nomePadrao, String mensagem) {
        System.out.println("   [PADRÃO: " + nomePadrao + "] " + mensagem);
    }


    public static void info(String mensagem) {
        System.out.println("   " + mensagem);
    }

    public static void erro(String mensagem) {
        System.out.println("   (!) " + mensagem);
    }
}
