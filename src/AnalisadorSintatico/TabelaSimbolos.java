package AnalisadorSintatico;

import java.util.ArrayList;
import java.util.List;

public class TabelaSimbolos {
    private List<Simbolo> simbolos;

    public TabelaSimbolos() {
        this.simbolos = new ArrayList<>();
    }

    public void adicionarSimbolo(Simbolo simbolo) {
        simbolos.add(simbolo);
    }

    public List<Simbolo> getSimbolos() {
        return simbolos;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Simbolo simbolo : simbolos) {
            builder.append(simbolo.toString()).append("\n");
        }
        return builder.toString();
    }
}

