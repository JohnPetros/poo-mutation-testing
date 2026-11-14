# Análise dos mutantes sobreviventes — suíte inicial

Etapa 5 do roteiro: para cada mutante que sobreviveu à suíte inicial, decidir
se ele é **equivalente** (nenhum teste consegue matá-lo, porque o comportamento
não muda) ou **matável** (falta um teste), e nesse caso dizer qual teste o mata.

## Resumo

| | PIT (método) | muJava + manuais (classe) |
|---|---|---|
| Mutantes gerados | 59 | 82 (75 + 7 manuais) |
| Natimortos | 0 | 6 |
| Mortos | 30 | 38 |
| Sobreviventes | 29 | 38 |
| ↳ equivalentes | **1** | **13** |
| ↳ matáveis | **28** | **25** |
| Score sem descontar equivalentes | 30/59 = 51% | 38/76 = 50% |
| **Score descontando equivalentes** | **30/58 = 52%** | **38/63 = 60%** |
| Cobertura de linha (JaCoCo) | 100% | 100% |

Score = mortos ÷ (gerados − equivalentes − natimortos).

Dos 67 sobreviventes, só 14 são equivalentes. Os outros 53 são defeitos reais
que a suíte inicial deixa passar, mesmo executando 100% das linhas.

---

## 1. PIT (nível de método)

### 1.1 Equivalente (1)

| Local | Mutação | Por que é equivalente |
|---|---|---|
| `Frete.calcular`, linha 31 | `valor < TAXA_MINIMA` → `valor <= TAXA_MINIMA` | Só muda o caso `valor == 10.0`. Nele, o original devolve `valor` (10.0) e o mutante devolve `TAXA_MINIMA` (10.0): o mesmo número. |

O PIT não tem como marcar equivalentes, então o desconto é feito nesta análise.

### 1.2 Matáveis (28)

**Contas (12):** a suíte só testa `calcular() > 0` e `prazoEmDias() > 0`, e
qualquer troca de `*` por `/` ou de `+` por `-` continua positiva com os valores
usados. Basta comparar com o valor exato.

| Local | Mutações | Teste que mata |
|---|---|---|
| `Frete.calcular` (l. 30) | `*`→`/` (×2), `+`→`-` | `new Frete(10, 100).calcular() == 25.0` |
| `Frete.prazoEmDias` (l. 38) | `/`→`*`, `+`→`-` | `new Frete(10, 1000).prazoEmDias() == 5` |
| `FreteEconomico.calcular` (l. 18) | `*`→`/` | `new FreteEconomico(10, 100).calcular() == 20.0` |
| `FreteExpresso.calcular` (l. 19) | `*`→`/` (×2), `+`→`-` (×2) | `new FreteExpresso(10, 100).calcular() == 50.0` |
| `FreteInternacional.calcular` (l. 22) | `+`→`-` | `new FreteInternacional(10, 5000, "Portugal").calcular() == 320.0` |
| `FreteInternacional.prazoEmDias` (l. 27) | `+`→`-` | `new FreteInternacional(10, 5000, "Portugal").prazoEmDias() == 23` |
| `Pedido.valorTotal` (l. 20) | `+`→`-` | `new Pedido("Ana", 200, new FreteExpresso(10, 100)).valorTotal() == 250.0` |

**Limites (3):**

| Local | Mutação | Teste que mata |
|---|---|---|
| `Frete(...)`, l. 19 | `peso <= 0` → `peso < 0` | `new Frete(0, 100)` deve lançar `IllegalArgumentException` (hoje o teste usa -1) |
| `Frete(...)`, l. 22 | `distancia < 0` → `distancia <= 0` | `new Frete(10, 0)` deve ser aceito (`calcular() == 20.0`) |
| `Frete.calcular`, l. 31 | condição removida (nunca aplica a taxa mínima) | `new Frete(1, 1).calcular() == 10.0` |

**Retornos trocados (11):** `assertNotNull` não percebe `""`, e nenhum teste
verifica um `false`.

