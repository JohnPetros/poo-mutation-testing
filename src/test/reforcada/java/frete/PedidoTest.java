package frete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/** Mata PCI (cast para uma subclasse), JSI nos atributos e a conta do PIT. */
public class PedidoTest {

    private static final double DELTA = 1e-9;

    /** 200 + 50 = 250. */
    @Test
    public void calculaValorTotal() {
        Pedido pedido = new Pedido("Ana", 200, new FreteExpresso(10, 100));
        assertEquals(250.0, pedido.valorTotal(), DELTA);
    }

    /**
     * Polimorfismo: o pedido funciona com qualquer modalidade. Um cast para
     * uma subclasse fixa (PCI) lança ClassCastException em alguma delas.
     */
    @Test
    public void valorTotalComQualquerModalidade() {
        assertEquals(225.0, new Pedido("Ana", 200, new Frete(10, 100)).valorTotal(), DELTA);
        assertEquals(220.0, new Pedido("Ana", 200, new FreteEconomico(10, 100)).valorTotal(), DELTA);
        assertEquals(250.0, new Pedido("Ana", 200, new FreteExpresso(10, 100)).valorTotal(), DELTA);
    }

    @Test
    public void prazoComQualquerModalidade() {
        assertEquals(3, new Pedido("Ana", 200, new Frete(10, 100)).prazoEntrega());
        assertEquals(7, new Pedido("Ana", 200, new FreteEconomico(10, 100)).prazoEntrega());
        assertEquals(1, new Pedido("Ana", 200, new FreteExpresso(10, 100)).prazoEntrega());
    }

    @Test
    public void informaDados() {
        Frete frete = new Frete(10, 100);
        Pedido pedido = new Pedido("Ana", 200, frete);
        assertEquals("Ana", pedido.getCliente());
        assertEquals(200.0, pedido.getValorProdutos(), DELTA);
        assertSame(frete, pedido.getFrete());
    }

    /** JSI: se algum atributo fosse static, o segundo pedido mudaria o primeiro. */
    @Test
    public void cadaPedidoTemSeusDados() {
        Frete freteAna = new FreteExpresso(10, 100);
        Pedido ana = new Pedido("Ana", 200, freteAna);
        new Pedido("Bruno", 50, new Frete(1, 1));
        assertEquals("Ana", ana.getCliente());
        assertEquals(200.0, ana.getValorProdutos(), DELTA);
        assertSame(freteAna, ana.getFrete());
        assertEquals(250.0, ana.valorTotal(), DELTA);
    }
}
