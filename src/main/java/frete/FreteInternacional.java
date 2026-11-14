package frete;

/**
 * Sobrescreve o cálculo, reaproveita o cálculo da classe pai (super) e soma
 * uma taxa alfandegária.
 */
public class FreteInternacional extends Frete {

    public static final double TAXA_ALFANDEGARIA = 50.0;
    public static final int DIAS_ALFANDEGA = 10;

    private String paisDestino;

    public FreteInternacional(double peso, double distancia, String paisDestino) {
        super(peso, distancia);
        this.paisDestino = paisDestino;
    }

    @Override
    public double calcular() {
        double valorBase = super.calcular();
        return valorBase + TAXA_ALFANDEGARIA;
    }

    @Override
    public int prazoEmDias() {
        return super.prazoEmDias() + DIAS_ALFANDEGA;
    }

    @Override
    public String getModalidade() {
        return "Internacional";
    }

    public String getPaisDestino() {
        return paisDestino;
    }
}
