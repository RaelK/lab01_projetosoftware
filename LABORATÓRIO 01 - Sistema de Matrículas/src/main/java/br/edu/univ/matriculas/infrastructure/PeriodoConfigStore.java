package br.edu.univ.matriculas.infrastructure;

public class PeriodoConfigStore {
  private final FileStorage fs;
  private final String path = "config.json";

  public PeriodoConfigStore(FileStorage fs){ this.fs = fs; }

  public boolean isAberto(){
    String c = fs.read(path).trim();
    if (c.isEmpty()) return false;
    int i = c.indexOf("periodoAberto");
    if (i<0) return false;
    int j = c.indexOf(":", i);
    if (j<0) return false;
    String tail = c.substring(j+1).toLowerCase();
    return tail.contains("true");
  }

  public void setAberto(boolean aberto){
    String json = "{\n  \"periodoAberto\": " + (aberto ? "true" : "false") + "\n}\n";
    fs.write(path, json);
  }
}

/* 
A leitura faz indexOf("periodoAberto") + contains("true"). Isso quebra com qualquer espaço estranho, comentário, ou se houver outra chave que contenha a palavra "true".
Sugestão: usar uma lib de JSON real (Jackson, Gson) ou — mais simples ainda — guardar como .properties:
propertiesperiodoAberto=true
Que se lê com java.util.Properties em duas linhas, sem dependências.
Benefícios: robustez, menos código, formato padrão.
*/
