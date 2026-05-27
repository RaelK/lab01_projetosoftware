package br.edu.univ.matriculas.infrastructure;
import br.edu.univ.matriculas.domain.Aluno;
public interface AlunoRepository { Aluno byEmail(String email); void save(Aluno a); }
