# Teste de Mutação aplicado a Orientação a Objetos — Cálculo de Frete

Trabalho do grupo Ctrl Alt Del. Tema: *Técnica de Teste Baseado de Defeitos:
Análise de Mutantes, ou Teste de Mutação aplicado a Orientação a Objetos*.
O roteiro está em [`documentation/`](documentation/).

O exemplo prático mostra que **100% de cobertura de linha não garante bons
testes**. Duas suítes cobrem o mesmo módulo de frete com 100% de cobertura de
linha. A inicial tem asserções fracas e mata cerca de 55% dos mutantes. A
reforçada, escrita a partir dos mutantes sobreviventes, mata 100% dos que não
são equivalentes, tanto no nível de método (PIT) quanto no de classe (muJava).

## Requisitos

- JDK 8 ou mais novo (testado com o JDK 21)
- Maven 3.6 ou mais novo
- `git` e `bash`, para o script do muJava, que baixa a ferramenta na primeira
  execução

## Demonstração rápida

```bash
# suíte inicial: 100% de cobertura de linha, mas só 52% no PIT e 60% no muJava
mvn clean test
mvn test-compile org.pitest:pitest-maven:mutationCoverage
scripts/mujava.sh

# suíte reforçada: a mesma cobertura, e 100% nos dois níveis
mvn test -Dsuite=reforcada
mvn test-compile org.pitest:pitest-maven:mutationCoverage -Dsuite=reforcada
scripts/mujava.sh reforcada
```

Os relatórios ficam em `target/site/jacoco-<suite>/`,
`target/pit-reports/<suite>/` e `target/mujava-reports/<suite>/`.

## Estrutura

```
src/main/java/frete/          módulo de frete (código de produção)
src/test/inicial/java/frete/   suíte inicial: 100% de cobertura de linha, asserções fracas
src/test/reforcada/java/frete/ suíte reforçada, guiada pelos mutantes sobreviventes
```

| Classe | Recurso OO exercitado |
|---|---|
| `Frete` | classe base, constantes `static` (taxa mínima) |
| `FreteEconomico` | herança + `super`, aplica desconto |
| `FreteExpresso` | sobrescrita completa, adicional de urgência |
| `FreteInternacional` | sobrescrita + `super`, taxa alfandegária |
| `Pedido` | polimorfismo (referência à classe base) |
| `Calculadora` | sobrecarga e criação da modalidade |
| `Rastreio` | `equals` × `==`, `hashCode` |

O código de produção não usa recursos posteriores ao Java 6 (lambdas, `<>`,
try-with-resources, `switch` com String, `java.util.Objects`), para continuar
compatível com o muJava. O Maven compila com alvo Java 8, o mínimo do JDK 21.

## Comandos

A suíte é escolhida pela propriedade `suite` (padrão: `inicial`).

```bash
# testes + cobertura (JaCoCo) -> target/site/jacoco-inicial/index.html
mvn clean test

# mutação de nível de método (PIT) -> target/pit-reports/inicial/index.html
mvn test-compile org.pitest:pitest-maven:mutationCoverage

# mutação de nível de classe (muJava) -> target/mujava-reports/inicial/
scripts/mujava.sh

# mesmos comandos com a suíte reforçada
mvn clean test -Dsuite=reforcada
mvn test-compile org.pitest:pitest-maven:mutationCoverage -Dsuite=reforcada
scripts/mujava.sh reforcada
```

## muJava (mutação de nível de classe)

