package br.edu.univ.matriculas.domain;

public class Disciplina {
  private String codigo;
  private String nome;
  private TipoDisciplina tipo;
  private int capacidadeMax = 60;
  private boolean ativa;

  public boolean temVaga(int inscritos) { return inscritos < capacidadeMax; }

  public String getCodigo() { return codigo; }
  public void setCodigo(String codigo) { this.codigo = codigo; }
  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }
  public TipoDisciplina getTipo() { return tipo; }
  public void setTipo(TipoDisciplina tipo) { this.tipo = tipo; }
  public int getCapacidadeMax() { return capacidadeMax; }
  public void setCapacidadeMax(int capacidadeMax) { this.capacidadeMax = capacidadeMax; }
  public boolean isAtiva() { return ativa; }
  public void setAtiva(boolean ativa) { this.ativa = ativa; }
}