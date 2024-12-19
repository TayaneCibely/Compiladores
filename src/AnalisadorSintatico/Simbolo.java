package AnalisadorSintatico;

public class Simbolo {
    private String identificador;
    private String tipo;
    private int linha;
    private String valor;

    public Simbolo(String identificador, String tipo, int linha, String valor) {
        this.identificador = identificador;
        this.tipo = tipo;
        this.linha = linha;
        this.valor = valor;
    }

    // Getters e Setters
    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Simbolo{" +
                "identificador='" + identificador + '\'' +
                ", tipo='" + tipo + '\'' +
                ", linha=" + linha +
                ", valor='" + valor + '\'' +
                '}';
    }
}