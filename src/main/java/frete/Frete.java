package frete;

/**
 * Modalidade padrão de frete. O valor depende do peso e da distância e nunca
 * fica abaixo da taxa mínima, que é uma constante de classe.
 */
public class Frete {

    public static final double TAXA_MINIMA = 10.0;
    public static final double PRECO_POR_KG = 2.0;
    public static final double PRECO_POR_KM = 0.05;
    public static final int PRAZO_BASE_DIAS = 3;
    public static final double KM_POR_DIA = 500.0;

    protected double peso;
    protected double distancia;

    public Frete(double peso, double distancia) {
        if (peso <= 0) {
            throw new IllegalArgumentException("O peso deve ser positivo");
        }
        if (distancia < 0) {
            throw new IllegalArgumentException("A distancia nao pode ser negativa");
        }
        this.peso = peso;
        this.distancia = distancia;
    }

    public double calcular() {
        double valor = peso * PRECO_POR_KG + distancia * PRECO_POR_KM;
        if (valor < TAXA_MINIMA) {
            return TAXA_MINIMA;
        }
        return valor;
    }

    public int prazoEmDias() {
        return PRAZO_BASE_DIAS + (int) (distancia / KM_POR_DIA);
    }

    public String getModalidade() {
        return "Padrao";
    }

    public double getPeso() {
        return peso;
    }

    public double getDistancia() {
        return distancia;
    }
}
