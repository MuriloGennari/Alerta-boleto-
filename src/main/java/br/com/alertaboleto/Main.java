package br.com.alertaboleto;

import br.com.alertaboleto.dominio.Boleto;
import br.com.alertaboleto.dominio.Cpf;
import br.com.alertaboleto.dominio.LinhaDigitavel;
import br.com.alertaboleto.dominio.ResultadoAnalise;
import br.com.alertaboleto.dominio.StatusBoleto;
import br.com.alertaboleto.dominio.TipoBoleto;
import br.com.alertaboleto.padroes.comportamentais.observer.HistoricoAlertasObserver;
import br.com.alertaboleto.padroes.comportamentais.observer.MonitorDeBoletos;
import br.com.alertaboleto.padroes.comportamentais.observer.ObservadorBoleto;
import br.com.alertaboleto.padroes.criacionais.builder.BoletoBuilder;
import br.com.alertaboleto.padroes.criacionais.factorymethod.CanalNotificacao;
import br.com.alertaboleto.padroes.criacionais.factorymethod.FabricaCanalEmail;
import br.com.alertaboleto.padroes.criacionais.factorymethod.FabricaCanalNotificacao;
import br.com.alertaboleto.padroes.criacionais.factorymethod.FabricaCanalSms;
import br.com.alertaboleto.padroes.criacionais.factorymethod.FabricaCanalWhatsApp;
import br.com.alertaboleto.padroes.estruturais.adapter.AdaptadorBancoAurora;
import br.com.alertaboleto.padroes.estruturais.adapter.AdaptadorBancoHorizonte;
import br.com.alertaboleto.padroes.estruturais.adapter.FonteDeBoletos;
import br.com.alertaboleto.padroes.estruturais.adapter.externo.ApiBancoHorizonte;
import br.com.alertaboleto.padroes.estruturais.adapter.externo.SistemaLegadoBancoAurora;
import br.com.alertaboleto.padroes.estruturais.decorator.Alerta;
import br.com.alertaboleto.servicos.CalculadoraEncargos;
import br.com.alertaboleto.servicos.ServicoAlertaBoletos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Ponto de entrada: menu interativo no terminal.
 * <p>
 * O Main só faz a interface com o usuário (ler opções, exibir resultados). Toda regra
 * de negócio fica no {@link ServicoAlertaBoletos} e nos padrões. Ele também é o lugar
 * onde as peças são "montadas" (composição manual, já que não usamos Spring).
 */
public class Main {

    private static final String CPF_EXEMPLO = "529.982.247-25"; // CPF de teste válido (gerado, não pertence a ninguém)
    private static final String NOME_EXEMPLO = "Maria Aparecida Souza";

    private final Scanner scanner = new Scanner(System.in);
    private final ServicoAlertaBoletos servico;
    private final HistoricoAlertasObserver historico = new HistoricoAlertasObserver();
    private final List<FabricaCanalNotificacao> fabricas = List.of(
            new FabricaCanalEmail(), new FabricaCanalSms(), new FabricaCanalWhatsApp());

    public Main() {
        MonitorDeBoletos monitor = new MonitorDeBoletos();
        // Os bancos são acessados SEMPRE pela interface FonteDeBoletos (Adapter)
        List<FonteDeBoletos> fontes = List.of(
                new AdaptadorBancoAurora(new SistemaLegadoBancoAurora()),
                new AdaptadorBancoHorizonte(new ApiBancoHorizonte()));
        servico = new ServicoAlertaBoletos(monitor, fontes);
    }

    public static void main(String[] args) {
        new Main().executar();
    }

    // =================================================================== loop principal

