package br.edu.univ.matriculas.infrastructure;
import java.util.List;
import br.edu.univ.matriculas.domain.Disciplina;
public interface DisciplinaRepository { List<Disciplina> findAbertas(); void save(Disciplina d); }

/*
Existem três interfaces órfãs em infrastructure (AlunoRepository, DisciplinaRepository, MatriculaRepository) que não são implementadas por ninguém — as classes *CsvRepository não as declaram com implements.
Sugestão: ou fazer as classes implementarem essas interfaces (combinado com o comentário #1, movê-las para application), ou removê-las. Como estão, são código morto que confunde quem lê.
Benefícios: ou um padrão coerente, ou menos ruído no projeto.
*/
