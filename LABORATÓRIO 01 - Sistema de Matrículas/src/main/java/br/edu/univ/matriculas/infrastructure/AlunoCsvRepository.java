package br.edu.univ.matriculas.infrastructure;

import java.util.*;

public class AlunoCsvRepository {
  private final FileStorage fs;
  private final String path = "alunos.csv";

  public static class AlunoDTO {
    public final String id, nome, email;
    public AlunoDTO(String id, String nome, String email){ this.id=id; this.nome=nome; this.email=email; }
  }

  public AlunoCsvRepository(FileStorage fs){ this.fs = fs; }

  public List<AlunoDTO> findAll(){
    String content = fs.read(path);
    List<AlunoDTO> out = new ArrayList<>();
    if (content==null || content.isBlank()) return out;
    for (String line : content.split("\\R")){
      if (line.isBlank() || line.startsWith("#")) continue;
      String[] p = line.split(";", -1);
      if (p.length < 3) continue;
      out.add(new AlunoDTO(p[0], p[1], p[2]));
    }
    return out;
  }

  public Optional<AlunoDTO> findById(String id){
    for (AlunoDTO a : findAll()) if (Objects.equals(a.id, id)) return Optional.of(a);
    return Optional.empty();
  }
}