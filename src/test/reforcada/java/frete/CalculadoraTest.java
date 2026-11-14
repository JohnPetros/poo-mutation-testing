package frete;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Mata PNC (instanciar outra classe), EOC (equals → ==) e os do PIT. */
public class CalculadoraTest {

    private static final double DELTA = 1e-9;

    private final Calculadora calculadora = new Calculadora();

    /** 5*2 + 20*0,05 = 11. */
    @Test
    public void calculaEntregaLocal() {
        assertEquals(11.0, calculadora.calcular(5), DELTA);
    }

    /** 5*2 + 300*0,05 = 25. Com FreteExpresso daria 45; com FreteEconomico, 20. */
    @Test
    public void calculaPorPesoEDistancia() {
        assertEquals(25.0, calculadora.calcular(5, 300), DELTA);
    }

    /** Cada modalidade cria exatamente a classe pedida, inclusive a padrão. */
    @Test
    public void criaModalidades() {
        assertEquals(FreteEconomico.class, calculadora.criarFrete("economico", 5, 300).getClass());
        assertEquals(FreteExpresso.class, calculadora.criarFrete("expresso", 5, 300).getClass());
        assertEquals(Frete.class, calculadora.criarFrete("padrao", 5, 300).getClass());
    }

    /**
     * EOC: new String cria um objeto diferente do literal, com o mesmo
     * conteúdo. Com == no lugar de equals, cairia no frete padrão.
     */
    @Test
    public void comparaModalidadePorConteudo() {
        assertEquals(FreteEconomico.class,
                calculadora.criarFrete(new String("economico"), 5, 300).getClass());
        assertEquals(FreteExpresso.class,
                calculadora.criarFrete(new String("expresso"), 5, 300).getClass());
    }

    @Test
    public void criaComOsDadosInformados() {
        Frete frete = calculadora.criarFrete("expresso", 5, 300);
        assertEquals(5.0, frete.getPeso(), DELTA);
        assertEquals(300.0, frete.getDistancia(), DELTA);
    }
}
