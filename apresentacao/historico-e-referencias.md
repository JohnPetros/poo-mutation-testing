# Histórico e referências

Material de apoio para os tópicos "Histórico" e "Referências" da apresentação.
Os dados bibliográficos (volume, número, páginas, DOI) foram conferidos em
bases como IEEE, ACM, Crossref e nas páginas dos autores.

## Histórico

### As duas hipóteses que sustentam a técnica

O teste de mutação parte de duas hipóteses, formuladas por DeMillo, Lipton e
Sayward (1978):

- **Programador competente.** Programadores escrevem programas *quase*
  corretos. Os defeitos reais costumam ser pequenos desvios do programa
  certo: um operador trocado, uma condição invertida. Por isso faz sentido
  simular defeitos com pequenas alterações, que são os mutantes.
- **Efeito de acoplamento.** Testes que detectam defeitos simples também
  detectam a maioria dos defeitos complexos, que são combinações dos
  simples. Por isso basta gerar mutantes com uma única alteração.

### Linha do tempo

| Ano | Marco |
|---|---|
| **1971** | Richard Lipton, então aluno de pós-graduação na Carnegie Mellon, propõe a ideia num trabalho de disciplina, *Fault Diagnosis of Computer Programs* (Jia; Harman, 2011). |
| **1977** | Richard Hamlet publica *Testing Programs with the Aid of a Compiler*, que usa um compilador para gerar e testar variações do programa. |
| **1978** | DeMillo, Lipton e Sayward publicam *Hints on Test Data Selection*, o artigo que funda a técnica e formula as duas hipóteses. |
| **1980** | Timothy Budd defende em Yale a primeira tese de doutorado sobre análise de mutantes (Jia; Harman, 2011). |
| **Fim dos anos 1980** | Surge o **Mothra**, para Fortran 77, a primeira ferramenta de mutação amplamente usada (King; Offutt, 1991). |
| **1992** | Offutt confirma experimentalmente o efeito de acoplamento. |
| **Anos 1990** | O custo vira o foco das pesquisas, com técnicas como a mutação seletiva, que usa só um subconjunto dos operadores (Jia; Harman, 2011). |
| **2000** | Kim, Clark e McDermid propõem os primeiros operadores de mutação de **classe** para Java (herança, polimorfismo). |
| **2002** | Ma, Kwon e Offutt definem os operadores **inter-classe** para Java, base do muJava. |
| **2005** | O **muJava** é publicado. É a primeira ferramenta a gerar automaticamente mutantes de classe para Java. |
| **2009** | Começa o JaCoCo, a ferramenta de cobertura usada no trabalho. |
| **Década de 2010** | Henry Coles cria o **PIT**. As primeiras versões usavam o motor do Jumble; depois o PIT ganhou um motor próprio, que muta bytecode. A técnica chega à indústria com integração ao Maven e ao Gradle. |
| **2011** | Jia e Harman publicam o levantamento mais citado da área, cobrindo a evolução da técnica desde 1977. |
| **2014** | Just et al. mostram que matar mutantes está correlacionado com detectar **falhas reais**, independentemente da cobertura de código. |
| **2016** | O PIT é apresentado no ISSTA, uma das principais conferências de teste. |
| **2018** | O Google relata o uso de mutação em escala, mostrando mutantes sobreviventes durante a revisão de código. |
| **2019** | Papadakis et al. publicam um novo levantamento, com os avanços desde 2011. |

### Resumo em três fases

1. **Anos 1970: a ideia.** Lipton, Hamlet, DeMillo e Sayward definem a técnica
   e as hipóteses que a justificam.
2. **Anos 1980 e 1990: ferramentas e custo.** Surge o Mothra, as hipóteses
   são validadas e as pesquisas buscam reduzir o custo computacional.
3. **Anos 2000 em diante: OO e indústria.** Surgem os operadores de classe
   (muJava), ferramentas práticas (PIT) e evidências de que os mutantes
   representam falhas reais. Empresas como o Google passam a usar a técnica.

### Ligação com o nosso trabalho

- O artigo de **Just et al. (2014)** dá respaldo científico à conclusão do
  nosso exemplo: o mutation score avalia a suíte de testes de um jeito que
  a cobertura não avalia.
- Os operadores de classe que usamos (IOD, IHI, JSI, PNC, PCI, EOC…) vêm de
  **Ma, Kwon e Offutt (2002)** e foram implementados no **muJava (2005)**.

## Referências

Formato ABNT (NBR 6023), em ordem alfabética.

### Livro

AMMANN, Paul; OFFUTT, Jeff. **Introduction to software testing**. 2. ed.
Cambridge: Cambridge University Press, 2016. ISBN 978-1-107-17201-2.

### Artigos

