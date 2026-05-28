LAB 01


O código usa System.out.println para mensagens ao usuário (ok, é CLI) e também para mensagens de erro implícitas (ex.: "(stub) Escolheu: " + op). Não há nenhum logger.
Sugestão: adotar SLF4J + Logback:
xml<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.13</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.5.6</version>
</dependency>
E usar logger.info(...) / logger.warn(...) / logger.error(..., exception) na infraestrutura. Manter System.out apenas para o que é a UI.
Benefícios: logs com níveis, timestamps, threads; possibilidade de rotação, envio para arquivo / ELK / Datadog; padrão de mercado.
