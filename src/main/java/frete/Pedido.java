package frete;

/**
 * Pedido da loja. Recebe a modalidade de frete de forma polimórfica, como
 * referência à classe base: não sabe qual subclasse está usando.
 */
public class Pedido {

    private String cliente;
    private double valorProdutos;
    private Frete frete;

    public Pedido(String cliente, double valorProdutos, Frete frete) {
        this.cliente = cliente;
        this.valorProdutos = valorProdutos;
        this.frete = frete;
    }

    public double valorTotal() {
        return valorProdutos + frete.calcular();
    }

    public int prazoEntrega() {
        return frete.prazoEmDias();
    }

    public String getCliente() {
        return cliente;
    }

    public double getValorProdutos() {
        return valorProdutos;
    }

    public Frete getFrete() {
        return frete;
    }
}