COLES, Henry; LAURENT, Thomas; HENARD, Christopher; PAPADAKIS, Mike;
VENTRESQUE, Anthony. PIT: a practical mutation testing tool for Java. In:
INTERNATIONAL SYMPOSIUM ON SOFTWARE TESTING AND ANALYSIS (ISSTA), 25., 2016,
Saarbrücken. **Proceedings** [...]. New York: ACM, 2016. p. 449-452.
DOI: 10.1145/2931037.2948707.

DEMILLO, Richard A.; LIPTON, Richard J.; SAYWARD, Frederick G. Hints on test
data selection: help for the practicing programmer. **Computer**, v. 11,
n. 4, p. 34-41, abr. 1978. DOI: 10.1109/C-M.1978.218136.

HAMLET, Richard G. Testing programs with the aid of a compiler. **IEEE
Transactions on Software Engineering**, v. SE-3, n. 4, p. 279-290, jul. 1977.
DOI: 10.1109/TSE.1977.231145.

JIA, Yue; HARMAN, Mark. An analysis and survey of the development of
mutation testing. **IEEE Transactions on Software Engineering**, v. 37, n. 5,
p. 649-678, set. 2011. DOI: 10.1109/TSE.2010.62.

JUST, René; JALALI, Darioush; INOZEMTSEVA, Laura; ERNST, Michael D.; HOLMES,
Reid; FRASER, Gordon. Are mutants a valid substitute for real faults in
software testing? In: ACM SIGSOFT INTERNATIONAL SYMPOSIUM ON FOUNDATIONS OF
SOFTWARE ENGINEERING (FSE), 22., 2014, Hong Kong. **Proceedings** [...].
New York: ACM, 2014. p. 654-665.

KIM, Sunwoo; CLARK, John A.; McDERMID, John A. Class mutation: mutation
testing for object-oriented programs. In: NET.OBJECTDAYS CONFERENCE ON
OBJECT-ORIENTED SOFTWARE SYSTEMS, 2000. **Proceedings** [...].
2000.

KING, K. N.; OFFUTT, A. Jefferson. A Fortran language system for
mutation-based software testing. **Software: Practice and Experience**,
v. 21, n. 7, p. 685-718, jul. 1991. DOI: 10.1002/spe.4380210704.

MA, Yu-Seung; KWON, Yong-Rae; OFFUTT, Jeff. Inter-class mutation operators
for Java. In: INTERNATIONAL SYMPOSIUM ON SOFTWARE RELIABILITY ENGINEERING
(ISSRE), 13., 2002, Annapolis. **Proceedings** [...]. Los Alamitos: IEEE,
2002. p. 352-363. DOI: 10.1109/ISSRE.2002.1173287.

MA, Yu-Seung; OFFUTT, Jeff; KWON, Yong-Rae. MuJava: an automated class
mutation system. **Software Testing, Verification and Reliability**, v. 15,
n. 2, p. 97-133, jun. 2005.

OFFUTT, A. Jefferson. Investigations of the software testing coupling
effect. **ACM Transactions on Software Engineering and Methodology**, v. 1,
n. 1, p. 5-20, jan. 1992.

PAPADAKIS, Mike; KINTIS, Marinos; ZHANG, Jie; JIA, Yue; LE TRAON, Yves;
HARMAN, Mark. Mutation testing advances: an analysis and survey. In: MEMON,
Atif M. (ed.). **Advances in Computers**, v. 112. Cambridge: Elsevier, 2019.
cap. 6.

PETROVIĆ, Goran; IVANKOVIĆ, Marko. State of mutation testing at Google. In:
INTERNATIONAL CONFERENCE ON SOFTWARE ENGINEERING: SOFTWARE ENGINEERING IN
PRACTICE (ICSE-SEIP), 40., 2018, Gotemburgo. **Proceedings** [...]. New
York: ACM, 2018. p. 163-171. DOI: 10.1145/3183519.3183521.

### Sites das ferramentas

JACOCO. **JaCoCo: Java Code Coverage Library**. Disponível em:
https://www.jacoco.org/jacoco/. Acesso em: 14 nov. 2026.

MUJAVA. **µJava home page**. Disponível em:
https://cs.gmu.edu/~offutt/mujava/. Acesso em: 14 nov. 2026.

MUJAVA. **muJava**: repositório do código-fonte. Disponível em:
https://github.com/jeffoffutt/muJava. Acesso em: 14 nov. 2026.

PITEST. **PIT mutation testing**. Disponível em: https://pitest.org/.
Acesso em: 14 nov. 2026.

### Pontos a conferir antes da entrega

- **King e Offutt (1991):** as fontes divergem na página inicial (685 ou
  686). Confira no site da Wiley.
- **Kim, Clark e McDermid (2000):** não achamos as atas online. A cidade
  e o número de operadores (13 ou 14) não foram confirmados.
- **Papadakis et al. (2019):** falta o intervalo de páginas do capítulo.
  Confira no ScienceDirect.
- **O trabalho de Lipton de 1971** não foi publicado. Cite-o pelo
  levantamento de Jia e Harman (2011), como fizemos na linha do tempo.
