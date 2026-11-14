import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import mujava.ClassMutantsGenerator;
import mujava.MutationSystem;
import mujava.util.InheritanceINFO;

/**
 * Driver sem interface gráfica para o muJava.
 *
 * O muJava só gera mutantes de nível de classe pela GUI (a CLI oficial cobre
 * apenas os operadores tradicionais). Este driver chama a mesma API que a GUI
 * usa para gerar os mutantes e, em vez do executor do muJava (que roda uma
 * única classe de teste por classe alvo), executa a suíte JUnit inteira contra
 * cada mutante numa JVM separada, como o PIT faz.
 *
 * Uso:
 *   gerar    <mujava_home>
 *   executar <mujava_home> <test_classes> <junit_classpath> <timeout_ms> <saida_csv> [equivalentes]
 */
public class MuJavaHeadless {

    public static void main(String[] args) throws Exception {
        if (args.length >= 1 && args[0].equals("gerar") && args.length == 2) {
            gerar(new File(args[1]).getAbsolutePath());
        } else if (args.length >= 1 && args[0].equals("manuais") && args.length == 3) {
            aplicarManuais(new File(args[1]).getAbsolutePath(), new File(args[2]));
        } else if (args.length >= 1 && args[0].equals("executar") && (args.length == 6 || args.length == 7)) {
            executar(new File(args[1]).getAbsolutePath(), args[2], args[3],
                    Long.parseLong(args[4]), new File(args[5]), args.length == 7 ? new File(args[6]) : null);
        } else {
            System.err.println("Uso: gerar <mujava_home>");
            System.err.println("     manuais <mujava_home> <arquivo_de_mutantes>");
            System.err.println("     executar <mujava_home> <test_classes> <junit_cp> <timeout_ms> <saida_csv> [equivalentes]");
            System.exit(2);
        }
    }

    // ------------------------------------------------------------------ geração

    static void gerar(String home) throws Exception {
        MutationSystem.setJMutationStructure(home);
        registrarHeranca();

        List<String> arquivos = new ArrayList<String>();
        listarFontes(new File(MutationSystem.SRC_PATH), "", arquivos);
        Collections.sort(arquivos);

        for (String arquivo : arquivos) {
            System.out.println("== Gerando mutantes de classe para " + arquivo);
            prepararDiretorios(arquivo);
            File original = new File(MutationSystem.SRC_PATH, arquivo);
            ClassMutantsGenerator gerador = new ClassMutantsGenerator(original, MutationSystem.cm_operators);
            gerador.makeMutants();
        }
    }

    /**
     * Equivalente a MutationSystem.recordInheritanceRelation(), sem o truque de
     * chamar URLClassLoader.addURL no class loader do sistema, que só funciona
     * até o Java 8. Aqui as classes originais já estão no classpath da JVM.
     */
    static void registrarHeranca() throws ClassNotFoundException {
        String[] nomes = MutationSystem.getAllClassNames(null, MutationSystem.CLASS_PATH);
        if (nomes == null || nomes.length == 0) {
            throw new IllegalStateException("Nenhuma classe em " + MutationSystem.CLASS_PATH);
        }
        InheritanceINFO[] info = new InheritanceINFO[nomes.length];
        for (int i = 0; i < nomes.length; i++) {
            Class<?> pai = Class.forName(nomes[i]).getSuperclass();
            String nomePai = (pai == null || pai == Object.class) ? "" : pai.getName();
            info[i] = new InheritanceINFO(nomes[i], nomePai);
        }
        for (InheritanceINFO filho : info) {
            for (InheritanceINFO candidato : info) {
                if (candidato.getClassName().equals(filho.getParentName())) {
                    filho.setParent(candidato);
                    candidato.addChild(filho);
                    break;
                }
            }
        }
        MutationSystem.classInfo = info;
    }

    static void listarFontes(File dir, String prefixo, List<String> saida) {
        File[] filhos = dir.listFiles();
        if (filhos == null) {
            return;
        }
        for (File f : filhos) {
            if (f.isDirectory()) {
                listarFontes(f, prefixo + f.getName() + "/", saida);
            } else if (f.getName().endsWith(".java")) {
                saida.add(prefixo + f.getName());
            }
        }
    }