| Local | Mutação | Teste que mata |
|---|---|---|
| `getModalidade` de `Frete`, `FreteEconomico`, `FreteExpresso`, `FreteInternacional` | retorna `""` | `assertEquals("Padrao" / "Economico" / "Expresso" / "Internacional", ...)` |
| `Pedido.getCliente` | retorna `""` | `assertEquals("Ana", pedido.getCliente())` |
| `FreteInternacional.getPaisDestino` | retorna `""` | `assertEquals("Portugal", ...)` |
| `Rastreio.getCodigo` | retorna `""` | `assertEquals("BR123", ...)` |
| `Rastreio.mesmaEntrega` | sempre `true` | códigos diferentes → `false`; `mesmaEntrega(null)` → `false` |
| `Rastreio.equals` | sempre `true` | `new Rastreio("BR1").equals(new Rastreio("BR2"))` → `false` |
| `Rastreio.hashCode` | retorna `0` | códigos diferentes geram hashes diferentes (ver nota) |

**Condições removidas (2):**

| Local | Mutação | Teste que mata |
|---|---|---|
| `Calculadora.criarFrete`, l. 23 e 26 | `if` de `"economico"` / `"expresso"` sempre falso | `criarFrete("economico", ...)` é `FreteEconomico`; `criarFrete("expresso", ...)` é `FreteExpresso` |

> **Nota sobre `hashCode` → 0:** é o caso mais discutível. Um `hashCode`
> constante respeita o contrato de `Object` (objetos iguais têm hashes
> iguais), então o código continua "correto", só que lento em `HashMap`.
> Não é equivalente, porque o valor é observável, mas o teste que o mata
> verifica uma propriedade de qualidade, e não de correção. Vale citar na
> apresentação.

---

## 2. muJava + manuais (nível de classe)

### 2.1 Equivalentes (13)

Os 13 são do operador **JSD** (remover `static`), todos aplicados a constantes
`static final` com valor literal. Os identificadores estão em
[`mujava-equivalentes.txt`](mujava-equivalentes.txt).

**Justificativa:** são constantes de tempo de compilação. O `javac` copia o
valor direto no bytecode de quem as usa (`ldc`) e nunca lê o campo. Conferido
com `javap`: nenhuma classe de produção nem de teste tem `getstatic`. Sem o
`static`, o campo vira `final` de instância com o mesmo literal, continua
sendo inlinado, e nada observável muda.

Isso ilustra uma desvantagem do teste de mutação apontada no roteiro: o
operador gera mutantes que exigem análise manual e não acrescentam nada. O
JSD só faz sentido em campos `static` que não são constantes, e este módulo
não tem nenhum.

### 2.2 Matáveis (25), por operador

| Operador | Sobreviventes | O que a suíte não verifica | Teste que mata |
|---|---|---|---|
| **IOD** (remover método sobrescrito) | 9: `calcular`, `prazoEmDias` e `getModalidade` em `FreteEconomico`, `FreteExpresso` e `FreteInternacional` | que cada subclasse se comporta diferente da base | valores exatos de `calcular()`, `prazoEmDias()` e `getModalidade()` em cada subclasse |
| **JSI** (inserir `static`) | 5: `Pedido.cliente`, `Pedido.valorProdutos`, `Pedido.frete`, `FreteInternacional.paisDestino`, `Rastreio.codigo` | que dois objetos têm estado independente | criar **dois** objetos com dados diferentes e conferir que o primeiro não mudou |
| **PNC** (instanciar outra classe da hierarquia) | 4: `new Frete` → `new FreteExpresso` / `new FreteEconomico` em `Calculadora.calcular` e em `Calculadora.criarFrete` | qual classe foi criada | `calculadora.calcular(5, 300) == 25.0`; `criarFrete("padrao", ...).getClass() == Frete.class` |
| **EOC** manual (`equals` → `==`) | 3: `Rastreio.mesmaEntrega`, `Calculadora.criarFrete` (×2) | comparação por conteúdo | usar `new String("BR123")` e `new String("economico")`: mesmo conteúdo, objetos diferentes |
| **IHI** (atributo que esconde o da classe pai) | 2: `peso` e `distancia` declarados de novo em `FreteExpresso` | o valor calculado pelo expresso | `new FreteExpresso(10, 100).calcular() == 50.0` (o mutante usa `peso = 0` e dá 20.0) |
| **PCI** (cast para uma subclasse) | 2: cast para `FreteExpresso` em `Pedido.valorTotal` e para `FreteEconomico` em `Pedido.prazoEntrega` | que o pedido funciona com **qualquer** modalidade | chamar `valorTotal()` e `prazoEntrega()` com pelo menos duas modalidades diferentes |

