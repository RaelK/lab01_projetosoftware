package br.edu.univ.matriculas.infrastructure;
import br.edu.univ.matriculas.domain.Matricula;
public interface MatriculaRepository { Matricula findByAlunoSemestre(String aId, String sem); void save(Matricula m); }
