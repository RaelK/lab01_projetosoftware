package br.edu.univ.matriculas.infrastructure;

import br.edu.univ.matriculas.application.CobrancaGateway;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileCobrancaGateway implements CobrancaGateway {
  private final FileStorage fs;
  public FileCobrancaGateway(FileStorage fs){ this.fs = fs; }

  @Override
  public void notificarInscricao(String alunoId, List<String> codDisciplinas) {
    String ts = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now());
    StringBuilder sb = new StringBuilder();
    sb.append("{\n  \"aluno\":\"").append(alunoId).append("\",\n");
    sb.append("  \"disciplinas\":[");
    for (int i=0;i<codDisciplinas.size();i++){
      if (i>0) sb.append(", ");
      sb.append("\"").append(codDisciplinas.get(i)).append("\"");
    }
    sb.append("],\n  \"timestamp\":\"").append(ts).append("\"\n}");
    fs.write("events/cobranca-"+alunoId+"-"+ts+".json", sb.toString());
  }
}