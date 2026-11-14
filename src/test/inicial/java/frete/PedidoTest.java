package frete;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PedidoTest {

    @Test
    public void calculaValorTotal() {
        Pedido pedido = new Pedido("Ana", 200, new FreteExpresso(10, 100));
        assertTrue(pedido.valorTotal() > 0);
    }

    @Test
    public void calculaPrazoEntrega() {
        Pedido pedido = new Pedido("Ana", 200, new FreteEconomico(10, 100));
        assertTrue(pedido.prazoEntrega() > 0);
    }

    @Test
    public void informaDados() {
        Pedido pedido = new Pedido("Ana", 200, new Frete(10, 100));
        assertNotNull(pedido.getCliente());
        assertTrue(pedido.getValorProdutos() > 0);
        assertNotNull(pedido.getFrete());
    }
}
