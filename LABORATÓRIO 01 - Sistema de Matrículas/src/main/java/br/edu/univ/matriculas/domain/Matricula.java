package br.edu.univ.matriculas.domain;

public class Matricula {
  private String id;
  private String semestre;
  private StatusMatricula status = StatusMatricula.PENDENTE;
  private String alunoId;
  private String disciplinaCod;

  public void confirmar(){ this.status = StatusMatricula.CONFIRMADA; }
  public void cancelar(){ this.status = StatusMatricula.CANCELADA; }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getSemestre() { return semestre; }
  public void setSemestre(String semestre) { this.semestre = semestre; }
  public StatusMatricula getStatus() { return status; }
  public void setStatus(StatusMatricula status) { this.status = status; }
  public String getAlunoId() { return alunoId; }
  public void setAlunoId(String alunoId) { this.alunoId = alunoId; }
  public String getDisciplinaCod() { return disciplinaCod; }
  public void setDisciplinaCod(String disciplinaCod) { this.disciplinaCod = disciplinaCod; }
}