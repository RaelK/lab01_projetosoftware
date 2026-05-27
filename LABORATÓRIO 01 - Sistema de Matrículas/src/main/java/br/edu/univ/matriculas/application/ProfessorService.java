package br.edu.univ.matriculas.application;

import br.edu.univ.matriculas.infrastructure.AlunoCsvRepository;
import br.edu.univ.matriculas.infrastructure.MatriculaCsvRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfessorService {
  private final MatriculaCsvRepository matRepo;
  private final AlunoCsvRepository alunoRepo;

  public ProfessorService(MatriculaCsvRepository m, AlunoCsvRepository a){
    this.matRepo = m; this.alunoRepo = a;
  }

  public List<String> listarAlunosPorDisciplina(String semestre, String discCod){
    List<String> ids = matRepo.listAlunosByDisciplina(semestre, discCod);
    List<String> out = new ArrayList<>();
    for (String id : ids){
      Optional<AlunoCsvRepository.AlunoDTO> oa = alunoRepo.findById(id);
      String nome = oa.isPresent() ? oa.get().nome : "(sem cadastro)";
      out.add(id + " - " + nome);
    }
    return out;
  }
}