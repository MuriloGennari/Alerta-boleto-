package br.com.alertaboleto.padroes.criacionais.factorymethod;

/**
 * @note Papel no Padrão: Concrete Creator (Factory Method) - cria {@link CanalEmail}.
 */
public class FabricaCanalEmail extends FabricaCanalNotificacao {

    @Override
    protected CanalNotificacao criarCanal(String destino) {
        return new CanalEmail(destino);
    }

    @Override
    protected String normalizarDestino(String destino) {
        return destino.toLowerCase();
    }

    @Override
    protected boolean destinoValido(String destino) {
        return destino.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    }

    @Override
    public String nomeCanal() {
        return "E-mail";
    }

    @Override
    public String exemploDestino() {
        return "nome@provedor.com";
    }
}
