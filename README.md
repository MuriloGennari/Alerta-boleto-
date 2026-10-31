# AlertaBoleto — Monitoramento de boletos no seu CPF

Aplicação em **Java puro** (sem frameworks) que monitora os boletos emitidos contra o CPF de uma pessoa,
avisa quando estão **atrasados** (com multa e juros calculados), lembra dos que estão **para vencer** e
detecta **boletos falsos** (golpe do boleto, boleto de homônimo, linha digitável adulterada).

O projeto aplica **6 padrões de projeto GoF**, 2 de cada categoria, cada um resolvendo um problema real do domínio.

## Padrões de projeto aplicados

| # | Padrão | Categoria | Problema de negócio que resolveu no projeto |
|---|--------|-----------|---------------------------------------------|
| 1 | **Builder** | Criacional | Um boleto tem 14 informações, várias opcionais, e cada banco entrega um subconjunto diferente. O `BoletoBuilder` monta o boleto passo a passo, aplica valores padrão (nosso número, data de emissão, linha digitável) e **valida as regras** (CPF com dígito verificador, valor > 0, linha digitável com 47 dígitos, vencimento ≥ emissão) antes de criar o objeto — nenhum boleto inválido entra no sistema. |
| 2 | **Factory Method** | Criacional | Cada canal de alerta (E-mail, SMS, WhatsApp) tem regras próprias de destino e de construção (e-mail precisa de `@`, SMS normaliza para `(41) 99999-8888`, WhatsApp para `+5541...`). A `FabricaCanalNotificacao` define o fluxo comum (normalizar → validar → criar) e cada fábrica concreta decide **qual canal instanciar** no método `criarCanal()`. O menu nunca faz `new` de um canal. |
| 3 | **Adapter** | Estrutural | Os boletos vêm de bancos com interfaces incompatíveis: o **Banco Aurora** (sistema legado) devolve texto separado por `;` com termos antigos (cedente/sacado), valores em centavos e datas `ddMMyyyy`; o **Banco Horizonte** (API de fintech) devolve objetos em inglês com `Double` e datas ISO. Os adaptadores traduzem ambos para a interface única `FonteDeBoletos`, e o serviço consulta qualquer banco do mesmo jeito. |
| 4 | **Decorator** | Estrutural | O conteúdo do alerta depende da situação: boleto vencido precisa de **multa e juros**, atraso > 30 dias precisa de **orientação sobre negativação (CDC art. 43 §2º)**, boleto legítimo recebe a **linha digitável** e boleto suspeito recebe o **aviso de golpe** (e nunca o código de pagamento). Os "adicionais" são empilhados dinamicamente sobre o alerta básico, sem criar uma subclasse para cada combinação. |
| 5 | **Chain of Responsibility** | Comportamental | A análise anti-golpe envolve várias regras independentes. Cada regra é um elo da cadeia: **titularidade do CPF** (se o CPF não for o seu, encerra a cadeia com risco máximo), **código do banco x linha digitável**, **beneficiário conhecido**, **valor atípico** (> 3x a média) e **termos suspeitos** ("Regularização de CPF", "Receita"...). Novas regras entram só encaixando um novo elo. |
| 6 | **Observer** | Comportamental | Quando um boleto vence, é marcado como suspeito, é pago ou contestado, vários interessados precisam saber: os canais cadastrados (e-mail, SMS, WhatsApp) e o histórico de auditoria. O `MonitorDeBoletos` (Subject) só mantém a lista de inscritos e avisa todos; canais podem ser incluídos/removidos em tempo de execução sem alterar o monitor. |

> Como os padrões se combinam: **Adapter** busca nos bancos → **Builder** monta os boletos → **Chain** analisa o risco →
> **Decorator** monta a mensagem → **Observer** distribui para os canais criados pelo **Factory Method**.

## Estrutura do projeto

```
src/main/java/br/com/alertaboleto/
├── Main.java                         # Menu interativo (Scanner) - ponto de entrada
├── dominio/                          # Regras do negócio, sem dependência dos padrões
│   ├── Boleto.java  StatusBoleto.java  TipoBoleto.java
│   ├── Cpf.java  LinhaDigitavel.java  ResultadoAnalise.java
├── padroes/
│   ├── criacionais/
│   │   ├── builder/        BoletoBuilder
│   │   └── factorymethod/  FabricaCanalNotificacao (+ Email, Sms, WhatsApp) e os canais
│   ├── estruturais/
│   │   ├── adapter/        FonteDeBoletos, AdaptadorBancoAurora, AdaptadorBancoHorizonte
│   │   │   └── externo/    SistemaLegadoBancoAurora, ApiBancoHorizonte (sistemas "de terceiros")
│   │   └── decorator/      Alerta, AlertaBoleto, AlertaDecorator e os decoradores concretos
│   └── comportamentais/
│       ├── chain/          VerificadorRisco e os 5 verificadores
│       └── observer/       ObservadorBoleto, MonitorDeBoletos, HistoricoAlertasObserver
├── servicos/                         # Orquestração dos casos de uso
│   ├── ServicoAlertaBoletos.java  RepositorioBoletos.java  CalculadoraEncargos.java
└── util/
    └── Log.java                      # Mensagens [PADRÃO: ...] no terminal
```

Cada classe que participa de um padrão indica seu papel no Javadoc com `@note Papel no Padrão: ...`.

## Como executar

**IntelliJ IDEA:** `File > Open` → selecione a pasta do projeto (a que contém o `pom.xml`) → aguarde o Maven carregar →
abra `src/main/java/br/com/alertaboleto/Main.java` → clique no ▶ ao lado de `main`.

**Terminal (Maven):**
```bash
mvn compile
java -cp target/classes br.com.alertaboleto.Main
```

Requisito: JDK 17 ou superior.

## Roteiro sugerido para a avaliação

1. Pressione ENTER duas vezes para usar o titular de exemplo (CPF de teste válido).
2. **Opção 2** — cadastre um e-mail e um SMS (*Factory Method + Observer*). Tente um destino inválido para ver a validação.
3. **Opção 4** — sincronize com os bancos (*Adapter + Builder*). Rode de novo: duplicados são ignorados.
4. **Opção 6** — varredura (*Chain + Decorator + Observer*): veja os vencidos com juros, o lembrete (o SMS ignora por baixa prioridade),
   o boleto de **homônimo** (cadeia interrompida no 1º elo) e o **boleto golpe** (risco 100/100, sem linha digitável).
5. **Opção 8** — monte um alerta escolhendo os adicionais (*Decorator*).
6. **Opção 9** — pague, conteste ou reconheça um boleto (*Observer* notifica os canais).
7. **Opção 10** — avance o tempo e rode a varredura de novo: o boleto "a vencer" vira "vencido" e os juros crescem.
8. **Opção 11** — histórico de todos os alertas (observer de auditoria).

## Observação

Bancos, empresas e CNPJs são **fictícios**. O CPF de exemplo é um número de teste gerado pelo algoritmo oficial e não pertence a ninguém.
Os envios de e-mail/SMS/WhatsApp são **simulados** no terminal.
