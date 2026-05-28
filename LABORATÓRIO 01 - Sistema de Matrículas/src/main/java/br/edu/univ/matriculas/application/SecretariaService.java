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

/*
 A construção de Disciplina envolve regra (capacidadeMax = 60 default, conversão de string para enum). Isso é responsabilidade típica de uma Factory Method ou Static Factory na própria entidade.
Sugestão:
javapublic class Disciplina {
    public static Disciplina criar(String codigo, String nome, String tipo) {
        Disciplina d = new Disciplina();
        d.setCodigo(codigo.toUpperCase());
        d.setNome(Objects.requireNonNull(nome));
        d.setTipo(TipoDisciplina.fromString(tipo));
        d.setCapacidadeMax(CAPACIDADE_PADRAO);
        d.setAtiva(true);
        return d;
    }
    private static final int CAPACIDADE_PADRAO = 60;
}
E no enum:
javapublic enum TipoDisciplina {
    OBRIGATORIA, OPTATIVA;
    public static TipoDisciplina fromString(String s) {
        return "OBRIGATORIA".equalsIgnoreCase(s) ? OBRIGATORIA : OPTATIVA;
    }
}
Benefícios: lógica de construção centralizada, menos código no service, validação no nascimento do objeto.
*/
