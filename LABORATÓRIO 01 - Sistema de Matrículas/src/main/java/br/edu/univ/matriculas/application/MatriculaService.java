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