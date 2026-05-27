package br.edu.univ.matriculas.domain;

public class Curso {
  private String codigo;
  private String nome;
  private int creditos;

  public String getCodigo() { return codigo; }
  public void setCodigo(String codigo) { this.codigo = codigo; }
  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }
  public int getCreditos() { return creditos; }
  public void setCreditos(int creditos) { this.creditos = creditos; }
}