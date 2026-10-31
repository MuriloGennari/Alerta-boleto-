package br.com.alertaboleto.padroes.criacionais.factorymethod;

import br.com.alertaboleto.util.Log;


public abstract class FabricaCanalNotificacao {


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


    protected abstract CanalNotificacao criarCanal(String destino);

    protected abstract boolean destinoValido(String destino);

    protected String normalizarDestino(String destino) {
        return destino;
    }

    public abstract String nomeCanal();

    public abstract String exemploDestino();
}
