package br.com.alertaboleto.servicos;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.Cpf;
import br.com.alertaboleto.dominio.ResultadoAnalise;
import br.com.alertaboleto.dominio.StatusBoleto;
import br.com.alertaboleto.padroes.comportamentais.chain.ContextoAnalise;
import br.com.alertaboleto.padroes.comportamentais.chain.VerificadorBeneficiario;
import br.com.alertaboleto.padroes.comportamentais.chain.VerificadorCodigoBanco;
import br.com.alertaboleto.padroes.comportamentais.chain.VerificadorRisco;
import br.com.alertaboleto.padroes.comportamentais.chain.VerificadorTermosSuspeitos;
import br.com.alertaboleto.padroes.comportamentais.chain.VerificadorTitularidade;
import br.com.alertaboleto.padroes.comportamentais.chain.VerificadorValorAtipico;
import br.com.alertaboleto.padroes.comportamentais.observer.EventoBoleto;
import br.com.alertaboleto.padroes.comportamentais.observer.MonitorDeBoletos;
import br.com.alertaboleto.padroes.comportamentais.observer.TipoEvento;
import br.com.alertaboleto.padroes.estruturais.adapter.FonteDeBoletos;
import br.com.alertaboleto.padroes.estruturais.decorator.Alerta;
import br.com.alertaboleto.padroes.estruturais.decorator.AlertaBoleto;
import br.com.alertaboleto.padroes.estruturais.decorator.AlertaInformativo;
import br.com.alertaboleto.padroes.estruturais.decorator.AvisoGolpeDecorator;
import br.com.alertaboleto.padroes.estruturais.decorator.CodigoPagamentoDecorator;
import br.com.alertaboleto.padroes.estruturais.decorator.EncargosAtrasoDecorator;
import br.com.alertaboleto.padroes.estruturais.decorator.SugestaoNegociacaoDecorator;
import br.com.alertaboleto.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;



public class ServicoAlertaBoletos {

    private static final int DIAS_LEMBRETE = 3;

    private final RepositorioBoletos repositorio = new RepositorioBoletos();
    private final MonitorDeBoletos monitor;
    private final List<FonteDeBoletos> fontes;
    private final CalculadoraEncargos calculadora = new CalculadoraEncargos();
    private final VerificadorRisco cadeiaAntiGolpe;
    private final Set<String> cnpjsConhecidos = new LinkedHashSet<>();

    private String nomeTitular;
    private String cpfTitular;
    private LocalDate hoje = LocalDate.now();

    public ServicoAlertaBoletos(MonitorDeBoletos monitor, List<FonteDeBoletos> fontes) {
        this.monitor = monitor;
        this.fontes = fontes;
        this.cadeiaAntiGolpe = montarCadeiaAntiGolpe();
        // Empresas que o titular já reconhece (normalmente viria do histórico dele)
        cnpjsConhecidos.addAll(List.of(
                "12.345.678/0001-90", "23.456.789/0001-01", "34.567.890/0001-12",
                "56.789.012/0001-34", "67.890.123/0001-45", "78.901.234/0001-56"));
    }


    private VerificadorRisco montarCadeiaAntiGolpe() {
        VerificadorRisco primeiro = new VerificadorTitularidade();
        primeiro.encadear(new VerificadorCodigoBanco())
                .encadear(new VerificadorBeneficiario())
                .encadear(new VerificadorValorAtipico())
                .encadear(new VerificadorTermosSuspeitos());
        return primeiro;
    }



