package frete;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Mata IOD, IHI (atributo que esconde o da classe pai) e as contas do PIT. */
public class FreteExpressoTest {

    private static final double DELTA = 1e-9;

    /**
     * 10*3 + 100*0,05 + 15 = 50. Sem a sobrescrita daria 25; com um peso
     * declarado de novo na subclasse (IHI), o peso lido seria 0 e daria 20.
     */
    @Test
    public void calculaComAdicional() {
        Frete frete = new FreteExpresso(10, 100);
        assertEquals(50.0, frete.calcular(), DELTA);
    }

    /** Sempre 1 dia, mesmo numa distância que daria mais na base. */
    @Test
    public void entregaNoDiaSeguinte() {
        Frete frete = new FreteExpresso(10, 5000);
        assertEquals(1, frete.prazoEmDias());
    }

    @Test
    public void informaModalidade() {
        assertEquals("Expresso", new FreteExpresso(10, 100).getModalidade());
    }
}
