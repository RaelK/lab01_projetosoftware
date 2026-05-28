package br.edu.univ.matriculas.application;

import br.edu.univ.matriculas.domain.Disciplina;
import br.edu.univ.matriculas.domain.TipoDisciplina;
import br.edu.univ.matriculas.infrastructure.*;

import java.util.*;

public class MatriculaService {
  private final DisciplinaCsvRepository discRepo;
  private final MatriculaCsvRepository matRepo;
  private final PeriodoConfigStore periodo;
  private final CobrancaGateway cobranca;
  private final ComprovanteWriter comprovante;

  //A camada application depende diretamente de classes concretas de infrastructure (DisciplinaCsvRepository, MatriculaCsvRepository, PeriodoConfigStore, ComprovanteWriter). Existem interfaces (DisciplinaRepository, MatriculaRepository, AlunoRepository) em infrastructure, mas elas não são usadas em lugar nenhum.
  //Sugestão: aplicar o Dependency Inversion Principle (SOLID) movendo essas interfaces para o pacote application (ou um subpacote application.ports) e fazendo os serviços dependerem delas — o que já está esboçado pelo CobrancaGateway, o único caso feito corretamente.

  public MatriculaService(DisciplinaCsvRepository d, MatriculaCsvRepository m, PeriodoConfigStore p,
                          CobrancaGateway c, ComprovanteWriter comp){
    this.discRepo = d; this.matRepo = m; this.periodo = p; this.cobranca = c; this.comprovante = comp;
  }

  public String adicionarDisciplina(String alunoId, String semestre, String discCod){
    if (!periodo.isAberto()) return "Período de matrículas FECHADO.";
    Optional<Disciplina> od = discRepo.findByCodigo(discCod);
    if (od.isEmpty() || !od.get().isAtiva()) return "Disciplina inexistente/inativa.";
    Disciplina d = od.get();

    if (matRepo.countInscritos(semestre, discCod) >= d.getCapacidadeMax()) return "Disciplina sem vagas.";

    List<String> atuais = matRepo.listByAluno(semestre, alunoId);
    int obrig=0, opt=0;
    for (String cod : atuais) {
      Optional<Disciplina> ox = discRepo.findByCodigo(cod);
      if (ox.isPresent()){
        if (ox.get().getTipo()==TipoDisciplina.OBRIGATORIA) obrig++; else opt++;
      }
    }
    if (d.getTipo()==TipoDisciplina.OBRIGATORIA && obrig >= 4) return "Limite de 4 obrigatórias atingido.";
    if (d.getTipo()==TipoDisciplina.OPTATIVA   && opt   >= 2) return "Limite de 2 optativas atingido.";

    matRepo.add(semestre, alunoId, discCod);
    return "OK";
  }

  /*
  javafor (String cod : atuais) {
    Optional<Disciplina> ox = discRepo.findByCodigo(cod);
    if (ox.isPresent()){
        if (ox.get().getTipo()==TipoDisciplina.OBRIGATORIA) obrig++; else opt++;
    }
}
Usar isPresent() + get() é equivalente a usar null check — desperdiça os recursos do Optional.
Sugestão:
javafor (String cod : atuais) {
    discRepo.findByCodigo(cod).ifPresent(d -> {
        if (d.getTipo() == OBRIGATORIA) obrig++; else opt++;
    });
}
Ou, ainda mais idiomático, usar Streams:
javaMap<TipoDisciplina, Long> contagem = atuais.stream()
    .map(discRepo::findByCodigo)
    .flatMap(Optional::stream)
    .collect(Collectors.groupingBy(Disciplina::getTipo, Collectors.counting()));
Benefícios: estilo funcional, menos linhas, pode evoluir para paralelo facilmente.
*/

  public void cancelarDisciplina(String alunoId, String semestre, String discCod){
    matRepo.cancelar(semestre, alunoId, discCod);
  }

