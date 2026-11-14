package frete;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CalculadoraTest {

    private final Calculadora calculadora = new Calculadora();

    @Test
    public void calculaEntregaLocal() {
        assertTrue(calculadora.calcular(5) > 0);
    }

    @Test
    public void calculaPorPesoEDistancia() {
        assertTrue(calculadora.calcular(5, 300) > 0);
    }

    @Test
    public void criaModalidades() {
        assertNotNull(calculadora.criarFrete("economico", 5, 300));
        assertNotNull(calculadora.criarFrete("expresso", 5, 300));
        assertNotNull(calculadora.criarFrete("padrao", 5, 300));
    }
}
