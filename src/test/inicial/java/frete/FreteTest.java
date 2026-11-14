package frete;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Suíte inicial: executa todas as linhas, mas as asserções são fracas
 * (só verificam que o resultado "parece válido").
 */
public class FreteTest {

    @Test
    public void calculaValorPadrao() {
        Frete frete = new Frete(10, 100);
        assertTrue(frete.calcular() > 0);
    }

    @Test
    public void aplicaTaxaMinima() {
        Frete frete = new Frete(1, 1);
        assertTrue(frete.calcular() > 0);
    }

    @Test
    public void calculaPrazo() {
        Frete frete = new Frete(10, 1000);
        assertTrue(frete.prazoEmDias() > 0);
    }

    @Test
    public void informaModalidadeEDados() {
        Frete frete = new Frete(10, 100);
        assertNotNull(frete.getModalidade());
        assertTrue(frete.getPeso() > 0);
        assertTrue(frete.getDistancia() > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejeitaPesoInvalido() {
        new Frete(-1, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejeitaDistanciaInvalida() {
        new Frete(10, -1);
    }
}
