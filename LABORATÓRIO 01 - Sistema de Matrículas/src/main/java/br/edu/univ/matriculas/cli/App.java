package br.edu.univ.matriculas.cli;

import br.edu.univ.matriculas.application.*;
import br.edu.univ.matriculas.domain.Disciplina;
import br.edu.univ.matriculas.infrastructure.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {
  private static final Scanner in = new Scanner(System.in);

  private static final FileStorage FS = new FileStorage("data");
  private static final DisciplinaCsvRepository DISC_REPO = new DisciplinaCsvRepository(FS);
  private static final PeriodoConfigStore PERIOD_STORE = new PeriodoConfigStore(FS);
  private static final MatriculaCsvRepository MAT_REPO = new MatriculaCsvRepository(FS);
  private static final FileCobrancaGateway COB = new FileCobrancaGateway(FS);
  private static final ComprovanteWriter COMP = new ComprovanteWriter(FS);
  private static final AlunoCsvRepository ALUNO_REPO = new AlunoCsvRepository(FS);

  private static final SecretariaService SECRETARIA = new SecretariaService(DISC_REPO, PERIOD_STORE);
  private static final MatriculaService MATRICULAS = new MatriculaService(DISC_REPO, MAT_REPO, PERIOD_STORE, COB, COMP);
  private static final ProfessorService PROFESSOR = new ProfessorService(MAT_REPO, ALUNO_REPO);

  public static void main(String[] args) {
    System.out.println("== Sistema de Matrículas ==");
    while (true) {
      System.out.println("\n1) Login Aluno");
      System.out.println("2) Login Secretaria");
      System.out.println("3) Login Professor");
      System.out.println("0) Sair  (ou 'q')");
      System.out.print("> ");
      String op = read().toLowerCase();

      if (op.equals("q") || op.equals("0")) {
        System.out.println("Até mais!");
        return;
      }

      switch (op) {
        case "1" -> menuAluno();
        case "2" -> menuSecretaria();
        case "3" -> menuProfessor();
        default  -> System.out.println("Opção inválida.");
      }
    }
  }

  // ==== Validações/Utils ====
  static boolean isSemestre(String s) { return s != null && s.matches("^\\d{4}\\.(1|2)$"); }

  static String askSemestre() {
    while (true) {
      System.out.print("Semestre (ex.: 2025.1): ");
      String sem = read();
      if (isSemestre(sem)) return sem;
      System.out.println("Semestre inválido. Use o formato AAAA.1 ou AAAA.2 (ex.: 2025.1).");
    }
  }

  static String askAlunoId() {
    while (true) {
      System.out.print("Aluno ID (ex.: A001): ");
      String id = read().toUpperCase();
      if (ALUNO_REPO.findById(id).isPresent()) return id;
      System.out.println("Aluno ID inválido. Use um ID cadastrado (ex.: A001). Consulte data/alunos.csv.");
    }
  }

  static String askDisciplinaCodigo() {
    while (true) {
      System.out.print("Código da disciplina (ex.: MAT101): ");
      String cod = read().toUpperCase();
      Optional<Disciplina> od = DISC_REPO.findByCodigo(cod);
      if (od.isEmpty()) {
        System.out.println("Código inválido. Digite um código existente (ex.: MAT101). Use 'Aluno → 1' para listar.");
        continue;
      }
      if (!od.get().isAtiva()) {
        System.out.println("Disciplina inativa. Use 'Aluno → 1' para ver as ativas.");
        continue;
      }
      return cod;
    }
  }

  static String read() { String s = in.nextLine(); return s == null ? "" : s.trim(); }
  static void pause() { System.out.print("(Enter para continuar) "); in.nextLine(); }

  // ---- Aluno ----
  static void menuAluno() {
    System.out.println("\n[Aluno]");
    System.out.println("1) Listar currículo (disciplinas ativas)");
    System.out.println("2) Matricular-se em disciplina");
    System.out.println("3) Cancelar matrícula");
    System.out.println("4) Confirmar inscrição no semestre");
    System.out.println("0) Voltar  (ou 'q' para sair)");
    System.out.print("> ");
    String op = read().toLowerCase();
    if (op.equals("q")) { System.out.println("Até mais!"); System.exit(0); }
    if (op.equals("0")) return;

    switch (op){
      case "1" -> {
        List<Disciplina> ds = DISC_REPO.findAbertas();
        if (ds.isEmpty()) System.out.println("Nenhuma disciplina ativa.");
        else for (Disciplina d: ds) System.out.println("- "+d.getCodigo()+" | "+d.getNome()+" | "+d.getTipo());
        pause();
      }
      case "2" -> {
        String sem = askSemestre();
        String aluno = askAlunoId();
        String cod = askDisciplinaCodigo();
        System.out.println(MATRICULAS.adicionarDisciplina(aluno, sem, cod));
        pause();
      }
      case "3" -> {
        String sem = askSemestre();
        String aluno = askAlunoId();
        String cod = askDisciplinaCodigo();
        MATRICULAS.cancelarDisciplina(aluno, sem, cod);
        System.out.println("Cancelada (se existia).");
        pause();
      }
      case "4" -> {
        String sem = askSemestre();
        String aluno = askAlunoId();
        System.out.println(MATRICULAS.confirmarInscricao(aluno, sem));
        System.out.println("→ Veja arquivos em data/events/comprovantes e data/events/ de cobrança.");
        pause();
      }
      default -> { System.out.println("Opção inválida."); pause(); }
    }
  }

  // ---- Secretaria ----
  static void menuSecretaria() {
    System.out.println("\n[Secretaria]  (Período " + (SECRETARIA.isPeriodoAberto() ? "ABERTO" : "FECHADO") + ")");
    System.out.println("1) Listar/Adicionar disciplinas");
    System.out.println("2) Atribuir professor (stub)");
    System.out.println("3) Abrir período de matrículas");
    System.out.println("4) Fechar período de matrículas");
    System.out.println("5) Encerrar período (processar ativa/cancelada por >=3)");
    System.out.println("0) Voltar  (ou 'q' para sair)");
    System.out.print("> ");
    String op = read().toLowerCase();
    if (op.equals("q")) { System.out.println("Até mais!"); System.exit(0); }
    switch (op) {
      case "0" -> { return; }
      case "1" -> crudDisciplinas();
      case "3" -> { SECRETARIA.abrirPeriodo(); System.out.println("Período: ABERTO"); pause(); }
      case "4" -> { SECRETARIA.fecharPeriodo(); System.out.println("Período: FECHADO"); pause(); }
      case "5" -> {
        String sem = askSemestre();
        System.out.println(MATRICULAS.fechamentoPeriodo(sem));
        pause();
      }
      default -> { System.out.println("(stub) Escolheu: " + op); pause(); }
    }
  }

  static void crudDisciplinas(){
    System.out.println("\n[Disciplinas]");
    List<Disciplina> ds = SECRETARIA.listarDisciplinas();
    if (ds.isEmpty()) { System.out.println("Nenhuma disciplina cadastrada."); }
    else for (Disciplina d : ds) System.out.println("- " + d.getCodigo() + " | " + d.getNome() + " | " + d.getTipo() + " | ativa=" + d.isAtiva());
    System.out.println("\n(a) Adicionar  (Enter para voltar)");
    System.out.print("> ");
    String op = read().toLowerCase();
    if (!"a".equals(op)) return;

    System.out.print("Código: "); String cod = read().toUpperCase();
    System.out.print("Nome: "); String nome = read();
    System.out.print("Tipo (OBRIGATORIA/OPTATIVA): "); String tipo = read().toUpperCase();
    SECRETARIA.criarDisciplina(cod, nome, tipo);
    System.out.println("OK! Disciplina salva.");
    pause();
  }

  // ---- Professor ----
  static void menuProfessor() {
    System.out.println("\n[Professor]");
    System.out.println("1) Consultar alunos matriculados em minha disciplina");
    System.out.println("0) Voltar  (ou 'q' para sair)");
    System.out.print("> ");
    String op = read().toLowerCase();
    if (op.equals("q")) { System.out.println("Até mais!"); System.exit(0); }
    if (op.equals("0")) return;

    String sem = askSemestre();
    String cod = askDisciplinaCodigo();
    List<String> alunos = new java.util.ArrayList<>(PROFESSOR.listarAlunosPorDisciplina(sem, cod));
    if (alunos.isEmpty()) System.out.println("Nenhum aluno encontrado para "+cod+" no semestre "+sem+".");
    else {
      System.out.println("Alunos:");
      for (String s : alunos) System.out.println(" - " + s);
    }
    pause();
  }
}

