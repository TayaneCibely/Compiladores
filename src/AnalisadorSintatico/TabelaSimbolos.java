package AnalisadorSintatico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabelaSimbolos {
//    private List<Simbolo> simbolos;
    // criando uma pilha de escopos
    private boolean escopoAtivo = true;
    private List<Boolean> escoposAtivos = new ArrayList<>();
    private List<Map<String, Simbolo>> pilhaEscopos;


    public TabelaSimbolos() {
//        this.simbolos = new ArrayList<>();
        this.pilhaEscopos = new ArrayList<>();
        entrarEscopo();
    }

    public void entrarEscopo() {
        pilhaEscopos.add(new HashMap<>());
        escoposAtivos.add(true);
    }

    public void sairEscopo() {
        if (!pilhaEscopos.isEmpty()) {
            int ultimoIndice = escoposAtivos.size() - 1;
            if (ultimoIndice >= 0) {
                escoposAtivos.set(ultimoIndice, false);
            }
        }
    }

    public void adicionarSimbolo(Simbolo simbolo) {
//        simbolos.add(simbolo);
        if (pilhaEscopos.isEmpty()) {
            entrarEscopo();
        }

        Map<String, Simbolo> escopoAtual = pilhaEscopos.get(pilhaEscopos.size() - 1);
        escopoAtual.put(simbolo.getIdentificador(), simbolo);
    }

    public Simbolo buscarSimbolo(String identificador) {
        for(int i = pilhaEscopos.size()-1; i>=0; i--) {
            Map<String, Simbolo> escopo = pilhaEscopos.get(i);
            if(escopo.containsKey(identificador)) {
                return escopo.get(identificador);
            }
        }
        return null;
    }

    // recuperar todos os símbolos
    public List<Simbolo> getSimbolos() {
        List<Simbolo> todosSimbolos = new ArrayList<>();
        for(Map<String, Simbolo> escopo : pilhaEscopos) {
            todosSimbolos.addAll(escopo.values());
        }
        return todosSimbolos;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pilhaEscopos.size(); i++) {
            boolean ativo = i < escoposAtivos.size() ? escoposAtivos.get(i) : false;
            String status = ativo ? "ativo" : "inativo";
            builder.append("Escopo ").append(i).append(" (").append(status).append("):\n");
            Map<String, Simbolo> escopo = pilhaEscopos.get(i);
            for (Simbolo simbolo : escopo.values()) {
                builder.append("  ").append(simbolo.toString()).append("\n");
            }
        }
        return builder.toString();
    }
}

