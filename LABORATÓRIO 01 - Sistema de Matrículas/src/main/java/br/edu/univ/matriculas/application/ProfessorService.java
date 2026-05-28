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

/* linha 23

  AlunoCsvRepository.AlunoDTO é uma classe interna pública que o ProfessorService importa. Resultado: a camada application tem dependência simbólica de um detalhe de implementação da camada infrastructure.
Sugestão: mover o DTO (ou converter para record) para application ou usar diretamente a entidade Aluno do domínio:
java// domain/Aluno.java já existe — basta usá-la
public Optional<Aluno> findById(String id) { ... }
Benefícios: respeito ao sentido das dependências, eliminação de DTO duplicado, uso do modelo de domínio.
  */