    private void executar() {
        banner();
        titulo("CONFIGURAÇÃO INICIAL");
        servico.getMonitor().inscrever(historico);
        configurarTitular();

        while (true) {
            mostrarMenu();
            int opcao = lerInt("Escolha uma opção: ", 0, 11);
            System.out.println();
            try {
                switch (opcao) {
                    case 1 -> configurarTitular();
                    case 2 -> cadastrarCanal();
                    case 3 -> removerCanal();
                    case 4 -> sincronizar();
                    case 5 -> cadastrarBoletoManual();
                    case 6 -> varredura();
                    case 7 -> listarBoletos();
                    case 8 -> alertaPersonalizado();
                    case 9 -> acoesBoleto();
                    case 10 -> avancarTempo();
                    case 11 -> verHistorico();
                    case 0 -> {
                        System.out.println("Até logo! Mantenha seus boletos em dia. :)");
                        return;
                    }
                    default -> { }
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("   (!) " + e.getMessage());
            }
            pausar();
        }
    }

    private void banner() {
        System.out.println("==================================================================");
        System.out.println("   ALERTA BOLETO - monitoramento de boletos no seu CPF");
        System.out.println("   Atrasos, lembretes e detecção de boletos falsos");
        System.out.println("   Linhas [PADRÃO: ...] mostram os Design Patterns em ação");
        System.out.println("==================================================================");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("------------------------------------------------------------------");
        System.out.println(" Titular: " + servico.getNomeTitular() + "  CPF " + Cpf.formatar(servico.getCpfTitular())
                + "   | Data do sistema: " + servico.getHoje().format(Boleto.DATA_BR));
        System.out.println(" Boletos: " + servico.listar().size()
                + "   | Canais de alerta ativos: " + canaisAtivos().size());
        System.out.println("------------------------------------------------------------------");
        System.out.println("  1 - Alterar titular (nome e CPF)");
        System.out.println("  2 - Cadastrar canal de alerta ............ [Factory Method + Observer]");
        System.out.println("  3 - Remover canal de alerta .............. [Observer]");
        System.out.println("  4 - Sincronizar boletos com os bancos ..... [Adapter + Builder]");
        System.out.println("  5 - Cadastrar boleto manualmente .......... [Builder]");
        System.out.println("  6 - Executar varredura de atrasos/golpes .. [Chain + Decorator + Observer]");
        System.out.println("  7 - Listar boletos");
        System.out.println("  8 - Montar alerta personalizado ........... [Decorator]");
        System.out.println("  9 - Ações sobre um boleto (pagar/contestar/reconhecer) [Observer]");
        System.out.println(" 10 - Avançar o tempo (simular dias passando)");
        System.out.println(" 11 - Ver histórico de alertas ............. [Observer]");
        System.out.println("  0 - Sair");
    }

    // =================================================================== opções do menu

    private void configurarTitular() {
        System.out.println("Informe o titular que será monitorado (ENTER usa o exemplo).");
        String nome = lerTexto("Nome [" + NOME_EXEMPLO + "]: ");
        if (nome.isBlank()) nome = NOME_EXEMPLO;
        while (true) {
            String cpf = lerTexto("CPF  [" + CPF_EXEMPLO + "]: ");
            if (cpf.isBlank()) cpf = CPF_EXEMPLO;
            try {
                servico.definirTitular(nome, cpf);
                System.out.println("   Titular definido: " + nome + " - CPF " + Cpf.formatar(cpf));
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("   (!) " + e.getMessage() + " Tente novamente.");
            }
        }
    }

    private void cadastrarCanal() {
        titulo("CADASTRAR CANAL DE ALERTA");
        for (int i = 0; i < fabricas.size(); i++) {
            System.out.println("  " + (i + 1) + " - " + fabricas.get(i).nomeCanal());
        }
        FabricaCanalNotificacao fabrica = fabricas.get(lerInt("Qual canal? ", 1, fabricas.size()) - 1);
        String destino = lerTexto("Destino (" + fabrica.exemploDestino() + "): ");

        CanalNotificacao canal = fabrica.novoCanal(destino); // Factory Method decide a classe concreta
        servico.getMonitor().inscrever(canal);               // e o canal vira um Observer do monitor
        System.out.println("   Canal ativo! Os próximos alertas também irão para " + canal.descricao());
    }

    private void removerCanal() {
        titulo("REMOVER CANAL DE ALERTA");
        List<ObservadorBoleto> canais = canaisAtivos();
        if (canais.isEmpty()) {
            System.out.println("   Nenhum canal cadastrado.");
            return;
        }
        for (int i = 0; i < canais.size(); i++) {
            System.out.println("  " + (i + 1) + " - " + canais.get(i).descricao());
        }
        servico.getMonitor().remover(canais.get(lerInt("Remover qual? ", 1, canais.size()) - 1));
    }

    private void sincronizar() {
        titulo("SINCRONIZAR COM OS BANCOS");
        int novos = servico.sincronizar();
        System.out.println();
        System.out.println("   " + novos + " boleto(s) novo(s) importado(s). Total: " + servico.listar().size() + ".");
        if (novos == 0) {
            System.out.println("   (Boletos repetidos são ignorados pela linha digitável.)");
        } else {
            System.out.println("   Dica: execute a varredura (opção 6) para gerar os alertas.");
        }
    }

    private void cadastrarBoletoManual() {
        titulo("CADASTRAR BOLETO MANUALMENTE (Builder)");
        System.out.println("Informe os dados. Campos com [ ] são opcionais: o Builder aplica um valor padrão.");

        BoletoBuilder builder = BoletoBuilder.novoBoleto()
                .paraOPagador(servico.getNomeTitular(), servico.getCpfTitular());

        String benef = lerTexto("Beneficiário (quem está cobrando): ");
        String cnpj = lerTexto("[CNPJ do beneficiário]: ");
        builder.doBeneficiario(benef, cnpj);

        builder.noValorDe(lerValor("Valor (ex.: 150,90): "));
        builder.comVencimentoEm(lerData("Vencimento (dd/mm/aaaa) ou dias relativos (ex.: -5 = venceu há 5 dias): "));

        String banco = lerTexto("Código do banco (3 dígitos) [001]: ");
        builder.doBanco(banco.isBlank() ? "001" : banco, null);

        TipoBoleto[] tipos = TipoBoleto.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println("  " + (i + 1) + " - " + tipos[i].getDescricao());
        }
        builder.doTipo(tipos[lerInt("Tipo: ", 1, tipos.length) - 1]);

        String linha = lerTexto("[Linha digitável - 47 dígitos] (ENTER para gerar): ");
        if (!linha.isBlank()) builder.comLinhaDigitavel(linha);

        Boleto boleto = servico.cadastrarManual(builder.construir());
        System.out.println("   Boleto cadastrado:");
        System.out.println("   " + boleto.resumo(servico.getHoje()));
        System.out.println("   Linha digitável: " + LinhaDigitavel.formatar(boleto.getLinhaDigitavel()));
    }

