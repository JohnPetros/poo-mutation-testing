package frete;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Mata IOD (remover o método sobrescrito) e as contas do PIT. */
public class FreteEconomicoTest {

    private static final double DELTA = 1e-9;

    /** 25 * (1 - 0,2) = 20. Sem a sobrescrita daria 25. */
    @Test
    public void calculaComDesconto() {
        Frete frete = new FreteEconomico(10, 100);
        assertEquals(20.0, frete.calcular(), DELTA);
    }

    /** 3 + 0 + 4 = 7. Sem a sobrescrita daria 3. */
    @Test
    public void calculaPrazoComDiasExtras() {
        Frete frete = new FreteEconomico(10, 100);
        assertEquals(7, frete.prazoEmDias());
    }

    @Test
    public void informaModalidade() {
        assertEquals("Economico", new FreteEconomico(10, 100).getModalidade());
    }
}
