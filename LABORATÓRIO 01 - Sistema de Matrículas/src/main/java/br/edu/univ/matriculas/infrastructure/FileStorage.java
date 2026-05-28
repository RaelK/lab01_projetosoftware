package br.edu.univ.matriculas.infrastructure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileStorage {
  private final Path base;

  public FileStorage(String basePath) { this.base = Paths.get(basePath); }

  private Path resolve(String file) {
    String norm = file.replace("\\", "/");
    return base.resolve(norm);
  }

  public void write(String file, String content) {
    try {
      Path p = resolve(file);
      Path parent = p.getParent();
      if (parent != null) Files.createDirectories(parent);
      Files.writeString(p, content, StandardCharsets.UTF_8,
          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Falha ao escrever arquivo: " + file, e);
    }
  }

  public String read(String file) {
    try {
      Path p = resolve(file);
      if (!Files.exists(p)) return "";
      return Files.readString(p, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Falha ao ler arquivo: " + file, e);
    }
  }
}

/*
(linhas 19, 28)
javathrow new RuntimeException("Falha ao escrever arquivo: " + file, e);
Lançar RuntimeException diretamente é considerado má prática (Effective Java, item 72). O chamador não consegue diferenciar tipos de erro.
Sugestão: criar uma exceção de domínio da camada de persistência:
javapublic class PersistenciaException extends RuntimeException {
    public PersistenciaException(String msg, Throwable cause) { super(msg, cause); }
}
Benefícios: chamadores podem fazer catch específico, logs ficam mais expressivos, segue convenção da linguagem.
*/
