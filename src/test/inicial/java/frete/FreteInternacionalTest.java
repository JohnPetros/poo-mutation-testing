package frete;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FreteInternacionalTest {

    @Test
    public void calculaComTaxaAlfandegaria() {
        Frete frete = new FreteInternacional(10, 5000, "Portugal");
        assertTrue(frete.calcular() > 0);
    }

    @Test
    public void calculaPrazo() {
        Frete frete = new FreteInternacional(10, 5000, "Portugal");
        assertTrue(frete.prazoEmDias() > 0);
    }

    @Test
    public void informaModalidadeEPais() {
        FreteInternacional frete = new FreteInternacional(10, 5000, "Portugal");
        assertNotNull(frete.getModalidade());
        assertNotNull(frete.getPaisDestino());
    }
}
