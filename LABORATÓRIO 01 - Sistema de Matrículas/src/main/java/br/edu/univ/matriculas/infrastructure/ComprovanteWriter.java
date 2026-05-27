package br.edu.univ.matriculas.infrastructure;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ComprovanteWriter {
  private final FileStorage fs;
  public ComprovanteWriter(FileStorage fs){ this.fs = fs; }

  public void emitir(String alunoId, String semestre, List<String> disciplinas){
    String ts = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now());
    StringBuilder sb = new StringBuilder();
    sb.append("Comprovante de Inscrição\n");
    sb.append("Aluno: ").append(alunoId).append("\n");
    sb.append("Semestre: ").append(semestre).append("\n");
    sb.append("Data/Hora: ").append(ts).append("\n");
    sb.append("Disciplinas confirmadas:\n");
    for (String d : disciplinas) sb.append(" - ").append(d).append("\n");
    fs.write("events/comprovantes/comprovante-"+alunoId+"-"+semestre+"-"+ts+".txt", sb.toString());
  }
}