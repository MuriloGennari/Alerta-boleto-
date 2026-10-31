package br.com.alertaboleto.padroes.criacionais.factorymethod;


public class FabricaCanalWhatsApp extends FabricaCanalNotificacao {

    @Override
    protected CanalNotificacao criarCanal(String destino) {
        return new CanalWhatsApp(destino);
    }


    @Override
    protected String normalizarDestino(String destino) {
        String d = destino.replaceAll("\\D", "");
        if (d.length() == 11) {
            d = "55" + d;
        }
        return "+" + d;
    }

    @Override
    protected boolean destinoValido(String destino) {
        return destino.matches("\\+55[1-9][1-9]9\\d{8}");
    }

    @Override
    public String nomeCanal() {
        return "WhatsApp";
    }

    @Override
    public String exemploDestino() {
        return "celular com DDD, ex.: 41999998888";
    }
}
