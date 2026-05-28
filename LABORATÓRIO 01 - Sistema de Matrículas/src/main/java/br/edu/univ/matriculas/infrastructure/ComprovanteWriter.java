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

/*
A montagem do comprovante com StringBuilder e concatenações é frágil e difícil de evoluir (e se mudar o formato para HTML/PDF?).
Sugestão: isolar a formatação atrás de uma interface FormatadorComprovante e ter implementações FormatadorTexto, FormatadorHtml, etc. (combina Strategy + Builder). Ou usar uma classe ComprovanteBuilder fluente:
javaString conteudo = ComprovanteBuilder.novo()
    .comAluno(alunoId)
    .comSemestre(semestre)
    .comDisciplinas(disciplinas)
    .build();
Benefícios: legibilidade, flexibilidade de formato, testabilidade do conteúdo independente da escrita em disco
  */
