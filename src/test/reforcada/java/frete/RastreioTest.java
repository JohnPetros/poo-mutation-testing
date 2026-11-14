package frete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Mata EOC (equals → ==), JSI no código e os retornos trocados do PIT. */
public class RastreioTest {

    /**
     * EOC: new String cria outro objeto com o mesmo conteúdo. Com ==, as duas
     * entregas seriam consideradas diferentes.
     */
    @Test
    public void reconheceMesmaEntregaPorConteudo() {
        Rastreio a = new Rastreio(new String("BR123"));
        Rastreio b = new Rastreio(new String("BR123"));
        assertTrue(a.mesmaEntrega(b));
        assertTrue(a.equals(b));
    }

    /** PIT: mesmaEntrega e equals trocados por "sempre true". */
    @Test
    public void distingueEntregasDiferentes() {
        Rastreio a = new Rastreio("BR1");
        Rastreio b = new Rastreio("BR2");
        assertFalse(a.mesmaEntrega(b));
        assertFalse(a.equals(b));
    }

    @Test
    public void naoEhAMesmaEntregaQueNull() {
        Rastreio a = new Rastreio("BR123");
        assertFalse(a.mesmaEntrega(null));
        assertFalse(a.equals(null));
    }

    @Test
    public void comparaComEquals() {
        Rastreio a = new Rastreio("BR123");
        assertTrue(a.equals(a));
        assertFalse(a.equals("BR123"));
    }

    /** JSI: se codigo fosse static, o segundo rastreio mudaria o primeiro. */
    @Test
    public void cadaRastreioTemSeuCodigo() {
        Rastreio a = new Rastreio("BR1");
        Rastreio b = new Rastreio("BR2");
        assertEquals("BR1", a.getCodigo());
        assertEquals("BR2", b.getCodigo());
    }

    /**
     * Contrato: objetos iguais têm o mesmo hash. PIT (hashCode → 0): códigos
     * diferentes devem gerar hashes diferentes. É uma propriedade de qualidade
     * (espalhamento), não de correção; ver analise/sobreviventes.md.
     */
    @Test
    public void geraHashPeloCodigo() {
        assertEquals(new Rastreio(new String("BR123")).hashCode(),
                new Rastreio(new String("BR123")).hashCode());
        assertNotEquals(new Rastreio("BR1").hashCode(), new Rastreio("BR2").hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejeitaCodigoNulo() {
        new Rastreio(null);
    }
}