/*
A classe App tem 200 linhas, gerencia menus de 3 atores diferentes (Aluno, Secretaria, Professor), faz validação de entrada, instancia toda a aplicação e ainda imprime mensagens. É um God Object em formação.
Sugestão: quebrar em MenuAluno, MenuSecretaria, MenuProfessor (cada um com seu próprio executar()), além de um AppContext (ou CompositionRoot) responsável apenas por instanciar e injetar dependências.
javapublic class App {
    public static void main(String[] args) {
        AppContext ctx = AppContext.bootstrap("data");
        new MenuPrincipal(ctx).run();
    }
}
Benefícios: Single Responsibility Principle, arquivos pequenos, fácil onboarding.
*/

/*
Todos os repositórios, serviços e o Scanner são static final. Isso é o oposto de injeção de dependência: impossível trocar FileStorage por um InMemoryStorage em teste, impossível ter duas instâncias do app, e o estado do Scanner vaza por toda a classe.
Sugestão: transformar a classe em instância, receber dependências no construtor (manual ou via container leve como Guice/Dagger). Já há frameworks como Spring Boot que fariam isso automaticamente — mas mesmo sem framework, basta tirar os static.
Benefícios: testabilidade, ciclo de vida controlado, alinhamento com a Injeção de Dependências do comentário #1.
*/

//Os services retornam String com mensagens já formatadas para o usuário (ex.: "Disciplina sem vagas.", "Limite de 4 obrigatórias atingido."). Isso acopla a regra de negócio à apresentação CLI e impede internacionalização ou reuso em uma futura interface web/REST.
//Sugestão: retornar um objeto de resultado (Result Pattern) com um código de status enumerado e deixar a formatação para a camada cli.

/*
(linhas 51, 53-60, 62-69, 71-86)
A validação (isSemestre, askSemestre, askAlunoId, askDisciplinaCodigo) está dentro de App. Em uma futura interface web, tudo isso teria que ser reescrito.
Sugestão: centralizar em Value Objects no domínio que se autovalidam:
javapublic record Semestre(String valor) {
    private static final Pattern PADRAO = Pattern.compile("^\\d{4}\\.(1|2)$");
    public Semestre {
        if (!PADRAO.matcher(valor).matches())
            throw new IllegalArgumentException("Semestre inválido: " + valor);
    }
}
Aplicar também para AlunoId, CodigoDisciplina.
Benefícios: validação onde o tipo nasce, regras impossíveis de burlar, mais um conceito do DDD aplicado.
*/
