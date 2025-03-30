package AnalisadorSemantico;

import AnalisadorSintatico.*;
import AnalisadorSintatico.Simbolo;

import java.util.*;

public class AnalisadorSemantico {
    private TabelaSimbolos tabelaSimbolos;
    private List<String> errosSemanticos;

    public AnalisadorSemantico(TabelaSimbolos tabelaSimbolos) {
        this.tabelaSimbolos = tabelaSimbolos;
        this.errosSemanticos = new ArrayList<>();
    }

    public boolean analisar() {
        verificarDeclaracoesDuplicadas();
        verificarVariaveisUtilizadas();
        verificarTiposCompativeis();
        verificarRetornoFuncoes();

        if (!errosSemanticos.isEmpty()) {
            System.err.println("\n=== Erros Semânticos ===");
            for (String erro : errosSemanticos) {
                System.err.println(erro);
            }
            return false;
        }

        return true;
    }

    //  verificações semânticas
    // verificar se existe mais de uma variável com o mesmo nome no mesmo escopo
    public void verificarDeclaracoesDuplicadas() {
        Map<Integer, List<String>> duplicacoes = tabelaSimbolos.getDuplicacoesDetectadas();

        for (Map.Entry<Integer, List<String>> entry : duplicacoes.entrySet()) {
            List<String> mensagensErro = entry.getValue();

            for (String mensagem : mensagensErro) {
                errosSemanticos.add("Erro: " + mensagem);
            }
        }
    }

    private void verificarVariaveisUtilizadas() {

    }

    private void verificarTiposCompativeis() {

    }

    private void verificarRetornoFuncoes() {

    }

    public List<String> getErrosSemanticos() {
        return errosSemanticos;
    }

    public boolean temErros() {
        return !errosSemanticos.isEmpty();
    }

}
