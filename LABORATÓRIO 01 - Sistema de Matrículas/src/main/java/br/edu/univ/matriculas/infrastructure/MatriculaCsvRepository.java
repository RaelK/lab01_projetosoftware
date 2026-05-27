package br.edu.univ.matriculas.infrastructure;

import java.util.*;
import java.util.stream.Collectors;

public class MatriculaCsvRepository {
  private final FileStorage fs;

  public MatriculaCsvRepository(FileStorage fs){ this.fs = fs; }

  private String path(String sem){ return "matriculas-" + sem + ".csv"; }

  private static class Row {
    String alunoId, discCod, status;
    Row(String a, String d, String s){alunoId=a;discCod=d;status=s;}
  }

  private List<Row> parse(String sem){
    String content = fs.read(path(sem));
    List<Row> out = new ArrayList<>();
    if (content==null || content.isBlank()) return out;
    for (String line : content.split("\\R")){
      if (line.isBlank() || line.startsWith("#")) continue;
      String[] p = line.split(";", -1);
      if (p.length<3) continue;
      out.add(new Row(p[0], p[1], p[2]));
    }
    return out;
  }

  private void writeAll(String sem, List<Row> rows){
    String header = "# alunoId;disciplina;status";
    String body = rows.stream()
      .map(r -> String.join(";", r.alunoId, r.discCod, r.status))
      .collect(Collectors.joining(System.lineSeparator()));
    fs.write(path(sem), header + System.lineSeparator() + body);
  }

  public List<String> listByAluno(String sem, String alunoId){
    List<String> out = new ArrayList<>();
    for (Row r : parse(sem)) {
      if (Objects.equals(r.alunoId, alunoId) && !r.status.equals("CANCELADA")) out.add(r.discCod);
    }
    return out;
  }

  public int countInscritos(String sem, String discCod){
    int c = 0;
    for (Row r : parse(sem)) if (Objects.equals(r.discCod, discCod) && !r.status.equals("CANCELADA")) c++;
    return c;
  }

  public void add(String sem, String alunoId, String discCod){
    List<Row> rows = parse(sem);
    for (Row r : rows){
      if (Objects.equals(r.alunoId, alunoId) && Objects.equals(r.discCod, discCod)) {
        r.status = "PENDENTE";
        writeAll(sem, rows); return;
      }
    }
    rows.add(new Row(alunoId, discCod, "PENDENTE"));
    writeAll(sem, rows);
  }

  public void cancelar(String sem, String alunoId, String discCod){
    List<Row> rows = parse(sem);
    for (Row r : rows){
      if (Objects.equals(r.alunoId, alunoId) && Objects.equals(r.discCod, discCod)) {
        r.status = "CANCELADA";
      }
    }
    writeAll(sem, rows);
  }

  public List<String> confirmarAluno(String sem, String alunoId){
    List<Row> rows = parse(sem);
    List<String> confirmadas = new ArrayList<>();
    for (Row r : rows){
      if (Objects.equals(r.alunoId, alunoId) && !r.status.equals("CANCELADA")) {
        r.status = "CONFIRMADA";
        confirmadas.add(r.discCod);
      }
    }
    writeAll(sem, rows);
    return confirmadas;
  }

  public Set<String> listDisciplinasNoSemestre(String sem){
    Set<String> s = new HashSet<>();
    for (Row r : parse(sem)) if (!r.status.equals("CANCELADA")) s.add(r.discCod);
    return s;
  }

  public void cancelarPorDisciplina(String sem, String discCod){
    List<Row> rows = parse(sem);
    for (Row r : rows) if (Objects.equals(r.discCod, discCod)) r.status = "CANCELADA";
    writeAll(sem, rows);
  }

  public List<String> listAlunosByDisciplina(String sem, String discCod){
    LinkedHashSet<String> ids = new LinkedHashSet<>();
    for (Row r : parse(sem)){
      if (Objects.equals(r.discCod, discCod) && !r.status.equals("CANCELADA")) {
        ids.add(r.alunoId);
      }
    }
    return new ArrayList<>(ids);
  }
}