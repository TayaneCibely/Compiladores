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
    //  verificar se existe mais de uma variável com o mesmo nome no mesmo escopo
    public void verificarDeclaracoesDuplicadas() {
        Map<Integer, List<String>> duplicacoes = tabelaSimbolos.getDuplicacoesDetectadas();

        for (Map.Entry<Integer, List<String>> entry : duplicacoes.entrySet()) {
            List<String> mensagensErro = entry.getValue();

            for (String mensagem : mensagensErro) {
                errosSemanticos.add("Erro: " + mensagem);
            }
        }
    }

    // verificar se a variável foi declarada no código
    public void verificarVariaveisUtilizadas() {
        Map<String, List<Integer>> variaveisUsadas = tabelaSimbolos.getVariaveisUtilizadas();

        for (Map.Entry<String, List<Integer>> entry : variaveisUsadas.entrySet()) {
            String identificador = entry.getKey();
            List<Integer> linhas = entry.getValue();

            // ignorar valores literais
            if (identificador.matches("\\d+") ||
                    identificador.equals("true") ||
                    identificador.equals("false") ||
                    identificador.equals("return")) {
                continue;
            }

            if (tabelaSimbolos.buscarSimbolo(identificador) == null) {
                for (int linha : linhas) {
                    errosSemanticos.add("Erro: Variável '" + identificador +
                            "' utilizada na linha " + linha +
                            " não foi declarada.");
                }
            }
        }
    }

    public void verificarTiposCompativeis() {
        List<Simbolo> simbolos = tabelaSimbolos.getSimbolos();

        for (Simbolo simbolo : simbolos) {
            if (simbolo.getValor() != null) {
                String tipoVariavel = simbolo.getTipo();
                String valor = simbolo.getValor();
                String tipoExpressao = simbolo.getTipoExpressao();

                // verificações baseadas no tipo detectado da expressão
                if (tipoExpressao != null) {
                    if (!tipoExpressao.equals(tipoVariavel)) {
                        errosSemanticos.add("Erro: Incompatibilidade de tipos na linha " +
                                simbolo.getLinha() + ". Não é possível atribuir " +
                                "um valor do tipo '" + tipoExpressao + "' a uma variável de tipo '" +
                                tipoVariavel + "'.");
                    }
                }

                else {
                    // verificar se o valor é um número
                    if (valor.matches("\\d+")) {
                        if (!tipoVariavel.equals("int")) {
                            errosSemanticos.add("Erro: Incompatibilidade de tipos na linha " +
                                    simbolo.getLinha() + ". Não é possível atribuir " +
                                    "um valor inteiro a uma variável de tipo '" + tipoVariavel + "'.");
                        }
                    }

                    // verificar se o valor é uma string (começa e termina com aspas)
                    else if (valor.startsWith("\"") && valor.endsWith("\"")) {
                        errosSemanticos.add("Erro: Incompatibilidade de tipos na linha " +
                                simbolo.getLinha() + ". Não é possível atribuir " +
                                "um valor string a uma variável de tipo '" + tipoVariavel + "'.");
                    }

                    // verificar se o valor é booleano
                    else if (valor.equals("true") || valor.equals("false")) {
                        if (!tipoVariavel.equals("bool")) {
                            errosSemanticos.add("Erro: Incompatibilidade de tipos na linha " +
                                    simbolo.getLinha() + ". Não é possível atribuir " +
                                    "um valor booleano a uma variável de tipo '" + tipoVariavel + "'.");
                        }
                    }
                }
            }
        }
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
