package frete;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Mata IOD, JSI no país de destino e as contas do PIT. */
public class FreteInternacionalTest {

    private static final double DELTA = 1e-9;

    /** 10*2 + 5000*0,05 + 50 = 320. */
    @Test
    public void calculaComTaxaAlfandegaria() {
        Frete frete = new FreteInternacional(10, 5000, "Portugal");
        assertEquals(320.0, frete.calcular(), DELTA);
    }

    /** 3 + 5000/500 + 10 = 23. */
    @Test
    public void calculaPrazoComAlfandega() {
        Frete frete = new FreteInternacional(10, 5000, "Portugal");
        assertEquals(23, frete.prazoEmDias());
    }

    @Test
    public void informaModalidadeEPais() {
        FreteInternacional frete = new FreteInternacional(10, 5000, "Portugal");
        assertEquals("Internacional", frete.getModalidade());
        assertEquals("Portugal", frete.getPaisDestino());
    }

    /** JSI: se paisDestino fosse static, o segundo frete mudaria o primeiro. */
    @Test
    public void cadaFreteTemSeuPais() {
        FreteInternacional portugal = new FreteInternacional(10, 5000, "Portugal");
        FreteInternacional japao = new FreteInternacional(10, 18000, "Japao");
        assertEquals("Portugal", portugal.getPaisDestino());
        assertEquals("Japao", japao.getPaisDestino());
    }
}