    public void definirTitular(String nome, String cpf) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        if (!Cpf.valido(cpf)) {
            throw new IllegalArgumentException("CPF inválido (dígitos verificadores não conferem).");
        }
        boolean trocou = cpfTitular != null && !cpfTitular.equals(Cpf.somenteDigitos(cpf));
        this.nomeTitular = nome.trim();
        this.cpfTitular = Cpf.somenteDigitos(cpf);
        if (trocou) {
            repositorio.limpar();
            Log.info("Titular alterado: boletos anteriores foram descartados. Sincronize novamente.");
        }
    }




    public int sincronizar() {
        int novos = 0;
        for (FonteDeBoletos fonte : fontes) {
            Log.info("Consultando " + fonte.nomeFonte() + "...");
            List<Boleto> encontrados = fonte.buscarPorCpf(cpfTitular, nomeTitular);
            for (Boleto b : encontrados) {
                if (repositorio.adicionarSeNovo(b)) {
                    novos++;
                }
            }
        }
        return novos;
    }

    public Boleto cadastrarManual(Boleto boleto) {
        if (!repositorio.adicionarSeNovo(boleto)) {
            throw new IllegalArgumentException("Já existe um boleto com esta linha digitável.");
        }
        return boleto;
    }

    // ================================================================= Chain + Decorator + Observer


    public record ResumoVarredura(int analisados, int vencidos, int suspeitos, int lembretes) {
    }

    public ResumoVarredura executarVarredura() {
        int analisados = 0, vencidos = 0, suspeitos = 0, lembretes = 0;
        ContextoAnalise contexto = criarContexto();

        for (Boleto boleto : repositorio.todos()) {
            if (boleto.getStatus() != StatusBoleto.ABERTO) {
                continue; // vencidos/suspeitos já alertados, pagos e contestados não geram novo alerta
            }
            analisados++;
            System.out.println();
            Log.info(">> Analisando boleto #" + boleto.getId() + " - " + boleto.getBeneficiarioNome());
            analisarRisco(boleto, contexto);

            if (boleto.getAnalise().isSuspeito()) {
                boleto.marcarComoSuspeito();
                monitor.notificar(new EventoBoleto(TipoEvento.SUSPEITA_GOLPE, boleto, montarAlertaPadrao(boleto)));
                suspeitos++;
            } else if (boleto.estaVencido(hoje)) {
                boleto.marcarComoVencido();
                monitor.notificar(new EventoBoleto(TipoEvento.BOLETO_VENCIDO, boleto, montarAlertaPadrao(boleto)));
                vencidos++;
            } else if (boleto.diasParaVencer(hoje) <= DIAS_LEMBRETE && !boleto.isLembreteEnviado()) {
                boleto.setLembreteEnviado(true);
                monitor.notificar(new EventoBoleto(TipoEvento.LEMBRETE_VENCIMENTO, boleto, montarAlertaPadrao(boleto)));
                lembretes++;
            } else {
                Log.info("Sem alerta: boleto no prazo e seguro.");
            }
        }
        return new ResumoVarredura(analisados, vencidos, suspeitos, lembretes);
    }


    public ResultadoAnalise analisarRisco(Boleto boleto, ContextoAnalise contexto) {
        Log.padrao("Chain of Responsibility", "Iniciando cadeia anti-golpe no boleto #" + boleto.getId());
        ResultadoAnalise resultado = new ResultadoAnalise();
        cadeiaAntiGolpe.analisar(boleto, contexto, resultado);
        boleto.setAnalise(resultado);
        Log.padrao("Chain of Responsibility", "Resultado: risco " + resultado.getNivel()
                + " (" + resultado.getPontuacao() + "/100)");
        return resultado;
    }

    public ContextoAnalise criarContexto() {
        BigDecimal soma = BigDecimal.ZERO;
        int qtd = 0;
        for (Boleto b : repositorio.todos()) {
            if (cnpjsConhecidos.contains(b.getBeneficiarioCnpj()) && b.getPagadorCpf().equals(cpfTitular)) {
                soma = soma.add(b.getValor());
                qtd++;
            }
        }
        BigDecimal media = qtd == 0 ? BigDecimal.ZERO : soma.divide(BigDecimal.valueOf(qtd), 2, RoundingMode.HALF_UP);
        return new ContextoAnalise(cpfTitular, cnpjsConhecidos, media);
    }


    public Alerta montarAlertaPadrao(Boleto boleto) {
        Log.padrao("Decorator", "Montando alerta do boleto #" + boleto.getId() + " a partir do AlertaBoleto básico:");
        Alerta alerta = new AlertaBoleto(boleto, hoje);
        if (boleto.getAnalise() != null && boleto.getAnalise().isSuspeito()) {
            return new AvisoGolpeDecorator(alerta, boleto.getAnalise());
        }
        if (boleto.estaVencido(hoje)) {
            alerta = new EncargosAtrasoDecorator(alerta, boleto, hoje, calculadora);
            alerta = new SugestaoNegociacaoDecorator(alerta, boleto, hoje);
        }
        return new CodigoPagamentoDecorator(alerta, boleto);
    }

    public Alerta montarAlertaPersonalizado(Boleto boleto, boolean encargos, boolean negociacao,
                                            boolean antiGolpe, boolean codigo) {
        if (antiGolpe && boleto.getAnalise() == null) {
            analisarRisco(boleto, criarContexto());
        }
        Log.padrao("Decorator", "Empilhando adicionais escolhidos sobre o AlertaBoleto básico:");
        Alerta alerta = new AlertaBoleto(boleto, hoje);
        if (encargos) alerta = new EncargosAtrasoDecorator(alerta, boleto, hoje, calculadora);
        if (negociacao) alerta = new SugestaoNegociacaoDecorator(alerta, boleto, hoje);
        if (codigo) alerta = new CodigoPagamentoDecorator(alerta, boleto);
        if (antiGolpe) alerta = new AvisoGolpeDecorator(alerta, boleto.getAnalise());
        return alerta;
    }

    public CalculadoraEncargos.Encargos calcularEncargos(Boleto boleto) {
        return calculadora.calcular(boleto, hoje);
    }



    public void registrarPagamento(Boleto boleto) {
        CalculadoraEncargos.Encargos e = calculadora.calcular(boleto, hoje);
        boleto.marcarComoPago();
        monitor.notificar(new EventoBoleto(TipoEvento.BOLETO_PAGO, boleto, new AlertaInformativo(
                "Pagamento registrado: " + boleto.getBeneficiarioNome(),
                "Valor pago: " + Boleto.moeda(e.total())
                        + (e.diasAtraso() > 0 ? " (incluindo " + Boleto.moeda(e.multa().add(e.juros())) + " de encargos)" : "")
                        + "\nO boleto saiu do monitoramento. Guarde o comprovante por 5 anos.", 1)));
    }

    public String contestar(Boleto boleto) {
        boleto.contestar();
        String protocolo = "CTS-" + hoje.getYear() + "-" + String.format("%05d", boleto.getId() * 7919 % 100000);
        monitor.notificar(new EventoBoleto(TipoEvento.BOLETO_CONTESTADO, boleto, new AlertaInformativo(
                "Contestação aberta: " + boleto.getBeneficiarioNome(),
                "Protocolo: " + protocolo + "\nNão pague este boleto. Registre também um boletim de ocorrência"
                        + "\ne avise o banco emissor (" + boleto.getNomeBanco() + ").", 5)));
        return protocolo;
    }

    /** Titular confirma que conhece o beneficiário: ele entra na lista e o boleto volta ao fluxo normal. */
    public void reconhecerBeneficiario(Boleto boleto) {
        boleto.reconhecerComoLegitimo();
        cnpjsConhecidos.add(boleto.getBeneficiarioCnpj());
    }



    public void avancarDias(int dias) {
        hoje = hoje.plusDays(dias);
    }



    public List<Boleto> listar() {
        return repositorio.todos();
    }

    public Optional<Boleto> buscar(int id) {
        return repositorio.buscarPorId(id);
    }

    public boolean temTitular() {
        return cpfTitular != null;
    }

    public String getNomeTitular() { return nomeTitular; }
    public String getCpfTitular() { return cpfTitular; }
    public LocalDate getHoje() { return hoje; }
    public Set<String> getCnpjsConhecidos() { return cnpjsConhecidos; }
    public MonitorDeBoletos getMonitor() { return monitor; }
}
