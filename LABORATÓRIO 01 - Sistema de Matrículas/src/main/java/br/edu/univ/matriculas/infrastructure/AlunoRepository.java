package br.edu.univ.matriculas.infrastructure;
import br.edu.univ.matriculas.domain.Aluno;
public interface AlunoRepository { Aluno byEmail(String email); void save(Aluno a); }


/*
Existem três interfaces órfãs em infrastructure (AlunoRepository, DisciplinaRepository, MatriculaRepository) que não são implementadas por ninguém — as classes *CsvRepository não as declaram com implements.
Sugestão: ou fazer as classes implementarem essas interfaces (combinado com o comentário #1, movê-las para application), ou removê-las. Como estão, são código morto que confunde quem lê.
Benefícios: ou um padrão coerente, ou menos ruído no projeto.
*/
