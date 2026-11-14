package frete;

/**
 * Identifica uma entrega pelo código de rastreio. Duas entregas são a mesma
 * quando têm o mesmo código, comparado por conteúdo e não por referência.
 */
public class Rastreio {

    private String codigo;

    public Rastreio(String codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException("O codigo e obrigatorio");
        }
        this.codigo = codigo;
    }

    public boolean mesmaEntrega(Rastreio outro) {
        return outro != null && this.codigo.equals(outro.codigo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rastreio)) {
            return false;
        }
        return mesmaEntrega((Rastreio) obj);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    public String getCodigo() {
        return codigo;
    }
}