O muJava não é publicado no Maven Central. O script `scripts/mujava.sh` baixa
o código do [repositório oficial](https://github.com/jeffoffutt/muJava) num
commit fixo e o executa pelo driver
[`tools/mujava/driver/MuJavaHeadless.java`](tools/mujava/driver/MuJavaHeadless.java),
por três motivos:

- A CLI oficial do muJava gera apenas mutantes tradicionais (de método); os
  operadores de classe só estão disponíveis na interface gráfica. O driver
  chama a mesma API que a GUI (`ClassMutantsGenerator`) com todos os operadores.
- O muJava usa um truque de class loader que só funciona até o Java 8
  (`URLClassLoader.addURL`). O driver monta a tabela de herança sem ele, o que
  permite rodar no JDK 21.
- O executor do muJava roda uma classe de teste por classe alvo. O driver roda
  a **suíte inteira** contra cada mutante, numa JVM separada, com timeout,
  como o PIT faz. Assim os dois scores são comparáveis.

Cada mutante é compilado antes de rodar; o que não compila é contado como
**natimorto**. Mutantes equivalentes podem ser listados em
`analise/mujava-equivalentes.txt`, uma linha por mutante:

```
frete.Frete:JSD_1  remover static de uma constante final não muda o comportamento
```

Eles saem do denominador do score:
`mortos / (gerados − equivalentes − natimortos)`.

### Limitações do muJava neste código

Três operadores do roteiro não geram mutantes aqui, por limitação da ferramenta:

| Operador | Por quê |
|---|---|
| ISD (remover `super.`) | o muJava só implementa para acesso a atributo (`super.x`), não para chamada de método |
| IOP (mover a chamada a `super`) | só se aplica quando `super.m()` é uma instrução isolada; aqui está dentro de `return` ou atribuição |
| EOC (`equals` → `==`) | a direção `equals` → `==` está vazia no código do muJava; só existe `==` → `equals` |

### Mutantes manuais

Para cobrir ISD e EOC, o roteiro prevê mutantes criados à mão. Eles estão
descritos em [`analise/mutantes-manuais.txt`](analise/mutantes-manuais.txt),
uma linha por mutante:

```
frete.Rastreio | EOC | this.codigo.equals(outro.codigo) | this.codigo == outro.codigo
```

O script aplica cada troca sobre o código atual, o que evita mutantes
desatualizados, e grava o resultado na mesma estrutura do muJava. Assim eles
entram no mesmo relatório, marcados como `[manual]`. Se o trecho original não
existir mais, ou aparecer mais de uma vez, o script para com erro.

| Mutante | Suíte inicial | Observação |
|---|---|---|
| ISD em `FreteEconomico` e `FreteInternacional` (4) | mortos | sem o `super.`, o método chama a si mesmo até dar `StackOverflowError`; qualquer teste que o execute falha |
| EOC em `Rastreio.mesmaEntrega` | **sobrevive** | os testes comparam literais iguais, que a JVM guarda como o mesmo objeto, então `==` dá o mesmo resultado que `equals` |
| EOC em `Calculadora.criarFrete` (2) | **sobrevivem** | mesmo motivo: o teste passa o literal `"economico"` |

O IOP continua de fora: nenhum método sobrescrito chama `super` como instrução
isolada nem tem efeito colateral, então mudar a posição da chamada não compila
ou não altera nada.

## Resultados: suíte inicial × reforçada

| Métrica | Inicial | Reforçada |
|---|---|---|
| Testes | 25 | 35 |
| Cobertura de linha (JaCoCo) | 66/66 = **100%** | 66/66 = **100%** |
| Cobertura de desvio (JaCoCo) | 18/20 = 90% | 20/20 = 100% |
| Mutation score de método (PIT) | 30/59 = 51% | 58/59 = 98% |
| ↳ sem o equivalente | 30/58 = **52%** | 58/58 = **100%** |
| Mutation score de classe (muJava + manuais) | 38/63 = **60%** | 63/63 = **100%** |

No muJava, os dois números já descontam 13 equivalentes e 6 natimortos (de
82 gerados). O único sobrevivente do PIT na suíte reforçada é o equivalente.

A cobertura de linha não mudou: as duas suítes executam as mesmas linhas. O
que mudou foi a qualidade das asserções, e só o teste de mutação mostra isso.

A análise de cada sobrevivente da suíte inicial (equivalente ou matável, e
qual teste o mata) está em [`analise/sobreviventes.md`](analise/sobreviventes.md).
