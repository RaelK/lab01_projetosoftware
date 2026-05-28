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
Aparecem números mágicos espalhados: 4 (obrigatórias), 2 (optativas), 60 (capacidade default), 3 (mínimo para abrir disciplina em fechamentoPeriodo, linha 64).
Sugestão: extrair para constantes nomeadas (idealmente em uma classe RegrasAcademicas ou em um arquivo de configuração).
javapublic final class RegrasAcademicas {
    public static final int MAX_OBRIGATORIAS_POR_SEMESTRE = 4;
    public static final int MAX_OPTATIVAS_POR_SEMESTRE = 2;
    public static final int MIN_ALUNOS_PARA_ABRIR_TURMA = 3;
    public static final int CAPACIDADE_PADRAO_TURMA = 60;
}
Benefícios: legibilidade, mudança em um único lugar, documentação implícita das regras de negócio.
*/

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