    /** Mesmo preparo que a GUI faz em MutantsGenPanel.setMutationSystemPathFor. */
    static void prepararDiretorios(String arquivo) {
        String nome = arquivo.substring(0, arquivo.length() - ".java".length()).replace('/', '.');
        int ponto = nome.lastIndexOf('.');
        MutationSystem.CLASS_NAME = ponto >= 0 ? nome.substring(ponto + 1) : nome;

        String base = MutationSystem.MUTANT_HOME + "/" + nome;
        new File(base).mkdirs();
        MutationSystem.CLASS_MUTANT_PATH = base + "/" + MutationSystem.CM_DIR_NAME;
        MutationSystem.TRADITIONAL_MUTANT_PATH = base + "/" + MutationSystem.TM_DIR_NAME;
        MutationSystem.ORIGINAL_PATH = base + "/" + MutationSystem.ORIGINAL_DIR_NAME;
        new File(MutationSystem.CLASS_MUTANT_PATH).mkdirs();
        new File(MutationSystem.TRADITIONAL_MUTANT_PATH).mkdirs();
        new File(MutationSystem.ORIGINAL_PATH).mkdirs();
        MutationSystem.DIR_NAME = nome;
    }

    // ------------------------------------------------------- mutantes manuais

    /**
     * Aplica os mutantes descritos no arquivo (formato
     * "classe | operador | trecho original | trecho mutado") sobre o código
     * atual e grava cada um na mesma estrutura que o muJava usa
     * (result/classe/class_mutants/OP_n), marcado como [manual] no log.
     */
    static void aplicarManuais(String home, File arquivo) throws IOException {
        List<String> linhas = java.nio.file.Files.readAllLines(arquivo.toPath(),
                java.nio.charset.StandardCharsets.UTF_8);
        int total = 0;
        for (int n = 0; n < linhas.size(); n++) {
            String linha = linhas.get(n).trim();
            if (linha.isEmpty() || linha.startsWith("#")) {
                continue;
            }
            String[] p = linha.split(" \\| ", 4);
            if (p.length != 4) {
                throw new IllegalArgumentException(arquivo + ":" + (n + 1) + ": esperado 4 campos separados por ' | '");
            }
            String classe = p[0].trim();
            String operador = p[1].trim();
            String original = p[2].trim();
            String mutado = p[3].trim();

            String simples = classe.substring(classe.lastIndexOf('.') + 1);
            File fonte = new File(home + "/src/" + classe.replace('.', '/') + ".java");
            String codigo = new String(java.nio.file.Files.readAllBytes(fonte.toPath()),
                    java.nio.charset.StandardCharsets.UTF_8);
            int pos = codigo.indexOf(original);
            if (pos < 0 || codigo.indexOf(original, pos + 1) >= 0) {
                throw new IllegalArgumentException(arquivo + ":" + (n + 1) + ": o trecho original deve aparecer "
                        + "exatamente uma vez em " + fonte.getName() + ": " + original);
            }
            int linhaFonte = 1;
            for (int i = 0; i < pos; i++) {
                if (codigo.charAt(i) == '\n') {
                    linhaFonte++;
                }
            }

            File cm = new File(home + "/result/" + classe + "/" + MutationSystem.CM_DIR_NAME);
            cm.mkdirs();
            int indice = 1;
            while (new File(cm, operador + "_" + indice).exists()) {
                indice++;
            }
            File dir = new File(cm, operador + "_" + indice);
            dir.mkdirs();
            String mutante = codigo.substring(0, pos) + mutado + codigo.substring(pos + original.length());
            java.nio.file.Files.write(new File(dir, simples + ".java").toPath(),
                    mutante.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            PrintWriter log = new PrintWriter(new FileWriter(new File(cm, "mutation_log"), true));
            try {
                log.println(dir.getName() + ":" + linhaFonte + ":[manual] " + original + " => " + mutado);
            } finally {
                log.close();
            }
            System.out.println("[manual] " + classe + ":" + dir.getName() + "  " + original + " => " + mutado);
            total++;
        }
        System.out.println(total + " mutantes manuais aplicados");
    }

    // ---------------------------------------------------------------- execução

    static class Mutante {
        String classe;
        String id;
        String operador;
        String descricao = "";
        File dir;
        String status;
        String detalhe = "";
    }

    static void executar(String home, String testClasses, String junitCp, long timeoutMs,
                         File saidaCsv, File arquivoEquivalentes) throws Exception {
        String classesOriginais = home + "/classes";
        List<String> testes = listarClassesDeTeste(new File(testClasses));
        if (testes.isEmpty()) {
            throw new IllegalStateException("Nenhuma classe *Test encontrada em " + testClasses);
        }
        Map<String, String> equivalentes = lerEquivalentes(arquivoEquivalentes);

        String cpBase = classesOriginais + File.pathSeparator + testClasses + File.pathSeparator + junitCp;
        int original = rodarSuite(cpBase, testes, timeoutMs);
        if (original != 0) {
            throw new IllegalStateException("A suíte falha no código original (saída " + original + ")");
        }

        List<Mutante> mutantes = coletarMutantes(home);
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        long inicio = System.currentTimeMillis();

        for (Mutante m : mutantes) {
            if (equivalentes.containsKey(m.id)) {
                m.status = "EQUIVALENTE";
                m.detalhe = equivalentes.get(m.id);
                continue;
            }
            File bin = new File(m.dir, "bin");
            bin.mkdirs();
            List<String> argsJavac = new ArrayList<String>();
            Collections.addAll(argsJavac, "--release", "8", "-nowarn", "-encoding", "UTF-8",
                    "-cp", classesOriginais, "-d", bin.getPath());
            for (File f : m.dir.listFiles()) {
                if (f.getName().endsWith(".java")) {
                    argsJavac.add(f.getPath());
                }
            }
            java.io.ByteArrayOutputStream erros = new java.io.ByteArrayOutputStream();
            int compilou = javac.run(null, erros, erros, argsJavac.toArray(new String[0]));
            if (compilou != 0) {
                m.status = "NATIMORTO";
                m.detalhe = primeiraLinhaDeErro(erros.toString("UTF-8"));
                continue;
            }
            int r = rodarSuite(bin.getPath() + File.pathSeparator + cpBase, testes, timeoutMs);
            if (r == 0) {
                m.status = "SOBREVIVENTE";
            } else if (r == -1) {
                m.status = "MORTO";
                m.detalhe = "timeout";
            } else {
                m.status = "MORTO";
            }
            System.out.print(m.status.charAt(0));
            System.out.flush();
        }
        System.out.println();
        long duracao = System.currentTimeMillis() - inicio;

        escreverCsv(mutantes, saidaCsv);
        imprimirResumo(mutantes, duracao);
    }

    static List<String> listarClassesDeTeste(File dir) {
        List<String> nomes = new ArrayList<String>();
        coletarTestes(dir, "", nomes);
        Collections.sort(nomes);
        return nomes;
    }

    static void coletarTestes(File dir, String pacote, List<String> saida) {
        File[] filhos = dir.listFiles();
        if (filhos == null) {
            return;
        }
        for (File f : filhos) {
            if (f.isDirectory()) {
                coletarTestes(f, pacote + f.getName() + ".", saida);
            } else if (f.getName().endsWith("Test.class")) {
                saida.add(pacote + f.getName().substring(0, f.getName().length() - ".class".length()));
            }
        }
    }

    /** Roda a suíte numa JVM nova. Retorna o código de saída, ou -1 em caso de timeout. */
    static int rodarSuite(String classpath, List<String> testes, long timeoutMs) throws Exception {
        List<String> cmd = new ArrayList<String>();
        cmd.add(System.getProperty("java.home") + "/bin/java");
        Collections.addAll(cmd, "-Xss512k", "-cp", classpath, "org.junit.runner.JUnitCore");
        cmd.addAll(testes);
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        Process p = pb.start();
        if (!p.waitFor(timeoutMs, TimeUnit.MILLISECONDS)) {
            p.destroyForcibly();
            p.waitFor();
            return -1;
        }
        return p.exitValue();
    }

    static List<Mutante> coletarMutantes(String home) throws IOException {
        List<Mutante> lista = new ArrayList<Mutante>();
        File[] classes = new File(home, "result").listFiles();
        if (classes == null) {
            return lista;
        }
        java.util.Arrays.sort(classes);
        for (File dirClasse : classes) {
            File cm = new File(dirClasse, MutationSystem.CM_DIR_NAME);
            File[] dirs = cm.listFiles();
            if (dirs == null) {
                continue;
            }
            Map<String, String> descricoes = lerLogDeMutacao(new File(cm, "mutation_log"));
            List<File> ordenados = new ArrayList<File>();
            for (File d : dirs) {
                if (d.isDirectory() && d.getName().matches("[A-Z]+_\\d+")) {
                    ordenados.add(d);
                }
            }
            Collections.sort(ordenados, (a, b) -> {
                String[] x = a.getName().split("_");
                String[] y = b.getName().split("_");
                int c = x[0].compareTo(y[0]);
                return c != 0 ? c : Integer.compare(Integer.parseInt(x[1]), Integer.parseInt(y[1]));
            });
            for (File d : ordenados) {
                Mutante m = new Mutante();
                m.classe = dirClasse.getName();
                m.dir = d;
                m.operador = d.getName().substring(0, d.getName().indexOf('_'));
                m.id = m.classe + ":" + d.getName();
                String desc = descricoes.get(d.getName());
                if (desc != null) {
                    m.descricao = desc;
                }
                lista.add(m);
            }
        }
        return lista;
    }

    /** O muJava registra cada mutante como "OP_n:linha:descrição" em mutation_log. */
    static Map<String, String> lerLogDeMutacao(File log) throws IOException {
        Map<String, String> mapa = new HashMap<String, String>();
        if (!log.exists()) {
            return mapa;
        }
        BufferedReader r = new BufferedReader(new FileReader(log));
        try {
            String linha;
            while ((linha = r.readLine()) != null) {
                int i = linha.indexOf(':');
                if (i > 0) {
                    mapa.put(linha.substring(0, i), linha.substring(i + 1).trim());
                }
            }
        } finally {
            r.close();
        }
        return mapa;
    }

    /** Formato: "classe:OP_n  justificativa", uma por linha; '#' inicia comentário. */
    static Map<String, String> lerEquivalentes(File arquivo) throws IOException {
        Map<String, String> mapa = new HashMap<String, String>();
        if (arquivo == null || !arquivo.exists()) {
            return mapa;
        }
        BufferedReader r = new BufferedReader(new FileReader(arquivo));
        try {
            String linha;
            while ((linha = r.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }
                String[] partes = linha.split("\\s+", 2);
                mapa.put(partes[0], partes.length > 1 ? partes[1] : "");
            }
        } finally {
            r.close();
        }
        return mapa;
    }

    static String primeiraLinhaDeErro(String saida) {
        for (String linha : saida.split("\\R")) {
            int i = linha.indexOf("error:");
            if (i < 0) {
                i = linha.indexOf("erro:");
            }
            if (i >= 0) {
                return linha.substring(linha.indexOf(':', i) + 1).trim();
            }
        }
        return "não compila";
    }

    static void escreverCsv(List<Mutante> mutantes, File saida) throws IOException {
        saida.getParentFile().mkdirs();
        PrintWriter w = new PrintWriter(new FileWriter(saida));
        try {
            w.println("classe,mutante,operador,status,descricao,detalhe");
            for (Mutante m : mutantes) {
                w.println(csv(m.classe) + "," + csv(m.dir.getName()) + "," + csv(m.operador) + ","
                        + csv(m.status) + "," + csv(m.descricao) + "," + csv(m.detalhe));
            }
        } finally {
            w.close();
        }
    }

    static String csv(String s) {
        return "\"" + (s == null ? "" : s.replace("\"", "\"\"")) + "\"";
    }

    static void imprimirResumo(List<Mutante> mutantes, long duracaoMs) {
        Map<String, int[]> porOperador = new TreeMap<String, int[]>();
        Map<String, int[]> porClasse = new TreeMap<String, int[]>();
        int[] total = new int[4];
        for (Mutante m : mutantes) {
            int idx = indice(m.status);
            contar(porOperador, m.operador, idx);
            contar(porClasse, m.classe, idx);
            total[idx]++;
        }
        System.out.println();
        System.out.println("Legenda: M = mortos, S = sobreviventes, E = equivalentes, N = natimortos (nao compilam)");
        imprimirTabela("Operador", porOperador);
        imprimirTabela("Classe", porClasse);
        System.out.println();
        System.out.printf(Locale.ROOT, "Gerados: %d | Mortos: %d | Sobreviventes: %d | Equivalentes: %d | Natimortos: %d%n",
                mutantes.size(), total[0], total[1], total[2], total[3]);
        int validos = total[0] + total[1];
        System.out.printf(Locale.ROOT, "Mutation score de classe = mortos / (gerados - equivalentes - natimortos) = %d/%d = %s%n",
                total[0], validos, validos == 0 ? "n/a" : String.format(Locale.ROOT, "%.0f%%", 100.0 * total[0] / validos));
        System.out.printf(Locale.ROOT, "Tempo de execucao: %.1f s%n", duracaoMs / 1000.0);
        System.out.println();
        System.out.println("Sobreviventes:");
        for (Mutante m : mutantes) {
            if ("SOBREVIVENTE".equals(m.status)) {
                System.out.println("  " + m.id + "  " + m.descricao);
            }
        }
    }

    static int indice(String status) {
        if ("MORTO".equals(status)) return 0;
        if ("SOBREVIVENTE".equals(status)) return 1;
        if ("EQUIVALENTE".equals(status)) return 2;
        return 3;
    }

    static void contar(Map<String, int[]> mapa, String chave, int idx) {
        int[] c = mapa.get(chave);
        if (c == null) {
            c = new int[4];
            mapa.put(chave, c);
        }
        c[idx]++;
    }

    static void imprimirTabela(String titulo, Map<String, int[]> mapa) {
        System.out.println();
        System.out.printf(Locale.ROOT, "%-26s %4s %4s %4s %4s%n", titulo, "M", "S", "E", "N");
        for (Map.Entry<String, int[]> e : mapa.entrySet()) {
            int[] c = e.getValue();
            System.out.printf(Locale.ROOT, "%-26s %4d %4d %4d %4d%n", e.getKey(), c[0], c[1], c[2], c[3]);
        }
    }
}
