package frete;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Suíte reforçada: cada teste compara com o valor exato esperado, em vez de
 * só verificar que o resultado "parece válido". Os comentários dizem quais
 * mutantes sobreviventes da suíte inicial cada teste mata.
 */
public class FreteTest {

    private static final double DELTA = 1e-9;

    /** PIT: troca de * por / e de + por - no cálculo. 10*2 + 100*0,05 = 25. */
    @Test
    public void calculaValorExato() {
        assertEquals(25.0, new Frete(10, 100).calcular(), DELTA);
    }

    /** PIT: remoção da condição da taxa mínima. 1*2 + 1*0,05 = 2,05 → 10. */
    @Test
    public void aplicaTaxaMinima() {
        assertEquals(Frete.TAXA_MINIMA, new Frete(1, 1).calcular(), DELTA);
    }

    /** PIT: troca de / por * e de + por - no prazo. 3 + 1000/500 = 5. */
    @Test
    public void calculaPrazoExato() {
        assertEquals(5, new Frete(10, 1000).prazoEmDias());
    }

    /** PIT: retorno trocado por "". */
    @Test
    public void informaModalidadeEDados() {
        Frete frete = new Frete(10, 100);
        assertEquals("Padrao", frete.getModalidade());
        assertEquals(10.0, frete.getPeso(), DELTA);
        assertEquals(100.0, frete.getDistancia(), DELTA);
    }

    /** PIT: limite peso <= 0 → peso < 0. Zero também é inválido. */
    @Test(expected = IllegalArgumentException.class)
    public void rejeitaPesoZero() {
        new Frete(0, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejeitaPesoNegativo() {
        new Frete(-1, 100);
    }

    /** PIT: limite distancia < 0 → distancia <= 0. Distância zero é aceita. */
    @Test
    public void aceitaDistanciaZero() {
        assertEquals(20.0, new Frete(10, 0).calcular(), DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejeitaDistanciaNegativa() {
        new Frete(10, -1);
    }
}
