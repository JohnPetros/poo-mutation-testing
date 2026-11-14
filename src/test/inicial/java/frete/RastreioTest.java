package frete;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RastreioTest {

    @Test
    public void reconheceMesmaEntrega() {
        // Literais iguais apontam para a mesma String na JVM, entao este teste
        // tambem passaria se a comparacao fosse feita com == em vez de equals.
        Rastreio a = new Rastreio("BR123");
        Rastreio b = new Rastreio("BR123");
        assertTrue(a.mesmaEntrega(b));
    }

    @Test
    public void comparaComEquals() {
        Rastreio a = new Rastreio("BR123");
        assertTrue(a.equals(a));
        assertTrue(a.equals(new Rastreio("BR123")));
        assertFalse(a.equals("BR123"));
    }

    @Test
    public void geraHashEInformaCodigo() {
        Rastreio a = new Rastreio("BR123");
        assertTrue(a.hashCode() == a.hashCode());
        assertNotNull(a.getCodigo());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejeitaCodigoNulo() {
        new Rastreio(null);
    }
}
