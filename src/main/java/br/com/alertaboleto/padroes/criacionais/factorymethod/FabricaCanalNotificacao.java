package br.com.alertaboleto.padroes.criacionais.factorymethod;

import br.com.alertaboleto.util.Log;

/**
 * Criador abstrato dos canais de notificação.
 * <p>
 * <b>Problema resolvido:</b> cada canal tem regras próprias de destino (e-mail precisa de @,
 * celular precisa de DDD + 9 dígitos) e de construção. O método {@link #novoCanal(String)}
 * tem o fluxo comum (normalizar -> validar -> criar -> registrar log) e delega para as
 * subclasses SOMENTE a decisão de "qual objeto criar", através do método fábrica
 * {@link #criarCanal(String)}. O Main escolhe a fábrica e nunca dá {@code new} em um canal.
 *
 * @note Papel no Padrão: Creator (Factory Method)
 */
public abstract class FabricaCanalNotificacao {

    /**
     * Operação do criador que usa o método fábrica.
     *
     * @throws IllegalArgumentException se o destino não for válido para o canal
     */
    public final CanalNotificacao novoCanal(String destinoInformado) {
        String destino = normalizarDestino(destinoInformado == null ? "" : destinoInformado.trim());
        if (!destinoValido(destino)) {
            throw new IllegalArgumentException("Destino inválido para " + nomeCanal() + ": " + destinoInformado
                    + " (esperado: " + exemploDestino() + ")");
        }
        CanalNotificacao canal = criarCanal(destino); // <- chamada ao Factory Method
        Log.padrao("Factory Method", getClass().getSimpleName() + ".criarCanal() produziu "
                + canal.getClass().getSimpleName() + " para " + destino);
        return canal;
    }

    /** O MÉTODO FÁBRICA: cada subclasse decide qual canal concreto instanciar. */
    protected abstract CanalNotificacao criarCanal(String destino);

    protected abstract boolean destinoValido(String destino);

    protected String normalizarDestino(String destino) {
        return destino;
    }

    public abstract String nomeCanal();

    public abstract String exemploDestino();
}
