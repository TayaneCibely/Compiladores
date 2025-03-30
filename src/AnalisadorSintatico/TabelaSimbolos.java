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

    private Map<Integer, List<String>> duplicacoesDetectadas = new HashMap<>();

    public void adicionarSimbolo(Simbolo simbolo) {
        if (pilhaEscopos.isEmpty()) {
            entrarEscopo();
        }

        Map<String, Simbolo> escopoAtual = pilhaEscopos.get(pilhaEscopos.size() - 1);
        int escopoAtualIndice = pilhaEscopos.size() - 1;

        if (escopoAtual.containsKey(simbolo.getIdentificador())) {
            Simbolo simboloExistente = escopoAtual.get(simbolo.getIdentificador());

            String mensagem = "Símbolo '" + simbolo.getIdentificador() +
                    "' declarado múltiplas vezes no escopo " + escopoAtualIndice +
                    " (linhas " + simboloExistente.getLinha() + " e " + simbolo.getLinha() + ")";

            if (!duplicacoesDetectadas.containsKey(escopoAtualIndice)) {
                duplicacoesDetectadas.put(escopoAtualIndice, new ArrayList<>());
            }
            duplicacoesDetectadas.get(escopoAtualIndice).add(mensagem);
        }

        escopoAtual.put(simbolo.getIdentificador(), simbolo);
    }

    // registrar as variáveis utilizadas
    private Map<String, List<Integer>> variaveisUtilizadas = new HashMap<>();

    public void registrarUsoVariavel(String identificador, int linha) {
        if (!variaveisUtilizadas.containsKey(identificador)) {
            variaveisUtilizadas.put(identificador, new ArrayList<>());
        }
        variaveisUtilizadas.get(identificador).add(linha);
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

    public Map<Integer, List<String>> getDuplicacoesDetectadas() {
        return duplicacoesDetectadas;
    }

    public Map<String, List<Integer>> getVariaveisUtilizadas() {
        return variaveisUtilizadas;
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

