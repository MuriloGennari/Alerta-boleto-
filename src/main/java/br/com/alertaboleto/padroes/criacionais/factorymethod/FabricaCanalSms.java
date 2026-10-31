package br.com.alertaboleto.padroes.criacionais.factorymethod;

/**
 * @note Papel no Padrão: Concrete Creator (Factory Method) - cria {@link CanalSms}.
 */
public class FabricaCanalSms extends FabricaCanalNotificacao {

    @Override
    protected CanalNotificacao criarCanal(String destino) {
        return new CanalSms(destino);
    }

    /** Remove tudo que não é dígito e formata como (41) 99999-9999. */
    @Override
    protected String normalizarDestino(String destino) {
        String d = destino.replaceAll("\\D", "");
        if (d.length() == 11) {
            return "(" + d.substring(0, 2) + ") " + d.substring(2, 7) + "-" + d.substring(7);
        }
        return d;
    }

    /** Celular brasileiro: DDD (11 a 99) + 9 + 8 dígitos. */
    @Override
    protected boolean destinoValido(String destino) {
        return destino.matches("\\([1-9][1-9]\\) 9\\d{4}-\\d{4}");
    }

    @Override
    public String nomeCanal() {
        return "SMS";
    }

    @Override
    public String exemploDestino() {
        return "celular com DDD, ex.: 41999998888";
    }
}
