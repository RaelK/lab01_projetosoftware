package br.edu.univ.matriculas.infrastructure;
final class CsvUtils {
  private CsvUtils(){}
  static String esc(String s){ return s==null? "" : s.replace(";", "\\;"); }
  static String unesc(String s){ return s==null? "" : s.replace("\\;", ";"); }
}

/*
A classe CsvUtils tem esc() e unesc() para escapar ; em campos CSV, mas nenhum repositório a usa. Resultado: se uma disciplina chamada "Cálculo I; revisão" for salva, o parser quebra na próxima leitura.
Sugestão: ou aplicar esc/unesc consistentemente em todos os repositórios, ou (melhor) remover o CsvUtils e adotar uma lib mínima como OpenCSV. Código morto é dívida técnica.
Benefícios: robustez do parser, eliminação de bugs latentes.
*/
