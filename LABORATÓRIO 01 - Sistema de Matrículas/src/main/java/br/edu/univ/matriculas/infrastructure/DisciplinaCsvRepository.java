package br.edu.univ.matriculas.infrastructure;

import br.edu.univ.matriculas.domain.Disciplina;
import br.edu.univ.matriculas.domain.TipoDisciplina;

import java.util.*;
import java.util.stream.Collectors;

public class DisciplinaCsvRepository {
  private final FileStorage fs;
  private final String path = "disciplinas.csv";

  public DisciplinaCsvRepository(FileStorage fs){ this.fs = fs; }

  private List<Disciplina> parse(){
    String content = fs.read(path);
    List<Disciplina> out = new ArrayList<>();
    if (content==null || content.isBlank()) return out;
    for (String line : content.split("\\R")){
      if (line.isBlank() || line.startsWith("#")) continue;
      String[] p = line.split(";", -1);
      if (p.length < 5) continue;
      Disciplina d = new Disciplina();
      d.setCodigo(p[0].trim().toUpperCase());
      d.setNome(p[1].trim());
      d.setTipo("OBRIGATORIA".equalsIgnoreCase(p[2].trim()) ? TipoDisciplina.OBRIGATORIA : TipoDisciplina.OPTATIVA);
      try { d.setCapacidadeMax(Integer.parseInt(p[3].trim())); } catch (Exception e) { d.setCapacidadeMax(60); }
      d.setAtiva(Boolean.parseBoolean(p[4].trim()));
      out.add(d);
    }
    return out;
  }

  private void writeAll(List<Disciplina> ds){
    String header = "# codigo;nome;tipo;capacidade;ativa";
    String body = ds.stream().map(d ->
      String.join(";", d.getCodigo(), d.getNome(),
        d.getTipo()==TipoDisciplina.OBRIGATORIA ? "OBRIGATORIA" : "OPTATIVA",
        Integer.toString(d.getCapacidadeMax()),
        Boolean.toString(d.isAtiva()))
    ).collect(Collectors.joining(System.lineSeparator()));
    fs.write(path, header + System.lineSeparator() + body);
  }

  public List<Disciplina> findAll(){ return parse(); }
  public List<Disciplina> findAbertas(){
    List<Disciplina> out = new ArrayList<>();
    for (Disciplina d : parse()) if (d.isAtiva()) out.add(d);
    return out;
  }
  public Optional<Disciplina> findByCodigo(String cod){
    String c = cod==null? "" : cod.toUpperCase();
    for (Disciplina d : parse()) if (Objects.equals(d.getCodigo(), c)) return Optional.of(d);
    return Optional.empty();
  }
  public void save(Disciplina novo){
    List<Disciplina> ds = parse();
    boolean found=false;
    for (int i=0;i<ds.size();i++){
      if (Objects.equals(ds.get(i).getCodigo(), novo.getCodigo())) { ds.set(i, novo); found=true; break; }
    }
    if (!found) ds.add(novo);
    writeAll(ds);
  }
}