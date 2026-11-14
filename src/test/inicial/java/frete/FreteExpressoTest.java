package frete;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FreteExpressoTest {

    @Test
    public void calculaComAdicional() {
        Frete frete = new FreteExpresso(10, 100);
        assertTrue(frete.calcular() > 0);
    }

    @Test
    public void calculaPrazo() {
        Frete frete = new FreteExpresso(10, 100);
        assertTrue(frete.prazoEmDias() > 0);
    }

    @Test
    public void informaModalidade() {
        assertNotNull(new FreteExpresso(10, 100).getModalidade());
    }
}
