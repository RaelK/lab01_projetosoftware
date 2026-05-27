package br.edu.univ.matriculas.application;

import br.edu.univ.matriculas.domain.Disciplina;
import br.edu.univ.matriculas.domain.TipoDisciplina;
import br.edu.univ.matriculas.infrastructure.DisciplinaCsvRepository;
import br.edu.univ.matriculas.infrastructure.PeriodoConfigStore;

import java.util.List;

public class SecretariaService {
  private final DisciplinaCsvRepository repo;
  private final PeriodoConfigStore periodo;

  public SecretariaService(DisciplinaCsvRepository repo, PeriodoConfigStore periodo){
    this.repo = repo;
    this.periodo = periodo;
  }

  public void criarDisciplina(String codigo, String nome, String tipo){
    Disciplina d = new Disciplina();
    d.setCodigo(codigo.toUpperCase());
    d.setNome(nome);
    d.setTipo("OBRIGATORIA".equalsIgnoreCase(tipo) ? TipoDisciplina.OBRIGATORIA : TipoDisciplina.OPTATIVA);
    d.setCapacidadeMax(60);
    d.setAtiva(true);
    repo.save(d);
  }

  public List<Disciplina> listarDisciplinas(){ return repo.findAll(); }

  public void abrirPeriodo(){ periodo.setAberto(true); }
  public void fecharPeriodo(){ periodo.setAberto(false); }
  public boolean isPeriodoAberto(){ return periodo.isAberto(); }
}