    private void varredura() {
        titulo("VARREDURA DE ATRASOS E GOLPES - " + servico.getHoje().format(Boleto.DATA_BR));
        if (servico.listar().isEmpty()) {
            System.out.println("   Nenhum boleto. Sincronize com os bancos (opção 4) primeiro.");
            return;
        }
        if (canaisAtivos().isEmpty()) {
            System.out.println("   Aviso: nenhum canal cadastrado, os alertas irão só para o histórico (opção 2 cadastra).");
        }
        ServicoAlertaBoletos.ResumoVarredura r = servico.executarVarredura();
        System.out.println();
        System.out.println("   === Resumo: " + r.analisados() + " analisado(s) | " + r.vencidos() + " vencido(s) | "
                + r.suspeitos() + " suspeito(s) de golpe | " + r.lembretes() + " lembrete(s)");
        if (r.analisados() == 0) {
            System.out.println("   Nenhum boleto em aberto para analisar (os já alertados não são reenviados).");
        }
    }

    private void listarBoletos() {
        titulo("MEUS BOLETOS");
        if (servico.listar().isEmpty()) {
            System.out.println("   Nenhum boleto. Use a opção 4 ou 5.");
            return;
        }
        BigDecimal totalAtraso = BigDecimal.ZERO;
        for (Boleto b : servico.listar()) {
            System.out.println("  " + b.resumo(servico.getHoje()));
            ResultadoAnalise a = b.getAnalise();
            if (a != null) {
                System.out.println("       anti-golpe: risco " + a.getNivel() + " (" + a.getPontuacao() + "/100)");
            }
            if (b.getStatus() == StatusBoleto.VENCIDO) {
                totalAtraso = totalAtraso.add(servico.calcularEncargos(b).total());
            }
        }
        System.out.println();
        System.out.println("   Total devido em boletos vencidos (com multa e juros): " + Boleto.moeda(totalAtraso));
    }

    private void alertaPersonalizado() {
        titulo("ALERTA PERSONALIZADO (Decorator)");
        Boleto boleto = escolherBoleto();
        if (boleto == null) return;
        System.out.println("Escolha os adicionais que serão empilhados sobre o alerta básico:");
        boolean encargos = lerSimNao("  + Multa e juros?");
        boolean negociacao = lerSimNao("  + Orientação de negociação?");
        boolean codigo = lerSimNao("  + Linha digitável para pagamento?");
        boolean golpe = lerSimNao("  + Resultado da análise anti-golpe?");

        Alerta alerta = servico.montarAlertaPersonalizado(boleto, encargos, negociacao, golpe, codigo);
        System.out.println();
        System.out.println("   ===== " + alerta.titulo() + "  (prioridade " + alerta.prioridade() + "/10)");
        for (String l : alerta.conteudo().split("\n")) {
            System.out.println("   " + l);
        }
    }

