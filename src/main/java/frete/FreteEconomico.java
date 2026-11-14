package frete;

/**
 * Herda o cálculo da classe base e aplica um desconto. Em troca, a entrega
 * demora mais.
 */
public class FreteEconomico extends Frete {

    public static final double DESCONTO = 0.2;
    public static final int DIAS_EXTRAS = 4;

    public FreteEconomico(double peso, double distancia) {
        super(peso, distancia);
    }

    @Override
    public double calcular() {
        return super.calcular() * (1 - DESCONTO);
    }

    @Override
    public int prazoEmDias() {
        return super.prazoEmDias() + DIAS_EXTRAS;
    }

    @Override
    public String getModalidade() {
        return "Economico";
    }
}
