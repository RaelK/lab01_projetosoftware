package br.edu.univ.matriculas.infrastructure;
import java.util.List;
import br.edu.univ.matriculas.domain.Disciplina;
public interface DisciplinaRepository { List<Disciplina> findAbertas(); void save(Disciplina d); }