  public String confirmarInscricao(String alunoId, String semestre){
    List<String> confirmadas = matRepo.confirmarAluno(semestre, alunoId);
    if (confirmadas.isEmpty()) return "Nada a confirmar.";
    cobranca.notificarInscricao(alunoId, confirmadas);
    comprovante.emitir(alunoId, semestre, confirmadas);
    return "Inscrição confirmada, comprovante gerado e cobrança notificada.";
  }

  /*
  Em confirmarInscricao, o service chama cobranca.notificarInscricao(...) e comprovante.emitir(...) em sequência. Se amanhã quisermos enviar e-mail, atualizar BI, gerar PDF, o método cresce indefinidamente.
Sugestão: aplicar o Observer / Publish-Subscribe Pattern.
javapublic interface InscricaoListener {
    void onInscricaoConfirmada(InscricaoConfirmadaEvent ev);
}

public class MatriculaService {
    private final List<InscricaoListener> listeners;
    public String confirmarInscricao(String alunoId, String semestre) {
        // ...
        var ev = new InscricaoConfirmadaEvent(alunoId, semestre, confirmadas);
        listeners.forEach(l -> l.onInscricaoConfirmada(ev));
        // ...
    }
}
FileCobrancaGateway e ComprovanteWriter viram listeners registrados na composição.
Benefícios: baixo acoplamento, fácil estender, alinhado a arquiteturas orientadas a eventos.
*/

  public String fechamentoPeriodo(String semestre){
    Set<String> dis = matRepo.listDisciplinasNoSemestre(semestre);
    int ativadas=0, canceladas=0;
    for (String cod : dis){
      int qtd = matRepo.countInscritos(semestre, cod);
      Optional<Disciplina> od = discRepo.findByCodigo(cod);
      if (od.isEmpty()) continue;
      Disciplina d = od.get();
      if (qtd >= 3) {
        d.setAtiva(true);
        discRepo.save(d);
        ativadas++;
      } else {
        d.setAtiva(false);
        discRepo.save(d);
        matRepo.cancelarPorDisciplina(semestre, cod);
        canceladas++;
      }
    }
    return "Fechamento concluído: ativadas="+ativadas+", canceladas="+canceladas+".";
  }
}


/*
O bloco que conta obrigatórias/optativas e aplica limites diferentes por tipo é um caso clássico de Strategy.
Sugestão:
javapublic interface RegraDeLimite {
    boolean excede(List<Disciplina> atuais);
    String mensagemErro();
}

public class LimiteObrigatorias implements RegraDeLimite {
    public boolean excede(List<Disciplina> atuais) {
        return atuais.stream().filter(d -> d.getTipo() == OBRIGATORIA).count() >= 4;
    }
    public String mensagemErro() { return "Limite de 4 obrigatórias atingido."; }
}
// análogo para LimiteOptativas
No service:
javaMap<TipoDisciplina, RegraDeLimite> regras = Map.of(
    OBRIGATORIA, new LimiteObrigatorias(),
    OPTATIVA,    new LimiteOptativas());
if (regras.get(d.getTipo()).excede(atuais)) return regras.get(d.getTipo()).mensagemErro();
Benefícios: Open/Closed Principle — para adicionar um novo tipo (ex.: "ELETIVA" com limite de 1), basta criar uma nova classe sem tocar no service.

  */

/* 
Matricula tem três estados (PENDENTE, CONFIRMADA, CANCELADA) e regras de transição implícitas. Hoje qualquer um pode chamar confirmar() mesmo numa matrícula cancelada.
Sugestão: aplicar o State Pattern ou, mais simples, validar transições no próprio método (ver comentário #2). Para um sistema maior, vale criar uma máquina de estados explícita:
javaprivate static final Map<StatusMatricula, Set<StatusMatricula>> TRANSICOES_VALIDAS = Map.of(
    PENDENTE,    Set.of(CONFIRMADA, CANCELADA),
    CONFIRMADA,  Set.of(CANCELADA),
    CANCELADA,   Set.of()
);
Benefícios: invariantes garantidas, transições inválidas viram exceções claras em vez de bugs silenciosos.
  */

