package br.edu.univ.matriculas.application;

import java.util.List;

public interface CobrancaGateway {
  void notificarInscricao(String alunoId, List<String> codDisciplinas);
}