### 2.3 Mortos já na suíte inicial (para comparação)

| Operador | Por que morreu |
|---|---|
| JTD, JTI (`this`) | `peso = peso` deixa o atributo em 0, e `getPeso() > 0` falha |
| OAN, OMR (sobrecarga) | a chamada errada entra em recursão infinita (`StackOverflowError`) |
| ISD manual (`super.`) | mesmo motivo: sem o `super.`, o método chama a si mesmo |
| PCI (7 de 9) | o cast falha com `ClassCastException` quando o tipo real é outro |
| JSI em `Frete.peso` e `Frete.distancia` | vários testes criam fretes; o último sobrescreve o valor dos outros |

Esses operadores morreram por **erro de execução**, não por uma asserção bem
feita. Ou seja, a suíte inicial só pega defeitos de OO quando eles quebram o
programa.

---

## 3. Testes a criar na suíte reforçada

A lista abaixo mata todos os 53 sobreviventes matáveis.

**Valores exatos** (mata contas do PIT, IOD, IHI, PNC):
- `Frete(10, 100)` → `calcular() == 25.0`; `Frete(10, 1000)` → `prazoEmDias() == 5`
- `Frete(1, 1)` → `calcular() == 10.0` (taxa mínima)
- `FreteEconomico(10, 100)` → `20.0` e prazo `7`
- `FreteExpresso(10, 100)` → `50.0` e prazo `1`
- `FreteInternacional(10, 5000, "Portugal")` → `320.0` e prazo `23`
- `Pedido("Ana", 200, FreteExpresso(10, 100))` → `valorTotal() == 250.0`
- `Calculadora.calcular(5) == 11.0` e `calcular(5, 300) == 25.0`
- `getModalidade()` de cada classe, `getCliente()`, `getPaisDestino()`, `getCodigo()`

**Limites:** `new Frete(0, 100)` lança exceção; `new Frete(10, 0)` é aceito.

**Polimorfismo:** `Pedido` com pelo menos duas modalidades; `criarFrete`
devolve exatamente a classe pedida (`getClass()`), inclusive a padrão.

**Estado independente:** dois `Pedido`, dois `FreteInternacional` e dois
`Rastreio` com dados diferentes, conferindo os do primeiro.

**Comparação por conteúdo:** `Rastreio` e `criarFrete` com `new String(...)`;
códigos diferentes → `false`; `mesmaEntrega(null)` → `false`; `equals` com
código diferente → `false`; hashes diferentes para códigos diferentes.

Esses testes estão em `src/test/reforcada/java/frete/`. Cada um diz, num
comentário, quais mutantes mata.

## 4. Resultado da suíte reforçada

| | Inicial | Reforçada |
|---|---|---|
| Testes | 25 | 35 |
| Cobertura de linha | 100% | 100% |
| Cobertura de desvio | 90% | 100% |
| PIT, sem o equivalente | 30/58 = 52% | **58/58 = 100%** |
| muJava + manuais | 38/63 = 60% | **63/63 = 100%** |

Todos os 53 matáveis morreram. Sobraram só os 14 equivalentes, que nenhum
teste pode matar. A cobertura de linha ficou igual, o que confirma o ponto
do roteiro: 100% de cobertura não diz se os testes verificam alguma coisa.
