package lexer;

public class Token {
    private final TipoToken tipo;
    private final String valor;

    public Token(TipoToken tipo, String valor){
        this.tipo = tipo;
        this.valor = valor;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    public String getValor(){
        return valor;
    }

    @Override
    public String toString(){
        return String.format("Token(tipo=%s, valor='%s')", tipo, valor);
    }
}
