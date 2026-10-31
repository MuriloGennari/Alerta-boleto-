package br.com.alertaboleto.padroes.estruturais.decorator;

import br.com.alertaboleto.dominio.ResultadoAnalise;
import br.com.alertaboleto.util.Log;


public class AvisoGolpeDecorator extends AlertaDecorator {

    private final ResultadoAnalise analise;

    public AvisoGolpeDecorator(Alerta alerta, ResultadoAnalise analise) {
        super(alerta);
        this.analise = analise;
        Log.padrao("Decorator", "+ AvisoGolpeDecorator (resultado anti-golpe)");
    }

    private boolean suspeito() {
        return analise != null && analise.isSuspeito();
    }

    @Override
    public String titulo() {
        return suspeito() ? "[!] POSSÍVEL GOLPE - " + super.titulo() : super.titulo();
    }

    @Override
    public String conteudo() {
        if (analise == null) {
            return super.conteudo() + "\n-- Análise anti-golpe: ainda não realizada.";
        }
        if (!suspeito()) {
            return super.conteudo() + "\n-- Análise anti-golpe: risco " + analise.getNivel()
                    + " (" + analise.getPontuacao() + "/100). Nenhum indício relevante.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("NÃO PAGUE este boleto antes de confirmar com o credor por um canal oficial!\n");
        sb.append("Risco ").append(analise.getNivel()).append(" (").append(analise.getPontuacao()).append("/100). Motivos:\n");
        for (String motivo : analise.getMotivos()) {
            sb.append("   * ").append(motivo).append('\n');
        }
        sb.append("Se não reconhece a cobrança, conteste no menu 'Ações sobre um boleto'.\n");
        return sb + super.conteudo();
    }

    @Override
    public int prioridade() {
        return suspeito() ? 10 : super.prioridade();
    }
}
