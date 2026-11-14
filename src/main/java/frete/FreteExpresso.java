package frete;

/**
 * Sobrescreve o cálculo por completo: o quilo é mais caro e há um adicional
 * de urgência. A entrega é feita no dia seguinte.
 */
public class FreteExpresso extends Frete {

    public static final double PRECO_POR_KG_EXPRESSO = 3.0;
    public static final double ADICIONAL_URGENCIA = 15.0;
    public static final int PRAZO_EXPRESSO_DIAS = 1;

    public FreteExpresso(double peso, double distancia) {
        super(peso, distancia);
    }

    @Override
    public double calcular() {
        return peso * PRECO_POR_KG_EXPRESSO + distancia * PRECO_POR_KM + ADICIONAL_URGENCIA;
    }

    @Override
    public int prazoEmDias() {
        return PRAZO_EXPRESSO_DIAS;
    }

    @Override
    public String getModalidade() {
        return "Expresso";
    }
}
