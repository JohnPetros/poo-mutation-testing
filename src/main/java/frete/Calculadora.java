package frete;

/**
 * Ponto de entrada para cotações. Oferece o cálculo sobrecarregado (só por
 * peso, para entrega local, ou por peso e distância) e cria a modalidade de
 * frete pedida.
 */
public class Calculadora {

    public static final double DISTANCIA_LOCAL = 20.0;

    /** Entrega local: a distância é fixa. */
    public double calcular(double peso) {
        return calcular(peso, DISTANCIA_LOCAL);
    }

    public double calcular(double peso, double distancia) {
        Frete frete = new Frete(peso, distancia);
        return frete.calcular();
    }

    public Frete criarFrete(String modalidade, double peso, double distancia) {
        if ("economico".equals(modalidade)) {
            return new FreteEconomico(peso, distancia);
        }
        if ("expresso".equals(modalidade)) {
            return new FreteExpresso(peso, distancia);
        }
        return new Frete(peso, distancia);
    }
}
