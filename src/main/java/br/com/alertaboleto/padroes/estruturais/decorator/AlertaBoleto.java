package br.com.alertaboleto.padroes.estruturais.decorator;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.Cpf;

import java.time.LocalDate;

/**
 * Alerta básico: só os dados essenciais do boleto, sem nenhum "adicional".
 * É o objeto que fica no centro, sendo embrulhado pelos decoradores.
 *
 * @note Papel no Padrão: Concrete Component (Decorator)
 */
public class AlertaBoleto implements Alerta {

    private final Boleto boleto;
    private final LocalDate hoje;

    public AlertaBoleto(Boleto boleto, LocalDate hoje) {
        this.boleto = boleto;
        this.hoje = hoje;
    }

    @Override
    public String titulo() {
        if (boleto.estaVencido(hoje)) {
            return "Boleto em atraso há " + boleto.diasDeAtraso(hoje) + " dia(s): " + boleto.getBeneficiarioNome();
        }
        long dias = boleto.diasParaVencer(hoje);
        return dias == 0
                ? "Boleto vence HOJE: " + boleto.getBeneficiarioNome()
                : "Boleto vence em " + dias + " dia(s): " + boleto.getBeneficiarioNome();
    }

    @Override
    public String conteudo() {
        return "Beneficiário: " + boleto.getBeneficiarioNome() + " (CNPJ " + boleto.getBeneficiarioCnpj() + ")\n"
                + "Pagador: " + boleto.getPagadorNome() + " - CPF " + Cpf.mascarar(boleto.getPagadorCpf()) + "\n"
                + "Valor original: " + Boleto.moeda(boleto.getValor()) + "\n"
                + "Vencimento: " + boleto.getVencimento().format(Boleto.DATA_BR) + "\n"
                + "Emitido por: " + boleto.getNomeBanco() + " (" + boleto.getCodigoBanco() + ") via " + boleto.getOrigem();
    }

    @Override
    public int prioridade() {
        return boleto.estaVencido(hoje) ? 3 : 1;
    }
}