    private void acoesBoleto() {
        titulo("AÇÕES SOBRE UM BOLETO");
        Boleto boleto = escolherBoleto();
        if (boleto == null) return;
        System.out.println("  1 - Registrar pagamento");
        System.out.println("  2 - Contestar (não reconheço esta cobrança)");
        System.out.println("  3 - Reconhecer beneficiário (é legítimo, sair de 'suspeito')");
        System.out.println("  0 - Voltar");
        switch (lerInt("Ação: ", 0, 3)) {
            case 1 -> {
                if (boleto.getStatus() == StatusBoleto.PAGO) {
                    throw new IllegalStateException("Este boleto já está pago.");
                }
                CalculadoraEncargos.Encargos e = servico.calcularEncargos(boleto);
                System.out.println("   Valor a pagar hoje: " + Boleto.moeda(e.total()));
                if (boleto.getAnalise() != null && boleto.getAnalise().isSuspeito()
                        && !lerSimNao("   ATENÇÃO: boleto suspeito de golpe! Confirmar mesmo assim?")) {
                    return;
                }
                servico.registrarPagamento(boleto);
            }
            case 2 -> System.out.println("   Contestação registrada. Protocolo " + servico.contestar(boleto));
            case 3 -> {
                servico.reconhecerBeneficiario(boleto);
                System.out.println("   " + boleto.getBeneficiarioNome() + " agora é beneficiário conhecido."
                        + " O boleto voltou para 'Em aberto' e será reavaliado na próxima varredura.");
            }
            default -> { }
        }
    }

    private void avancarTempo() {
        titulo("SIMULAR PASSAGEM DO TEMPO");
        System.out.println("Útil para ver boletos 'a vencer' virarem 'vencidos' e os juros aumentarem.");
        int dias = lerInt("Avançar quantos dias? (1 a 365): ", 1, 365);
        servico.avancarDias(dias);
        System.out.println("   Nova data do sistema: " + servico.getHoje().format(Boleto.DATA_BR)
                + ". Execute a varredura (opção 6).");
    }

    private void verHistorico() {
        titulo("HISTÓRICO DE ALERTAS (observer de auditoria)");
        if (historico.getRegistros().isEmpty()) {
            System.out.println("   Nenhum alerta emitido ainda.");
            return;
        }
        historico.getRegistros().forEach(r -> System.out.println("  " + r));
    }

    // =================================================================== utilitários de entrada

    private List<ObservadorBoleto> canaisAtivos() {
        List<ObservadorBoleto> canais = new ArrayList<>();
        for (ObservadorBoleto o : servico.getMonitor().getObservadores()) {
            if (o instanceof CanalNotificacao) canais.add(o);
        }
        return canais;
    }

    private Boleto escolherBoleto() {
        if (servico.listar().isEmpty()) {
            System.out.println("   Nenhum boleto cadastrado.");
            return null;
        }
        servico.listar().forEach(b -> System.out.println("  " + b.resumo(servico.getHoje())));
        while (true) {
            int id = lerInt("Número do boleto (0 volta): ", 0, Integer.MAX_VALUE);
            if (id == 0) return null;
            Boleto b = servico.buscar(id).orElse(null);
            if (b != null) return b;
            System.out.println("   Boleto #" + id + " não existe.");
        }
    }

    private void titulo(String t) {
        System.out.println("=== " + t + " ===");
    }

    private String lerTexto(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println("\nEntrada encerrada. Saindo.");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    private int lerInt(String prompt, int min, int max) {
        while (true) {
            String s = lerTexto(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) {
                // cai na mensagem abaixo
            }
            System.out.println("   Digite um número entre " + min + " e " + (max == Integer.MAX_VALUE ? "..." : max) + ".");
        }
    }

    private boolean lerSimNao(String prompt) {
        while (true) {
            String s = lerTexto(prompt + " (s/n): ").toLowerCase();
            if (s.startsWith("s")) return true;
            if (s.startsWith("n")) return false;
        }
    }

    private BigDecimal lerValor(String prompt) {
        while (true) {
            String s = lerTexto(prompt).replace("R$", "").replace(" ", "");
            // aceita "1.234,56" (formato BR) e "1234.56"
            if (s.contains(",")) s = s.replace(".", "").replace(",", ".");
            try {
                BigDecimal v = new BigDecimal(s);
                if (v.signum() > 0) return v;
            } catch (NumberFormatException ignored) {
                // cai na mensagem abaixo
            }
            System.out.println("   Valor inválido.");
        }
    }

    private LocalDate lerData(String prompt) {
        while (true) {
            String s = lerTexto(prompt);
            try {
                if (s.matches("[+-]?\\d{1,4}")) {
                    return servico.getHoje().plusDays(Integer.parseInt(s));
                }
                return LocalDate.parse(s, Boleto.DATA_BR);
            } catch (DateTimeParseException | NumberFormatException e) {
                System.out.println("   Data inválida. Use dd/mm/aaaa ou um número de dias (ex.: -10).");
            }
        }
    }

    private void pausar() {
        lerTexto("\n[ENTER para voltar ao menu]");
    }
}
