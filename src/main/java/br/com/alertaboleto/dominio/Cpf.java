package br.com.alertaboleto.dominio;

/**
 * Regras de negócio do CPF: validação dos dígitos verificadores,
 * formatação e mascaramento (LGPD - não exibir o CPF completo em alertas).
 */
public final class Cpf {

    private Cpf() {
    }

    /** Remove pontos, traço e espaços. */
    public static String somenteDigitos(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("\\D", "");
    }

    /**
     * Valida o CPF pelo algoritmo oficial dos dois dígitos verificadores (módulo 11).
     */
    public static boolean valido(String cpf) {
        String d = somenteDigitos(cpf);
        if (d.length() != 11 || d.chars().distinct().count() == 1) {
            return false; // tamanho errado ou todos os dígitos iguais (ex.: 111.111.111-11)
        }
        return digito(d, 9) == d.charAt(9) - '0' && digito(d, 10) == d.charAt(10) - '0';
    }

    private static int digito(String d, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (d.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    /** 52998224725 -> 529.982.247-25 */
    public static String formatar(String cpf) {
        String d = somenteDigitos(cpf);
        if (d.length() != 11) {
            return cpf;
        }
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
    }

    /** 52998224725 -> ***.982.247-** (exibição segura em notificações). */
    public static String mascarar(String cpf) {
        String d = somenteDigitos(cpf);
        if (d.length() != 11) {
            return "***";
        }
        return "***." + d.substring(3, 6) + "." + d.substring(6, 9) + "-**";
    }
}
