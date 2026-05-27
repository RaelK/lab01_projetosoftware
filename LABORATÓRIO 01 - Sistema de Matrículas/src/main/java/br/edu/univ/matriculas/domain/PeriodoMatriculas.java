package br.edu.univ.matriculas.domain;
public class PeriodoMatriculas {
  private boolean aberto = false;
  public void abrir(){aberto = true;} public void fechar(){aberto = false;}
  public boolean isAberto(){return aberto;}
}
