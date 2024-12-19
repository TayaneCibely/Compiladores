package AnalisadorLexico;

public class Token {
    private final TipoToken tipo;
    private final String valor;
    private final int linha;

    public Token(TipoToken tipo, String valor, int linha){
        this.tipo = tipo;
        this.valor = valor;
        this.linha = linha;
    }

    public TipoToken getTipo() {
        return tipo; 
    }

    public String getValor(){
        return valor;
    }

    public int getLinha(){ return linha; }

    @Override
    public String toString(){
        return String.format("Token(tipo=%s, lexema='%s', linha=%d)", tipo, valor, linha);
    }
}
