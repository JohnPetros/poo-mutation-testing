#!/usr/bin/env bash
# Mutação de nível de classe com o muJava.
#
# Uso: scripts/mujava.sh [inicial|reforcada]   (padrão: inicial)
#
# 1. Baixa o muJava (GitHub, commit fixo) para tools/mujava/repo, se faltar.
# 2. Compila o código e a suíte escolhida com o Maven.
# 3. Gera os mutantes de classe com todos os operadores OO do muJava e aplica
#    os mutantes manuais de analise/mutantes-manuais.txt.
# 4. Roda a suíte inteira contra cada mutante, numa JVM separada.
#
# Saída: resumo no terminal e target/mujava-reports/<suite>/mutantes.csv
set -euo pipefail

SUITE="${1:-inicial}"
TIMEOUT_MS="${TIMEOUT_MS:-5000}"
MUJAVA_COMMIT="dd0e7db532097530e0cc44026ac3ad1e8df7b496"

RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
cd "$RAIZ"

REPO="tools/mujava/repo"
DRIVER_BIN="target/mujava-driver"
HOME_MUJAVA="target/mujava/frete-$SUITE"
SAIDA="target/mujava-reports/$SUITE"
EQUIVALENTES="analise/mujava-equivalentes.txt"
MANUAIS="analise/mutantes-manuais.txt"

if [ ! -d "src/test/$SUITE/java" ]; then
    echo "Suite desconhecida: $SUITE (esperado: src/test/$SUITE/java)" >&2
    exit 2
fi

# 1. muJava
if [ ! -f "$REPO/openjava.jar" ]; then
    echo ">> Baixando o muJava ($MUJAVA_COMMIT)"
    mkdir -p "$REPO"
    git -C "$REPO" init -q
    git -C "$REPO" remote add origin https://github.com/jeffoffutt/muJava.git
    git -C "$REPO" fetch -q --depth 1 origin "$MUJAVA_COMMIT"
    git -C "$REPO" checkout -q FETCH_HEAD
fi
MUJAVA_CP="$REPO/bin:$REPO/openjava.jar:$REPO/commons-io-2.4.jar"

# 2. Código e suíte
echo ">> Compilando (suite: $SUITE)"
# Apaga só as classes compiladas (as duas suítes têm classes com o mesmo nome),
# sem um "mvn clean" que levaria junto os relatórios do JaCoCo e do PIT.
rm -rf target/classes target/test-classes
mvn -q -B test-compile dependency:build-classpath \
    -Dsuite="$SUITE" -Dmdep.includeScope=test -Dmdep.outputFile=target/junit.classpath
JUNIT_CP="$(cat target/junit.classpath)"

mkdir -p "$DRIVER_BIN"
javac -nowarn -cp "$MUJAVA_CP" -d "$DRIVER_BIN" tools/mujava/driver/MuJavaHeadless.java

# 3. Geração
echo ">> Gerando mutantes de classe"
rm -rf "$HOME_MUJAVA"
mkdir -p "$HOME_MUJAVA"/{src,classes,result,testset}
cp -r src/main/java/. "$HOME_MUJAVA/src/"
cp -r target/classes/. "$HOME_MUJAVA/classes/"
java -cp "$DRIVER_BIN:$MUJAVA_CP:$HOME_MUJAVA/classes" MuJavaHeadless gerar "$HOME_MUJAVA" \
    > "$HOME_MUJAVA/geracao.log" 2>&1 || { cat "$HOME_MUJAVA/geracao.log" >&2; exit 1; }

# 3b. Mutantes manuais (operadores que o muJava não gera para este código)
if [ -f "$MANUAIS" ]; then
    echo ">> Aplicando mutantes manuais de $MANUAIS"
    java -cp "$DRIVER_BIN:$MUJAVA_CP" MuJavaHeadless manuais "$HOME_MUJAVA" "$MANUAIS"
fi

# 4. Execução
echo ">> Executando a suite '$SUITE' contra os mutantes"
ARGS=(executar "$HOME_MUJAVA" target/test-classes "$JUNIT_CP" "$TIMEOUT_MS" "$SAIDA/mutantes.csv")
if [ -f "$EQUIVALENTES" ]; then
    ARGS+=("$EQUIVALENTES")
fi
mkdir -p "$SAIDA"
java -cp "$DRIVER_BIN:$MUJAVA_CP" MuJavaHeadless "${ARGS[@]}" | tee "$SAIDA/resumo.txt"
