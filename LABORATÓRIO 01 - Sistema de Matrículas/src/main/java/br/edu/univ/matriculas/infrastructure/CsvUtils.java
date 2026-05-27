package br.edu.univ.matriculas.infrastructure;
final class CsvUtils {
  private CsvUtils(){}
  static String esc(String s){ return s==null? "" : s.replace(";", "\\;"); }
  static String unesc(String s){ return s==null? "" : s.replace("\\;", ";"); }
}